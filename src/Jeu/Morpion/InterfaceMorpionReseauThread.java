package Jeu.Morpion;

import Reseau.Client;
import Reseau.Serveur;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class InterfaceMorpionReseauThread {

    public static void main(String[] args) {

        ArrayList<Joueur> listeJoueurs = new ArrayList<Joueur>();
        Joueur JoueurClient = new Joueur("Client","pouet","X");
        Joueur JoueurServeur = new Joueur("Serveur","pouette","O");
        listeJoueurs.add(JoueurClient);
        listeJoueurs.add(JoueurServeur);
        Morpion morpion = new Morpion(listeJoueurs);



        System.out.println("Entrez votre choix: \n1=Jouer\n2=Regarder\n3=Héberger");
        Scanner choix = new Scanner(System.in);
        switch (choix.nextLine()){
            case "1":
                morpionCoteClient(JoueurClient, JoueurServeur, morpion);
                break;
            case "2":
                morpionCoteSpectateur(JoueurClient, JoueurServeur, morpion);
                break;
            case "3":
                if(morpion.getNbTour()<=0) {
                    morpionCoteServeur(JoueurClient, JoueurServeur, morpion);
                }
                break;
            default:
                System.out.println("Retape ton choix");
        }
    }

    public static void morpionCoteClient(Joueur joueurClient, Joueur joueurServeur, Morpion morpion) {
        Socket socket;//on essaye de se connecter a un serveur local
        try {

                saisirInfo(joueurClient);
                socket = Client.getSocket("localhost",2000);
                DataInputStream socketEntree = new DataInputStream (new BufferedInputStream(socket.getInputStream()));
                PrintStream socketSortie = new PrintStream ( new BufferedOutputStream(socket.getOutputStream()));

                pushInfoJoueur(joueurClient, socketSortie);

                System.out.println("Attente des infos du serveur...");
                pullInfoJoueur(joueurServeur, socketEntree);


            while(morpion.peutContinuerPartie()) {
                jouerTour(morpion);
                //On envoie un message puis on attend une réponse
                pushCoup(joueurClient, socketSortie);
                //test

                //on reçoit les données
                pullCoup(joueurServeur, morpion, socketEntree);
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Le client n'a pas pu se connecter");
        }
    }

    private static void jouerTour(Morpion morpion) {
        System.out.println(morpion);
        morpion.incrementerNbTour();
        morpion.jouer(0);
        morpion.incrementerNbTour();
        System.out.println(morpion);
    }

    public static void morpionCoteSpectateur(Joueur joueurServeur, Joueur joueurClient, Morpion morpion){
        Socket socket; //on essaye de se connecter a un serveur local
        try {
            socket = Client.getSocket("localhost",2001);
            DataInputStream socketEntree = new DataInputStream (new BufferedInputStream(socket.getInputStream()));

            pullGrille(morpion,socketEntree);
            pullInfoJoueur(joueurServeur,socketEntree);
            pullInfoJoueur(joueurClient,socketEntree);
            System.out.println("Vous observez une partie se jouant entre " + joueurServeur.getNom() + " et " + joueurClient.getNom());
            morpion.setNbTour(Integer.parseInt(Client.pull(socketEntree)));
            System.out.println("vous êtes au tour n°"+morpion.getNbTour());


            while(true) {
                System.out.println("attente du coup de " + joueurClient.getNom() + "...");
                pullCoup(joueurClient, morpion, socketEntree);
                morpion.incrementerNbTour();

                if (Client.pull(socketEntree).equals("FIN"))
                    break;
                System.out.println(morpion);

                System.out.println("attente du coup de " + joueurServeur.getNom() + "...");
                pullCoup(joueurServeur, morpion, socketEntree);
                morpion.incrementerNbTour();

                if (Client.pull(socketEntree).equals("FIN"))
                    break;
                System.out.println(morpion);
            }
            Client.pull(socketEntree);

            System.out.println("partie terminée");
            morpion.afficherGagnant();
            socket.close();
        } catch (IOException e) {
            System.err.println("Le spectateur n'a pas pu se connecter");
        }
    }

    public static void morpionCoteServeur(Joueur joueurServeur, Joueur joueurClient, Morpion morpion) {
        ///////////////////////////////////////////////////////Partie serveur\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        //Sinon on lance le serveur
        try {
            saisirInfo(joueurServeur);
            ThreadServeurEcouteSpectateur threadServeurEcouteSpectateur = new ThreadServeurEcouteSpectateur();
            Socket socketClient = Serveur.initialisationServeur();

            DataInputStream entreeServJoueur = new DataInputStream(new BufferedInputStream(socketClient.getInputStream()));
            PrintStream sortieServJoueur = new PrintStream(new BufferedOutputStream(socketClient.getOutputStream()));
            threadServeurEcouteSpectateur.start();

            Socket socketSpectateur = null;
            PrintStream sortieServSpec = null;
            threadServeurEcouteSpectateur.setS_service_spectateur(socketSpectateur);
            threadServeurEcouteSpectateur.setSortieServSpec(sortieServSpec);

            pullInfoJoueur(joueurClient, entreeServJoueur);
            while (joueurClient.getPion().equals(joueurServeur.getPion())) {
                System.out.println("Change de pion stp!");
                saisirInfo(joueurServeur);
            }

            pushInfoJoueur(joueurServeur, sortieServJoueur);

            boolean packetJoueurPushed = false;


            while (morpion.peutContinuerPartie()){
                pullCoup(joueurClient,morpion,entreeServJoueur);
                if (threadServeurEcouteSpectateur.isConnected()) {
                    if (!packetJoueurPushed) {
                        pushGrille(morpion, threadServeurEcouteSpectateur.getSortieServSpec());
                        pushInfoJoueurAuSpect(joueurServeur, joueurClient, threadServeurEcouteSpectateur.getSortieServSpec());
                        Client.push(String.valueOf(morpion.getNbTour()), threadServeurEcouteSpectateur.getSortieServSpec());
                    }
                    packetJoueurPushed = true;
                    pushCoup(joueurClient, threadServeurEcouteSpectateur.getSortieServSpec());

                    pushEtatPartieAuSpec(morpion, threadServeurEcouteSpectateur);
                }
                jouerTour(morpion);
                pushCoup(joueurServeur, sortieServJoueur);
                if (threadServeurEcouteSpectateur.isConnected() && packetJoueurPushed) {
                    pushCoup(joueurServeur, threadServeurEcouteSpectateur.getSortieServSpec());

                    pushEtatPartieAuSpec(morpion, threadServeurEcouteSpectateur);
                }

            }
            Client.push("1", threadServeurEcouteSpectateur.getSortieServSpec());
            socketClient.close();
            threadServeurEcouteSpectateur.getS_service_spectateur().close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void pushEtatPartieAuSpec(Morpion morpion, ThreadServeurEcouteSpectateur threadServeurEcouteSpectateur) {
        if (!morpion.peutContinuerPartie())
            Client.push("FIN", threadServeurEcouteSpectateur.getSortieServSpec());
        else
            Client.push("CONTINUE", threadServeurEcouteSpectateur.getSortieServSpec());
    }

    private static void pushGrille(Morpion morpion,PrintStream sortieServSpec){
        StringBuilder aEnvoyer = new StringBuilder();
        for (int i = 0; i < 9 ; i++) {
            aEnvoyer.append(morpion.getCaseGrilleDuMorpion(i));
        }
        Client.push(aEnvoyer.toString(),sortieServSpec);
    }
    private static void pullGrille(Morpion morpion,DataInputStream entreeSpec ){
        try {
            String grille = Client.pull(entreeSpec);
            for (int i = 0; i < 9; i++) {
                morpion.ajouterUnCoup(i, String.valueOf(grille.charAt(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void pushInfoJoueurAuSpect(Joueur joueurServeur, Joueur joueurClient, PrintStream sortieServSpec) {
        pushInfoJoueur(joueurServeur, sortieServSpec);
        pushInfoJoueur(joueurClient, sortieServSpec);
    }

    public static void pullCoup(Joueur adversaire, Morpion morpion, DataInputStream socketEntree) throws IOException {
        adversaire.setPositionJ(Integer.parseInt(Client.pull(socketEntree)));
        //Puis on met a jour le morpion
        morpion.ajouterUnCoup(adversaire.getPositionJ(), adversaire.getPion());

    }

    public static void pushCoup(Joueur j, PrintStream socketSortie) {
        Client.push(String.valueOf(j.getPositionJ()), socketSortie);
    }


    public static void saisirInfo(Joueur j) {
        Scanner saisieJoueur = new Scanner(System.in);
        saisirNomJoueur(saisieJoueur, j);
        saisirPion(saisieJoueur, j);
    }

    public static void saisirNomJoueur(Scanner saisie, Joueur j) {
        System.out.print("Ton nom : \n");
        String nomJoueur = saisie.nextLine();
        j.setNomJoueur(nomJoueur);
    }

    public static void saisirPion(Scanner saisie, Joueur j) {
        System.out.print("Ton pion : \n");
        String pion = saisie.nextLine();
        j.setPion(pion);
    }

    public static void pushInfoJoueur(Joueur j1, PrintStream socketSortie) {
        //envoie des donnees joueur
        String pack = j1.getNom()+" "+j1.getPion();
        Client.push(pack,socketSortie);
    }

    public static void pullInfoJoueur(Joueur adversaire, DataInputStream entreeServ) throws IOException {
        String packRecu = Client.pull(entreeServ);
        String[]infoAdversaire = packRecu.split(" ");
        adversaire.setNomJoueur(infoAdversaire[0]);
        adversaire.setPion(infoAdversaire[1]);
    }

}

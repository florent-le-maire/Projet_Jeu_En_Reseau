package Jeu.Morpion;

import Reseau.Client;
import Reseau.Serveur;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class InterfaceMorpionReseau {
//    public static void main(String[] args) {
//        Scanner saisieJoueur = new Scanner(System.in);
//        ArrayList<Joueur> listeJoueurs = new ArrayList<Joueur>();
//        Joueur j1 = new Joueur("Player1","pouet","X");
//        Joueur adversaire = new Joueur("Player2","pouette","O");
//        listeJoueurs.add(j1);
//        listeJoueurs.add(adversaire);
//        Morpion morpion = new Morpion(listeJoueurs);
//
//
//        saisirInfo(saisieJoueur, j1);
//        System.out.println("Entrer votre choix: \n1=Jouer\n2=Regarder\n3=Héberger");
//        Scanner choix = new Scanner(System.in);
//        switch (choix.nextLine()){
//            case "1":
//                morpionCoteClient(j1, adversaire, morpion);
//                break;
//            case "2":
//                morpionCoteSpectateur(j1, adversaire, morpion);
//                break;
//            case "3":
//                if(morpion.getNbTour()<=0) {
//                    morpionCoteServeur(j1, adversaire, morpion);
//                }
//                break;
//            default:
//                System.out.println("Retape ton choix");
//        }
//    }

    public static void morpionCoteClient(Joueur j1, Joueur adversaire, Morpion morpion) {
        Socket socket;//on se essaye de se connecter a un serveur local
        try {
                socket = Client.getSocket("iconya.fr",2000);
                DataInputStream socketEntree = new DataInputStream (new BufferedInputStream(socket.getInputStream()));
                PrintStream socketSortie = new PrintStream ( new BufferedOutputStream(socket.getOutputStream()));

                pushInfoJoueur(j1, socketSortie);

                pullInfoJoueur(adversaire, socketEntree);


            while(morpion.peutContinuerPartie()) {
                System.out.println(morpion);
                morpion.jouer(0);
                System.out.println(morpion);
                //On envoie un message puis on attend une réponse
                pushMorpion(j1, morpion, socketSortie);
                //test

                //on reçoit les données
                pullMorpion(adversaire, morpion, socketEntree);
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Le client n'a pas pu se connecter");
        }
    }

    public static void morpionCoteSpectateur(Joueur joueurServeur, Joueur joueurClient, Morpion morpion){
        Socket socket; //on se essaye de se connecter a un serveur local
        try {
            socket = Client.getSocket("localhost",2001);
            DataInputStream socketEntree = new DataInputStream (new BufferedInputStream(socket.getInputStream()));
            pullInfoJoueur(joueurServeur,socketEntree);

            pullInfoJoueur(joueurClient,socketEntree);

            while(morpion.peutContinuerPartie()) {
                pullMorpion(joueurClient,morpion,socketEntree);
                System.out.println(morpion);
                pullMorpion(joueurServeur,morpion,socketEntree);
                System.out.println(morpion);
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("Le client n'a pas pu se connecter");
        }
    }

    public static void morpionCoteServeur(Joueur j1, Joueur adversaire, Morpion morpion) {
        ///////////////////////////////////////////////////////Partie serveur\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        //Sinon on lance le serveur
        try {
            Socket s_service = Serveur.initialisationServeur();
            Socket s_service_spectateur = Serveur.initialisationSpectateur();
            DataInputStream entreeServ = new DataInputStream(new BufferedInputStream(s_service.getInputStream()));
            PrintStream sortieServ = new PrintStream(new BufferedOutputStream(s_service.getOutputStream()));
            PrintStream sortieServSpec = new PrintStream(new BufferedOutputStream(s_service_spectateur.getOutputStream()));

            pullInfoJoueur(adversaire, entreeServ);
            pushInfoJoueur(j1, sortieServ);
            pushInfoJoueur(j1, sortieServSpec);
            pushInfoJoueur(adversaire, sortieServSpec);

            while (morpion.peutContinuerPartie()) {
                pullMorpion(adversaire,morpion,entreeServ);
                pushMorpion(adversaire,morpion,sortieServSpec);
                System.out.println(morpion);
                morpion.jouer(0);
                System.out.println(morpion);
                pushMorpion(j1,morpion,sortieServ);
                pushMorpion(j1,morpion,sortieServSpec);

            }
            s_service.close();
            s_service_spectateur.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pullMorpion(Joueur adversaire, Morpion morpion, DataInputStream socketEntree) throws IOException {
        adversaire.setPositionJ(Integer.parseInt(Client.pull(socketEntree)));
        morpion.incrementerNbTour();
        //Puis on met a jour le morpion
        morpion.ajouterUnCoup(adversaire.getPositionJ(), adversaire.getPion());

    }

    public static void pushMorpion(Joueur j1, Morpion morpion, PrintStream socketSortie) {
        Client.push(String.valueOf(j1.getPositionJ()), socketSortie);
        morpion.incrementerNbTour();

    }


    public static void saisirInfo(Scanner saisieJoueur, Joueur j1) {
        saisirNomJoueur(saisieJoueur, j1);
        saisirPiont(saisieJoueur, j1);
    }

    public static void saisirNomJoueur(Scanner saisieJoueur, Joueur j1) {
        System.out.print("Ton nom : \n");
        String nomJoueur = saisieJoueur.nextLine();
        j1.setNomJoueur(nomJoueur);
    }

    public static void saisirPiont(Scanner saisieJoueur, Joueur j1) {
        System.out.print("Ton pion : \n");
        String piontJoueur = saisieJoueur.nextLine();
        j1.setPion(piontJoueur);
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

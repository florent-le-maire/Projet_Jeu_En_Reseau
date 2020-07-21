package Jeu.Morpion;

import Reseau.Serveur;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadServeurEcouteSpectateur extends Thread {
    private Socket s_service_spectateur;
    private PrintStream sortieServSpec;
    private Morpion morpion;
    private Joueur joueurClient;
    private Joueur joueurServeur;
    private boolean connected = false; //variable utilisée dans un ancien programme

    @Override
    public void run() {

            try {
                ServerSocket s_ecoute = new ServerSocket(2001);
                while (morpion.peutContinuerPartie()) {
                    Socket spectateur = s_ecoute.accept();
                    System.out.println("Connexion d'un nouveau spectateur...");
                    Thread t = new Thread(new ThreadGestionSpectateur(spectateur, morpion, joueurServeur, joueurClient));
                    t.start();
                    Thread.sleep(10);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
    }


    public void setMorpion(Morpion morpion) {
        this.morpion = morpion;
    }

    public void setJoueurClient(Joueur joueurClient) {
        this.joueurClient = joueurClient;
    }

    public void setJoueurServeur(Joueur joueurServeur) {
        this.joueurServeur = joueurServeur;
    }

    public void setS_service_spectateur(Socket s_service_spectateur) {
        this.s_service_spectateur = s_service_spectateur;
    }

    public void setSortieServSpec(PrintStream sortieServSpec) {
        this.sortieServSpec = sortieServSpec;
    }

    public Socket getS_service_spectateur() {
        return s_service_spectateur;
    }

    public PrintStream getSortieServSpec() {
        return sortieServSpec;
    }

    public boolean isConnected() {
        return connected;
    }

    public void run_spectateur_seul() {
        try {

            s_service_spectateur= Serveur.initialisationSpectateur();

            sortieServSpec = new PrintStream(new BufferedOutputStream(s_service_spectateur.getOutputStream()));

            connected = true;
            System.out.println("Le spectateur est connecté");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

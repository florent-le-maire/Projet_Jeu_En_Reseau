package testSocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
@SuppressWarnings("Duplicates")

public class Serveur {

    public static void lancementDuServ() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s_service = initialisationServeur();

                    // On crée 2 flots d'entrée et un de sortie
                    DataInputStream saisie = new DataInputStream(new BufferedInputStream(System.in));

                    // On crée un flot d'entrée et un de sortie
                    BufferedReader entree = new BufferedReader (new InputStreamReader ( s_service.getInputStream()));
                    PrintWriter sortie = new PrintWriter ( new OutputStreamWriter ( s_service.getOutputStream()));

                    //on va communiquer avec le client
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                communication(saisie, entree, sortie);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    // Le client s'est déconnecté
                    System.out.println("Fermeture de la connexion...");
                    // On ferme la socket de service
                    s_service.close();
                }
                catch (Exception e) {
                    return;
                }

            }
        });
        t.start();

    }

    public static Socket initialisationServeur() throws IOException {
        // On crée une socket serveur ( le port est passé en argument)
        ServerSocket s_ecoute = new ServerSocket(2000);
        System.out.println("Serveur démarré sur la socket d'écoute " + s_ecoute);
        // Attente d'une connexion
        Socket s_service = s_ecoute.accept();
        // Une connexion a été ouverte
        System.out.println("Ouverture de la connexion sur la socket de service " + s_service);
        return s_service;
    }

    private static void communication(DataInputStream saisie, BufferedReader entree, PrintWriter sortie) throws IOException {
        // On boucle sur l'echo
        while (true){
            // On lit une ligne en entrée
            System.out.println("On attend un message");
            String buff = entree.readLine();
            System.out.println("Texte reçue "+ buff);
            // On quitte si c'est égal à "FIN"
            if (buff.equals("FIN")) break;
            System.out.println("Texte ? ");
            buff = saisie.readLine();
            // On renvoie l'echo au client
            sortie.println(buff);
            sortie.flush();
        }
    }
    public static void serveurThread(){
        try {
//            TestThread[] threads = new TestThread[new TestThread("bonjour"),new TestThread("bonjour")];
//            for (int i = 0; i < 2; i++) {
//                Socket s_service = initialisationServeur();
//            }
            TestThread thread = new TestThread("bonjour");
            TestThread thread2 = new TestThread("bonjour");
            thread.start();
            thread2.start();

        }catch (Exception e){
            return;
        }
    }
}

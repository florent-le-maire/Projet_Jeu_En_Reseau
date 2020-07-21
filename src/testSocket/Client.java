package testSocket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
@SuppressWarnings("Duplicates")
public class Client {
    public static void connexion(){
        try {
            int port = 2000;
            String ip = "192.168.1.10";
            Socket s = getSocket(ip,port);
            // On crée 2 flots d'entrée et un de sortie
            DataInputStream saisie = new DataInputStream(new BufferedInputStream (System.in));
            DataInputStream entree = new DataInputStream (new BufferedInputStream (s.getInputStream()));
            PrintStream sortie = new PrintStream ( new BufferedOutputStream(s.getOutputStream()));

            communicationChat(saisie, entree, sortie);
            // On ferme la socket
            s.close();
        }
        catch (Exception e) {
            return;
        }

    }

    public static Socket getSocket(String ip,int port) throws IOException {
        // On crée un objet InetAdress sur  l'interface de loopback
        InetAddress adr = InetAddress.getByName(ip);
        // On crée une socket
        Socket s = new Socket (adr, port);
        System.out.println("Socket crée");
        return s;
    }


    private static void communicationChat(DataInputStream saisie, DataInputStream entree, PrintStream sortie) throws IOException {
        // On envoie du texte au serveur et on
        // affiche l'echo reçu
        while (true) {
            // Saisie du texte à envoyer au serveur
            System.out.println("Texte ? ");
            String buff = saisie.readLine();
            // Si on entre "FIN", on quitte
            if (buff.equals("FIN")) break;
            // On envoie le texte saisi au serveur
            envoyerDonnee(buff, sortie);
            // On affiche l'écho du serveur
            String buff2 = entree.readLine();
            System.out.println(buff2);
        }
    }
    public static String communicationServeur(String upload, DataInputStream entree, PrintStream sortie) throws IOException {

        // Saisie du texte à envoyer au serveur
        System.out.println("Texte ? ");
        if (finDeLaCommunication(upload)) return "FIN";
        envoyerDonnee(upload, sortie);
        return recevoirDonnee(entree);
    }

    public static String recevoirDonnee(DataInputStream entree) throws IOException {
        // On affiche l'écho du serveur
        String download = entree.readLine();
        return download;
    }

    public static void envoyerDonnee(String upload, PrintStream sortie) {
        // On envoie le texte saisi au serveur
        sortie.println(upload);
        sortie.flush();
    }

    private static boolean finDeLaCommunication(String upload) {
        // Si on entre "FIN", on quitte
        if (upload.equals("FIN")) return true;
        return false;
    }
}

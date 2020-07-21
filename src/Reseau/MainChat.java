package Reseau;

public class MainChat {
    //vestige du chat
    public static void main(String[] args) {
        Client.connexion();
        System.out.println("Impossible de se connecter au serveur");
        Serveur.lancementDuServ();
    }
}

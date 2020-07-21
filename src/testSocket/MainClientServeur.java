package testSocket;

public class MainClientServeur {
    public static void main(String[] args) {
        for(int i = 0; i < 1; i++){
            Thread t = new Thread(new TestThread(Integer.toString(i)));
            t.start();
        }
        System.out.println("Impossible de se connecter au serveur");
        Serveur.lancementDuServ();

    }
}

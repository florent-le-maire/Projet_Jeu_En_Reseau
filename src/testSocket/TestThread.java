package testSocket;

public class TestThread extends Thread {
    public TestThread(String name){
        super(name);
    }
    public void run(){
        Client.connexion();
    }

}

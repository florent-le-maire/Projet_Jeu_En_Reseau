package Jeu.Morpion;

import java.util.ArrayList;
import java.util.Scanner;

public class JeuMorpion {
    public void lancementDuJeu() {
        ArrayList<Joueur> listDesJoueurs = new ArrayList<Joueur>();
        Joueur j1 = new Joueur("Erwan","pouet","X");
        Joueur j2 = new Joueur("Florent","pouette","O");
        listDesJoueurs.add(j1);
        listDesJoueurs.add(j2);
        Morpion m1 = new Morpion(listDesJoueurs);
        jeuEnCours(listDesJoueurs, m1);

    }



    public String jeuMorpionEnReseau(){

        return "";
    }

    private void jeuEnCours(ArrayList<Joueur> listDesJoueurs, Morpion morpion) {
        int nbTour = 0;
        while (morpion.peutContinuerPartie()){
            if(morpion.estCoupValide(listDesJoueurs.get(nbTour%2)));
            morpion.jouer(nbTour % 2);
            System.out.println(morpion);
            nbTour++;
        }
    }

    public static int saisirEntier () {

        Scanner clavier=new Scanner(System.in);
        String s = clavier.nextLine(); //int lu = clavier.nextInt();
        int lu=456;
        try{
            lu = Integer.parseInt(s);
        }
        catch(NumberFormatException ex){
            System.out.println("Ce n'est pas un entier valide");
        }
        return lu;
    }
}

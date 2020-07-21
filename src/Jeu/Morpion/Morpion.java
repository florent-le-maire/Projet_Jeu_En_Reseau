package Jeu.Morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Morpion {
    private final ArrayList<Joueur> listeDesJoueurs;


    private final String[] grilleDuMorpion;
    private int nbTour;

    private static final int[][] casGagnants = new int[][] {{ 0, 1, 2 }, { 3, 4, 5 },{ 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };

    public Morpion(ArrayList<Joueur> listeDesJoueurs) {
        this.listeDesJoueurs = listeDesJoueurs;

        grilleDuMorpion = new String[9];
        Arrays.fill(grilleDuMorpion, " ");
        nbTour = 0;
    }
    public void incrementerNbTour(){
        nbTour++;
    }

    public void jouer(Joueur joueur){
        if (peutContinuerPartie()) {

            do {
                System.out.println("C'est a toi de jouer "+ joueur.getNom());
                joueur.setPositionJ(saisirEntier() - 1);
            } while (!estCoupValide(joueur));
            grilleDuMorpion[joueur.getPositionJ()] = joueur.getPion();
        }

    }
    public void jouer(int idjoueur){
        if (peutContinuerPartie()) {
            Joueur joueur = listeDesJoueurs.get(idjoueur);
            do {
                System.out.println("C'est a toi de jouer \n");
                joueur.setPositionJ(saisirEntier() - 1);
            } while (!estCoupValide(joueur));
            grilleDuMorpion[joueur.getPositionJ()] = joueur.getPion();
        }

    }
    public void ajouterUnCoup(int coup, String pion){
        grilleDuMorpion[coup] = pion;
    }

    public boolean estCoupValide(Joueur joueur){

        if (joueur.getPositionJ() < 0 || joueur.getPositionJ()>8) {
            System.out.println("mauvaise case");
            return false;
        }
        if (grilleDuMorpion[joueur.getPositionJ()] == " ") {
            return true;
        }
        else {
            System.out.println("Ooohhhh tu sais jouer  ???");
            return false;
        }
    }

    public Joueur aQuiLeTour(){
        for (Joueur joueur: listeDesJoueurs
             ) {
            if (joueur.doitJouer())
            {
                return joueur;
            }
            else {
                System.err.println("problème, ce n'est le tour de personne...");
                return null;
            }

        }
        System.err.println("il n'y a pas de joueur...");
        return null;
    }
    public void setNbTour(int nbTour) {
        this.nbTour = nbTour;
    }
    //TODO : peutContinuerPartie ne doit pas afficher...
    public boolean peutContinuerPartie(){
        boolean partieGagnee = partieGagnee();
        if (!partieGagnee && nbTour<9)
            return true;

        if (nbTour>=9 && !partieGagnee) {
            System.out.print("Egalité : Fin de la partie");
            return false;
        }

        System.out.println("Fin de la partie");
        afficherGagnant();
        return false;


    }

    protected void afficherGagnant() {
        for (Joueur joueur : listeDesJoueurs) {
            if (joueur.isGagner())
                System.out.println("Le gagnant est : " + joueur);
        }
    }

    private boolean partieGagnee() {
        String piont;
        if (nbTour<3)
            return false;
        int cptDePoint;// on init un cpt de point
        for (int i = 0; i < casGagnants.length; i++) { //on vien parcourir le tableau des cas gagnants
            cptDePoint = 0;
            piont = grilleDuMorpion[casGagnants[i][0]];//On initialise le piont à comparer
            if (piont != " "){
                for (int j = 0; j < casGagnants[0].length ; j++) { //on parcourt les 3 positions des pionts gagnants
                   if(grilleDuMorpion[casGagnants[i][j]] == piont){ // on compte le nombre de fois que le joueur est positionné
                       cptDePoint ++;                              // comme indiqué
                   }
                }
            }
            if (cptDePoint >= 3) // s'il est positionné 3 fois comme un cas gagnant alors C GAGNE
            {
                for (Joueur listeDesJoueur : listeDesJoueurs) {
                    if (listeDesJoueur.getPion()== piont ) {
                        listeDesJoueur.setGagner();
                    }
                }
                return true;
            }

        }
        return false;
    }
    public static int saisirEntier () {

        Scanner clavier=new Scanner(System.in);
        String s = clavier.nextLine(); //int lu = clavier.nextInt();
        int lu=456;
        try{
            lu = Integer.parseInt(s);
        }
        catch(NumberFormatException ex){
            System.err.println("Ce n'est pas un entier valide");
        }
        return lu;
    }

    public int getNbTour() {
        return nbTour;
    }

    public String getCaseGrilleDuMorpion(int i) {
        return grilleDuMorpion[i];
    }


    @Override
    public String toString() {
        StringBuilder affichage = new StringBuilder("Tour n°" + nbTour + "\n Ton profil : " + listeDesJoueurs.get(0) + "\n Adversaire : " + listeDesJoueurs.get(1) + "\n");
        for (int i = 7; i > 0  ; i-=3) {
            for (int j = 0; j < grilleDuMorpion.length/3 ; j++) {
                affichage.append("|").append(grilleDuMorpion[i + j - 1]);
            }
            affichage.append("|\n");
        }
        return affichage.toString();
    }
}

package diceForge;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe carte peut être utilisé pour les cartes ne donnant que des points de gloire
 * Sinon il faut créer / utiliser une classe dérivée de celle ci
 * Nom des cartes:
 * Coffre ; Herbes folles ; Ancien ; Marteau ; Ours ; Sanglier ; Biche ; Satyres
 * Hibou ; Minautore ; Bateau celeste ; Bouclier ; Cerberes ; Passeur ; Casque d invisibilite
 * Gorgone ; Triton ; Abysse
 */
public class Carte {
    private Ressource[] cout;
    private int nbrPointGloire;
    private String nom;

    public Carte(Ressource[] cout, int nbrPointGloire, String nom){
        if (cout.length <= 0)
            throw new DiceForgeException("Carte","Une carte doit couter quelque chose. Cout donné : "+cout);
        this.cout = cout;
        this.nbrPointGloire = nbrPointGloire;
        this.nom = nom;
    }

    /**
     * Pour cloner
     * Il faut l'override dans chaque classe fille
     * @return un clone de la carte en question
     */
    public Carte clone(){
        return new Carte(cout, nbrPointGloire, nom);
    }

    public void effetDirect(Joueur acheteur){
        if (nom.equals("Coffre")){
            acheteur.augmenterMaxOr(4);
            acheteur.augmenterMaxSoleil(3);
            acheteur.augmenterMaxLune(3);
        }
        else if (nom.equals("Herbes folles")){
            acheteur.ajouterLune(3);
            acheteur.ajouterOr(3);
        }
        else if (nom.equals("Ancien"))
            acheteur.ajouterRenfort(Joueur.Renfort.ANCIEN);
        else if (nom.equals("Biche"))
            acheteur.ajouterRenfort(Joueur.Renfort.BICHE);
        else if (nom.equals("Hibou"))
            acheteur.ajouterRenfort(Joueur.Renfort.HIBOU);
    }

    Ressource[] getCout() {
        return cout;
    }

    public int getNbrPointGloire() {
        return nbrPointGloire;
    }

    String getNom() { return nom; }

    boolean equals(Carte carte){
        if (carte == null)
            return false;
        return (nom.equals(carte.getNom()));
    }
    public String toString(){
        return nom;
    }

}

package diceForge;

/**
 * La classe carte peut être utilisé pour les cartes ne donnant que des points de gloire
 * Sinon il faut créer / utiliser une classe dérivée de celle ci
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

    public Ressource[] getCout() {
        return cout;
    }

    public int getNbrPointGloire() {
        return nbrPointGloire;
    }

    public String getNom() { return nom; }

    public boolean equals(Carte carte){
        if (carte == null)
            return false;
        return (nom.equals(carte.getNom()));
    }
    public String toString(){
        return nom;
    }

}

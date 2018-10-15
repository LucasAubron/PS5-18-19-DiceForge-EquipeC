package diceForge;

/**
 * La classe carte peut être utilisé pour les cartes ne donnant que des points de gloire
 * Sinon il faut créer / utiliser une classe dérivée de celle ci
 */
public class Carte {
    private Ressource[] cout;
    private int nbrPointGloire;

    public Carte(Ressource[] cout, int nbrPointGloire){
        if (cout.length <= 0)
            throw new DiceForgeException("Une carte doit couter quelque chose. Cout donné : "+cout);
        this.cout = cout;
        this.nbrPointGloire = nbrPointGloire;
    }

    public Ressource[] getCout() {
        return cout;
    }

    public int getNbrPointGloire() {
        return nbrPointGloire;
    }

    /**
     * TODO A CHANGER APRES LA VERSION MINIMALE SINON IL VA Y AVOIR DE GROS PROBLEME
     */
    public boolean equals(Carte carte){
        if (carte == null)
            return false;
        return (nbrPointGloire == carte.getNbrPointGloire());
    }
}

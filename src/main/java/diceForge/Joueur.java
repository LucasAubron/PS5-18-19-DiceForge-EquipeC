package diceForge;

public class Joueur {
    /**
     * Classe joueur. Ici on utilise plus d'objet pour les ressources, mais des variables distinctes.
     * Bien entendu ça peut changer. Pourquoi faire ça :
     * Parce la plupart des choses que l'on achete ne coute que d'une ressource, donc je pense
     * qu'avoir un unique tableau de ressource compliquerait les choses.
     * La classe ne doit contenir AUCUN élément d'un bot, la classe bot (il y en aura plusieurs) sera une classe à part.
     * Ainsi elle doit permettre d'avoir une grande communication avec l'extérieur
     */
    private int or;
    private int soleil;
    private int lune;
    private int pointDeGloire;

    public Joueur(int nbrOr, int nbrSoleil, int nbrLune){
        if (nbrOr < 2 || nbrOr > 7)
            throw new RuntimeException("Le nombre d'or est invalide. Min : 2, max : 7, actuel : "+nbrOr);
        or = nbrOr;
        if (nbrSoleil < 0 || nbrSoleil > 2)
            throw new RuntimeException("Le nombre de soleil est invalide. Min : 0, max : 2, actuel : "+nbrSoleil);
        soleil = nbrSoleil;
        if (nbrLune < 0 || nbrLune > 2)
            throw new RuntimeException("Le nombre de lune est invalide. Min : 0, max : 2, actuel : "+nbrLune);
        lune = nbrLune;
    }

    public int getOr() {
        return or;
    }

    public int getSoleil() {
        return soleil;
    }

    public int getLune() {
        return lune;
    }

    public int getPointDeGloire() {
        return pointDeGloire;
    }
}

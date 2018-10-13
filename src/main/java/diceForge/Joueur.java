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
    private int maxOr;
    private int soleil;
    private int maxSoleil;
    private int lune;
    private int maxLune;
    private int pointDeGloire;
    private De[] des;

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
        des = new De[]{new De(new Face[]{new Face(new Ressource[][]{{new Or()}}),
                new Face(new Ressource[][]{{new Soleil()}}),
                new Face(new Ressource[][]{{new PointDeGloire()}})})};//ON VA TOUS MOURRRRRIIIIRRR
    }

    public int getOr() {return or;}

    public void ajouterOr (int quantite){or = (or + quantite > maxOr) ? maxOr : or + quantite;}

    public int getSoleil() {return soleil;}

    public void ajouterSoleil(int quantite) {soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;}

    public int getLune() {return lune;}

    public void ajouterLune(int quantite) {lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;}

    public int getPointDeGloire() {return pointDeGloire;}

    public void lancerLesDes(){
        /**
         * C'est à partir d'ice qu'on lance les des, et que les problèmes arrivent...
         * Cette version ne marche que pour la version minimale, il faudra peut etre tout refaire /!\
         */
        for (De de:des){
            Face face = de.lancerLeDe();
            for (Ressource ressource:face.getRessource()[0]){
                if (ressource instanceof Or)
                    ajouterOr(1);
                else if (ressource instanceof Soleil)
                    ajouterSoleil(1);
                else if (ressource instanceof PointDeGloire)
                    ++pointDeGloire;
            }
        }
    }
}

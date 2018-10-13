package diceForge;

public class Joueur {
    /**
     * Classe joueur. Ici on utilise plus d'objet pour les ressources, mais des variables distinctes.
     * Bien entendu ça peut changer. Pourquoi faire ça :
     * Parce la plupart des choses que l'on achete ne coute que d'une ressource, donc je pense
     * qu'avoir un unique tableau de ressource compliquerait les choses.
     * La classe ne doit contenir AUCUN élément d'un bot, la classe bot (il y en aura plusieurs) sera une classe à part.
     * Ainsi elle doit permettre d'avoir une grande communication avec l'extérieur
     * Chaque joueur possède un identifiant, allant de 0 à 3 (s'il y a 4 joueurs, sinon moins)
     * qui permet d'identifier le joueur par rapport au autre (un peu comme dans une base de donnée).
     */
    private int or;
    private int maxOr = 12;
    private int soleil;
    private int maxSoleil = 6;
    private int lune;
    private int maxLune = 6;
    private int pointDeGloire;
    private int identifiant;
    private De[] des;
    private Face premierDeFaceCourante;
    private Face deuxiemeDeFaceCourante;

    public Joueur(int nbrOr, int nbrSoleil, int nbrLune, int indentifiant){
        if (nbrOr < 2 || nbrOr > 7)
            throw new RuntimeException("Le nombre d'or est invalide. Min : 2, max : 7, actuel : "+nbrOr);
        or = nbrOr;
        if (nbrSoleil < 0 || nbrSoleil > 2)
            throw new RuntimeException("Le nombre de soleil est invalide. Min : 0, max : 2, actuel : "+nbrSoleil);
        soleil = nbrSoleil;
        if (nbrLune < 0 || nbrLune > 2)
            throw new RuntimeException("Le nombre de lune est invalide. Min : 0, max : 2, actuel : "+nbrLune);
        lune = nbrLune;
        if (identifiant < 0 || identifiant > 3)
            throw new RuntimeException("L'identifiant est invalide. Min : 0, max : 3, actuel : "+identifiant);
        this.identifiant = indentifiant;
        des = new De[]{new De(new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Soleil(1)}}),
                new Face(new Ressource[][]{{new PointDeGloire(1)}})})};//ON VA TOUS MOURRRRRIIIIRRR
    }

    public int getOr() {return or;}

    public void ajouterOr (int quantite){or = (or + quantite > maxOr) ? maxOr : or + quantite;}

    public int getSoleil() {return soleil;}

    public void ajouterSoleil(int quantite) {soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;}

    public int getLune() {return lune;}

    public void ajouterLune(int quantite) {lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;}

    public int getPointDeGloire() {return pointDeGloire;}

    public int getIdentifiant() {return identifiant;}

    public void lancerLesDes(){
        /**
         * C'est à partir d'ice qu'on lance les des, et que les problèmes arrivent...
         * Cette version ne marche que pour la version minimale, il faudra peut etre tout refaire /!\
         */
        for (De de:des){
            Face face = de.lancerLeDe();
            if (de == des[0])
                this.premierDeFaceCourante = face;
            else
                this.deuxiemeDeFaceCourante = face;
            for (Ressource ressource:face.getRessource()[0]){
                if (ressource instanceof Or)
                    ajouterOr(ressource.getQuantite());
                else if (ressource instanceof Soleil)
                    ajouterSoleil(ressource.getQuantite());
                else if (ressource instanceof PointDeGloire)
                    pointDeGloire += ressource.getQuantite();
            }
        }
    }

    public void printRessourcesEtDes(){
        System.out.println("Or: " + or + "\t\t\t\t1er Dé: " + premierDeFaceCourante.toString());
        System.out.println("Soleil: " + soleil + "\t\t\t\t2ème Dé: not implemented in Minimal Product" ); //+ deuxiemeDeFaceCourante.toString()
        System.out.println("PointDeGloire: " + pointDeGloire);
    }

    //public void acheterExploit(Carte carte, )
}

package diceForge.Faces;

import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;


/**
 * Cette classe représente une face d'un dé, qu'elle soit dans un bassin ou sur un dé
 * Une face si elle est simple ne prends en argument qu'une ressource.
 * Si elle est a choix ou est une face addition elle prends en argument
 * une enum qui indique son type et une liste de ressoruces.
 * Sinon on a juste le type lorsqu'il s'agit d'une face spéciale achetable par un exploit.
 */
public class Face {

    //Attributs ------------------------------------------------------------------------------------------------

    private Ressource ressource; // pour les faces simples et bouclier
    private Ressource[] ressources; // pour les faces a choix, addition et sanglier
    public enum typeFace{SIMPLE, CHOIX, ADDITION, BOUCLIER, X3, MIROIR, SANGLIER, VOILECELESTE;};
    private typeFace type;

    //Constructeurs ---------------------------------------------------------------------------------------------

    public Face(Ressource ressource) {//face simple
        this.ressource = ressource;
        this.ressources = new Ressource[]{ressource}; //En soit ne sert à rien mais permet lors d'une recherche de
        this.type = typeFace.SIMPLE;                  // parcourir ce tableau sans check si c'est une face simple ou non
    }

    public Face(typeFace typeF, Ressource[] ressources) {//faces a choix ou face addition
        this.ressources = ressources;
        this.type = typeF;
    }

    public Face(typeFace typeF) { // faces a effet (sanglier, bouclier, X3, miroir, voiles celeste)
        this.type = typeF;
        if (type == typeFace.SANGLIER)
            ressources = new Ressource[]{
                    new Ressource(1, Ressource.type.SOLEIL),
                    new Ressource(1, Ressource.type.LUNE)};
    }

    //Méthodes --------------------------------------------------------------------------------------------------

    public typeFace getTypeFace() { return type; }

    public Ressource[] getRessources() { return ressources; }//face a choix et face addition

    public Ressource getRessource() { return ressource; }//face simple

    public boolean faitGagnerUneRessource(){
        return type != typeFace.VOILECELESTE && type != typeFace.X3;
    }

    public boolean estFaceAChoix(){ //on le fait pour ce type de face et pas les autres car on les rencontre souvent dans le code
        return type == typeFace.CHOIX || type == typeFace.SANGLIER || type == typeFace.MIROIR;
    }

    public boolean estUneFaceAyantBesoinDuDeuxiemeDe(){
        // Le miroir en soi n'a pas besoin
        // du résultat du deuxième dé, mais
        // face X3 ou bouclier, alors ça devient le cas
        return type == typeFace.BOUCLIER || type == typeFace.X3 || type == typeFace.MIROIR;
    }

    /**
     * A override pour les faces a effet
     */
    public void effetActif(Joueur joueur){}

    @Override
    public String toString() {
        StringBuilder affichage = new StringBuilder();
        String separateur = "";
        if (type == typeFace.ADDITION)
            separateur += " + ";
        if (type == typeFace.CHOIX)
            separateur += " ou ";
        for (Ressource ressource: ressources)
            affichage.append(ressource).append(separateur);
        if (type == typeFace.ADDITION)
            affichage = new StringBuilder(affichage.substring(0, affichage.length() - 2));//On supprime "+ "
        if (type == typeFace.CHOIX)
            affichage = new StringBuilder(affichage.substring(0, affichage.length() - 3));//On supprime "ou "
        return affichage + " ";
    }
}

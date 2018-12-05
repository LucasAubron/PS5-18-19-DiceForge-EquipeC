package diceForge;

import java.util.List;


/**
 * Cette classe représente une face d'un dé, qu'elle soit dans un bassin ou sur un dé
 * Une face si elle est simple ne prends en argument qu'une ressource/
 * Si elle est a choix ou est une face addition elle prends en argument
 * une enum qui indique son type et une liste de ressoruces.
 * Sinon on a juste le type lorsqu'il s'agit d'une face spéciale achetable par exploit.
 */
public class Face {

    private Ressource ressource; // pour les faces simples
    private Ressource[] ressources; // pour les faces a choix et addition
    private typeFace type;
    public enum typeFace{SIMPLE, CHOIX, ADDITION, BOUCLIER, X3, MIROIR, SANGLIER, VOILECELESTE};

    Face(Ressource ressource) {//face simple
        this.ressource = ressource;
        this.type = typeFace.SIMPLE;
    }

    Face(typeFace typeF,Ressource[] ressources) {//faces a choix ou face addition
        this.ressources = ressources;
        this.type = typeF;
    }

    Face(typeFace typeF) { // faces a effet (sanglier, bouclier, X3, miroir, voiles celeste)
        this.type = typeF;
        if (type == typeFace.SANGLIER)
            ressources = new Ressource[]{
                    new Ressource(1, Ressource.type.SOLEIL),
                    new Ressource(1, Ressource.type.LUNE)};
    }

    public Ressource[] getRessources() { return ressources; }//face a choix et face addition
    public Ressource getRessource() { return ressource; }//face simple
    public typeFace getTypeFace() { return type; }

    public boolean faitGagnerUneRessource(){
        if (type != typeFace.VOILECELESTE && type != typeFace.X3)
            return true;
        return false;
    }
    public boolean estFaceAChoix(){
        if (type == typeFace.CHOIX || type == typeFace.SANGLIER || type == typeFace.MIROIR)
            return true;
        return false;
    }

    /**
     * Méthode appeler à chaque fois que l'on tombe sur une face,
     * a Override si la face est une face a effet spécial (X3, miroir, bouclier, sanglier)
     * @param joueur
     */
    void effetActif(Joueur joueur){
    }

    @Override
    public String toString() {
        String affichage = "";
        /*
        for (Ressource[] ressources:ressource){
            if (!affichage.isEmpty()) {
                affichage = affichage.substring(0, affichage.length()-2);//On supprime " +"
                affichage += "ou ";
            }
            for (Ressource x:ressources){
                if (x != null) {
                    affichage += x.getQuantite();
                    affichage += x + " + ";
                }
            }
        }
        if (!affichage.isEmpty())
            affichage = affichage.substring(0, affichage.length()-2);//On supprime " +"
        */
        return affichage;
    }
}

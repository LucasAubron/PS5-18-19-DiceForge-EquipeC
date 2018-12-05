package diceForge;

import java.util.List;

/**
 * Cette classe représente une face d'un dé, qu'elle soit dans un bassin ou sur un dé
 * On utilise un tableau à 2 dimensions pour stocker les ressources. Pourquoi :
 * Si la case n'offre aucun choix (pas de ? sur la face), alors la liste principale est de taille 1,
 * les ressources contenues sont donc dans ressource[0]
 * Si il y a plusieurs choix, alors chaque choix est représenté par une liste.
 * Le premier choix est dans ressource[0], le deuxième dans ressource[1]...
 * N'hésitez pas à refaire ce pavé si vous avez compris et pouvez l'expliquer plus clairement.
 * Non je trouve que c'est plutot bien expliqué bien joué à toi Gabi.
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
    }

    public Ressource[] getRessources() { return ressources; }//face a choix et face addition
    public Ressource getRessource() { return ressource; }//face simple
    public typeFace getTypeFace() { return type; }
    public boolean naPasDeffet(){
        if (type == typeFace.SIMPLE || type == typeFace.CHOIX || type == typeFace.ADDITION)
            return true;
        return false;
    }
    public boolean estFaceAChoix(){
        if (type == typeFace.CHOIX || type == typeFace.SANGLIER)
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

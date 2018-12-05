package diceForge;

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
    private Ressource[][] ressource;
    Face(Ressource[][] ressource) {
        this.ressource = ressource;
    }

    public Ressource[][] getRessource() {
        return ressource;
    }

    /**
     * Méthode appeler à chaque fois que l'on tombe sur une face,
     * a Override si la face est une face a effet spécial (X3, miroir, bouclier, sanglier)
     * @param joueur
     */
    void effetActif(Joueur joueur){
    }

    public boolean estAChoixMultiple(){
        if (ressource.length >1)
            return true;
        return false;
    }

    public boolean estFaceAddition(){
        if (ressource[0].length >1)
            return true;
        return false;
    }

    @Override
    public String toString() {
        String affichage = "";
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
        return affichage;
    }
}

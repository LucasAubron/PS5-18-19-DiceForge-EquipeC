package diceForge;

/**
 * Cette classe représente une face d'un dé, qu'elle soit dans un bassin ou sur un dé
 * On utilise un tableau à 2 dimensions pour stocker les ressources. Pourquoi :
 * Si la case n'offre aucun choix (pas de ? sur la face), alors la liste principale est de taille 1,
 * les ressources contenues sont donc dans ressource[0]
 * Si il y a plusieurs choix, alors chaque choix est représenté par une liste.
 * Le premier choix est dans ressource[0], le deuxième dans ressource[1]...
 * N'hésitez pas à refaire ce pavé si vous avez compris et pouvez l'expliquer plus clairement.
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
     * Méthode appelé à chaque fois que l'on tombe sur cette face,
     * a Override si il se passe quelque chose dans ce cas la
     * @param joueur
     */
    void effetActif(Joueur joueur){
    }

    @Override
    public String toString() {
        String affichage = "";
        for (Ressource[] ressources:ressource){
            if (!affichage.isEmpty())
                affichage += "ou ";
            for (Ressource x:ressources){
                if (x != null) {
                    affichage += x.getQuantite();
                    affichage += x + " +";
                }
            }
        }
        if (!affichage.isEmpty())
            affichage = affichage.substring(0, affichage.length()-1);//On supprime le dernier +
        return affichage;
    }
}

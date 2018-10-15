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
    public Face(Ressource[][] ressource) {
        this.ressource = ressource;
    }

    public Ressource[][] getRessource() {
        return ressource;
    }

    @Override
    public String toString() {
        String res = "";
        if (ressource.length == 1){
            for (Ressource uneRess :ressource[0]) {
                res = res + " " + uneRess.getQuantite();
                if (uneRess instanceof Or)
                    res = res + " Or";
                else if (uneRess instanceof Soleil)
                    res = res + " Soleil";
                else if (uneRess instanceof PointDeGloire)
                    res = res + " PointDeGloire";
            }
        }
        return res;
    }
}

package diceForge;

/**
 * Les portails originels sont la ou les joueurs commence et la
 * ou ils viennent lorsqu'il sont chassés
 */
public class PortailsOriginels {
    private Joueur[] joueurs;

    public PortailsOriginels(Joueur[] joueurs){
        if (joueurs.length < 2 || joueurs.length > 4)
            throw new RuntimeException("Le nombre de joueur est invalide. Min : 2, max : 4, actuel : "+joueurs.length);
        this.joueurs = joueurs;
    }

    /**
     * Cette méthode permet de retirer un joueur d'un des portails originels
     * Elle est a utiliser à chaque fois qu'un joueur va sur une ile prendre une carte
     */
    public Joueur retirerJoueur(int numJoueur){
        if (numJoueur < 0 || numJoueur >= joueurs.length)
            throw new RuntimeException("Le numéro du joueur est invalide. Min : 0, max : "+(joueurs.length-1)+", actuel : "+numJoueur);
        if (joueurs[numJoueur] == null)
            throw new RuntimeException("Il n'y a pas de joueur à cette position. Position : "+numJoueur);
        Joueur x = joueurs[numJoueur];
        joueurs[numJoueur] = null;
        return x;
    }

    /**
     * Méthode à utiliser lorsqu'un joueur se fait chasser et revient aux portails originels
     * Si cette méthode renvoie l'erreur, il y a un gros problème quelque part
     */
    public void ajouterJoueur(Joueur joueur){
        if (joueurs[joueur.getIdentifiant()] != null)
            throw new RuntimeException("FATAL ERROR ! Il y a déjà une joueur situé dans son emplacement");
        joueurs[joueur.getIdentifiant()] = joueur;
    }

    public Joueur[] getJoueurs() {
        return joueurs;
    }
}

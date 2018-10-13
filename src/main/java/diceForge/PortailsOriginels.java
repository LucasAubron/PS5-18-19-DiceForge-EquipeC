package diceForge;

public class PortailsOriginels {
    /**
     * Les portails originels sont la ou les joueurs commence et la
     * ou ils viennent lorsqu'il sont chassés
     */
    private Joueur[] joueurs;
    public PortailsOriginels(Joueur[] joueurs){
        if (joueurs.length < 2 || joueurs.length > 4)
            throw new RuntimeException("Le nombre de joueur est invalide. Min : 2, max : 4, actuel : "+joueurs.length);
        this.joueurs = joueurs;
    }

    public Joueur retirerJoueur(int numJoueur){
        /**
         * Cette méthode permet de retirer un joueur d'un des portails originels
         * Elle est a utiliser à chaque fois qu'un joueur va sur une ile prendre une carte
         */
        if (numJoueur < 0 || numJoueur >= joueurs.length)
            throw new RuntimeException("Le numéro du joueur est invalide. Min : 0, max : "+(joueurs.length-1)+", actuel : "+numJoueur);
        if (joueurs[numJoueur] == null)
            throw new RuntimeException("Il n'y a pas de joueur à cette position. Position : "+numJoueur);
        Joueur x = joueurs[numJoueur];
        joueurs[numJoueur] = null;
        return x;
    }
}

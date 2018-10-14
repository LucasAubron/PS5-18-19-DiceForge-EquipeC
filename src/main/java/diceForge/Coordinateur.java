package diceForge;

/**
 * Le coordinateur s'occupe de faire tourner le jeu.
 * Il s'occupe du déroulement des manches, mais aussi de déplacer les joueurs.
 * A voir si c'est une bonne solution
 */
public class Coordinateur {
    Plateau plateau;
    private int nbrManche;

    public Coordinateur(Plateau plateau, int nbrManche){
        this.plateau = plateau;
        if (nbrManche < 4 || nbrManche > 10)
            throw new RuntimeException("Le nombre de manche est invalide. Min : 4, max : 10, actuel : "+nbrManche);
        this.nbrManche = nbrManche;
        for (int i = 1; i <= nbrManche; ++i){
            jouerManche();
        }
    }

    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    public void jouerManche(){
        for (Joueur joueur:plateau.getJoueur()){
            tour(joueur);
        }
    }

    public void tour(Joueur joueur){
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getPortail().getJoueurs().length == 2)//On passe par le portail pour de l'optimisation
                x.lancerLesDes();
            x.lancerLesDes();
        }
        //On ne peut pas aller plus loin avant de créer les bots
        //joueur.faitTonchoix......
    }
}

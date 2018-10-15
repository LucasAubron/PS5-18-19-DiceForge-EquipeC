package diceForge;

import java.awt.image.BandCombineOp;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
            jouerManche(i);
        }
    }

    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    public void jouerManche(int numeroManche){
        for (Joueur joueur:plateau.getJoueur()){
            tour(joueur, numeroManche);
        }
    }

    public void tour(Joueur joueur, int numeroManche){
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getPortail().getJoueurs().length == 2) {//On passe par le portail pour de l'optimisation
                x.lancerLesDes();
                if (plateau.modeVerbeux)
                    System.out.println(x.printRessourcesEtDes(numeroManche));
            }
            x.lancerLesDes();
            if (plateau.modeVerbeux)
                System.out.println(x.printRessourcesEtDes(numeroManche));
        }
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                ArrayList<Bassin> bassinAffordable = new ArrayList<>();//On créé la liste des bassins affordables
                for (Bassin bassin:plateau.getTemple().getSanctuaire()){
                    if (bassin.getCout() <= joueur.getOr())
                        bassinAffordable.add(bassin);//Puis on la remplie
                }
                joueur.choisirFaceAForger(bassinAffordable, numeroManche);//Puis on forge, le joueur s'occupe de retirer la face
                break;
            case EXPLOIT:

        }
    }
}

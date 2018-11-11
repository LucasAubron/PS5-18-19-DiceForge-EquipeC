package diceForge;

import java.util.List;

class Afficheur {

    private Plateau plateau;
    private String info = "";
    private boolean modeVerbeux;
    private Coordinateur coordinateur;
    private List joueurs;

    Afficheur(boolean modeVerbeux,Coordinateur coordinateur, Plateau plateau){
        this.plateau = plateau;
        this.modeVerbeux = modeVerbeux;
        this.coordinateur = coordinateur;
        this.joueurs = plateau.getPortail().getJoueurs();
        if (modeVerbeux)
            presentationModeVerbeux();
    }

    void presentationModeVerbeux(){
        info += "Cette partie oppose les bots (affichés dans l'odre de jeu): ";
        for (int i = 0; i < joueurs.size(); i++)
            info += joueurs.get(i) + ", ";
    }
    @Override
    public String toString(){
        return info;
    }
}

package diceForge;

import java.util.List;

class Afficheur {

    private Plateau plateau;
    private String info = "";
    private boolean modeVerbeux;
    private Coordinateur coordinateur;
    private List joueurs;

    Afficheur(boolean modeVerbeux, Coordinateur coordinateur) {
        this.modeVerbeux = modeVerbeux;
        this.coordinateur = coordinateur;
    }

    void setJoueurs(List joueurs){ this.joueurs = joueurs ;}

    void presentationModeVerbeux() {
        if (modeVerbeux) {
            info += "Cette partie oppose les bots (affichés dans l'odre de jeu): ";
            for (int i = 0; i < joueurs.size(); i++)
                info += joueurs.get(i) + ", ";
        }
    }

    void manche(int numManche) {
        info += "\n--------------------------------------------------\nManche " + numManche + "\n--------------------------------------------------\n";
    }

    void tour(Joueur joueur) {
        info += "\n\t\t--Tour du joueur n°" + joueur.getIdentifiant() + "--\n";
    }

    void ressourcesGagnees(Joueur joueur) {
        info += "\nLancés des dés du joueur n°" + joueur.getIdentifiant() + "\nRésultate dé n°1 : " + joueur.getDesFaceCourante()[0] + "\t||\tRésultat dé n°2 : " + joueur.getDesFaceCourante()[1] + "\n";
    }

    void desActuels(Joueur joueur) {
        info += "\nDés du joueur n°" + joueur.getIdentifiant() + "\nDé n°1: " + joueur.getDes()[0] + "\nDé n°2 : " + joueur.getDes()[1] + "\n";
    }

    void ressourcesDisponibles(Joueur joueur) {
    info +="\nLe joueur n°" + joueur.getIdentifiant() + " possède:\nOr: " + joueur.getOr() + "/" + joueur.getMaxOr() + "\nSoleil: " + joueur.getSoleil() + "/" + joueur.getMaxSoleil() + "\nLune: " + joueur.getLune() + "/" + joueur.getMaxLune() + "\n";
    }
    @Override
    public String toString(){
        return info;
    }
}

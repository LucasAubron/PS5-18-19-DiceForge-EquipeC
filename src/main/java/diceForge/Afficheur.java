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
        info += "\nLancés des dés du joueur n°" + joueur.getIdentifiant() + "\nRésultate dé n°1 : " + joueur.getDesFaceCourante()[0] + "\nRésultat dé n°2 : " + joueur.getDesFaceCourante()[1] + "\n";
    }

    void desActuels(Joueur joueur) {
        info += "\nDés du joueur n°" + joueur.getIdentifiant() + "\nDé n°1: " + joueur.getDes()[0] + "\nDé n°2 : " + joueur.getDes()[1] + "\n";
    }

    void ressourcesDisponibles(Joueur joueur) {
    info +="\nLe joueur n°" + joueur.getIdentifiant() + " possède:\nOr: " + joueur.getOr() + "/" + joueur.getMaxOr() + "\nSoleil: " + joueur.getSoleil() + "/" + joueur.getMaxSoleil() + "\nLune: " + joueur.getLune() + "/" + joueur.getMaxLune() + "\nPoints de gloire: " + joueur.getPointDeGloire() + "\n";
    }

    void choixFaceAChoix(Face faceAChoix, Ressource[] ressource){
        if (modeVerbeux)
            info += "\nLe joueur choisi la ressource " + ressource + "sur la face à choix " + faceAChoix;
    }

    void biche(int deChoisi, Face faceObtenue,Joueur joueur){
        if (modeVerbeux){
            deChoisi++;
            info += "\nLe joueur n°: " + joueur.getIdentifiant() +" active le renfort biche et lance le dé n°" + deChoisi + " .Il gagne " + faceObtenue;
            ressourcesDisponibles(joueur);
        }
    }

    void ancien(Joueur joueur){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort ancien, il consomme 3or pour gagner 4 points de gloire";
            ressourcesDisponibles(joueur);
        }
    }

    @Override
    public String toString(){
        return info;
    }
}

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
        if (modeVerbeux)
            info += "\n\n\n\n\n--------------------------------------------------\n\t\t\t\tMANCHE " + numManche + "\n--------------------------------------------------\n";
    }

    void tour(Joueur joueur) {
        if (modeVerbeux) {
            info += "\n";
            petitTrait();
            info += "\t\t--Tour du joueur n°" + joueur.getIdentifiant() + "--";
            petitTrait();
        }
    }

    void ressourcesGagnees(Joueur joueur) {
        if (modeVerbeux)
            info += "\n--Lancés des dés du joueur n°" + joueur.getIdentifiant() + "--\nRésultate dé n°1 : " + joueur.getDesFaceCourante()[0] + "\nRésultat dé n°2 : " + joueur.getDesFaceCourante()[1] + "\n";
    }

    void desActuels(Joueur joueur) {
        if (modeVerbeux)
            info += "\nDés du joueur n°" + joueur.getIdentifiant() + "\nDé n°1: " + joueur.getDes()[0] + "\nDé n°2 : " + joueur.getDes()[1] + "\n";
    }

    void ressourcesDisponibles(Joueur joueur) {
        if (modeVerbeux)
            info +="\nLe joueur n°" + joueur.getIdentifiant() + " possède:\nOr: " + joueur.getOr() + "/" + joueur.getMaxOr() + "\nSoleil: " + joueur.getSoleil() + "/" + joueur.getMaxSoleil() + "\nLune: " + joueur.getLune() + "/" + joueur.getMaxLune() + "\nPoints de gloire: " + joueur.getPointDeGloire() + "\n";
            if (!joueur.getMarteau().isEmpty())
                for (Marteau marteau: joueur.getMarteau())
                    info += marteau + "\n";
    }

    void recapJoueur(Joueur joueur){
        if (modeVerbeux){
            info += "\n\t\t|Information joueur|\n";
            ressourcesDisponibles(joueur);
            desActuels(joueur);
         }
    }
    void biche(int deChoisi, Face faceObtenue,Joueur joueur){
        if (modeVerbeux){
            deChoisi++;
            info += "\nLe joueur n°: " + joueur.getIdentifiant() +" active le renfort biche et lance le dé n°" + deChoisi + " . Il gagne " + faceObtenue + ".";
        }
    }

    void ancien(Joueur joueur){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort ancien, il consomme 3or pour gagner 4 points de gloire.";
        }
    }

    void hibou(Joueur joueur, Face face){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort hibou, il choisit " + face + ".";
        }
    }

    void choixFace(Joueur joueur, Face face, int choix){
        if (modeVerbeux){
            if (face.getRessource().length > 1) {
                info += "\nLe joueur n°" + joueur.getIdentifiant() + " choisi: " + face.getRessource()[choix][0].getQuantite() + face.getRessource()[choix][0] + "\n";
            }
        }
    }
    void presentationLancerDes(){
        if (modeVerbeux)
            info += "\n\t--Phase de lancer de dés--\n";
    }

    void presentationRenforts(){
        if (modeVerbeux)
            info += "\n\t--Phase de renforts--\n";
    }

    void grandTrait(){
        if (modeVerbeux)
            info += "\n--------------------------------------------------\n"; //pas nécessaire mais bon faut bien s'amuser
    }

    void petitTrait(){
        if (modeVerbeux)
            info += "\n-----------------------------------\n";
    }

    @Override
    public String toString(){
        return info;
    }
}

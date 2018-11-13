package diceForge;

import java.util.List;

public class Afficheur {

    private String info = "";
    private boolean modeVerbeux;
    private List joueurs;

    Afficheur(boolean modeVerbeux) {
        this.modeVerbeux = modeVerbeux;
    }

    void setJoueurs(List joueurs){ this.joueurs = joueurs ;}

    void presentationModeVerbeux() {
        if (modeVerbeux) {
            info += "\n\n\n\t\t-----------------------------------------------------------------------------------\n\t\t| Cette partie oppose les bots (affichés dans l'odre de jeu): ";
            for (int i = 0; i < joueurs.size(); i++)
                info += joueurs.get(i) + ", ";
            info += "|\n\t\t-----------------------------------------------------------------------------------";
        }
    }


    void manche(int numManche) {
        if (modeVerbeux)
            info += "\n\n\n\n\n-----------------------------------------------------------------------------------------------\n\t\t\t\t\t\t\t\t\t\tMANCHE " + numManche + "\n-----------------------------------------------------------------------------------------------\n";
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
            info += "\n--Lancés des dés du joueur n°" + joueur.getIdentifiant() + "--\nRésultate dé n°1 : " + joueur.getDesFaceCourante()[0] + "\nRésultat dé n°2 : " + joueur.getDesFaceCourante()[1] + "\n\n";
    }

    void desActuels(Joueur joueur) {
        if (modeVerbeux)
            info += "\nDés du joueur n°" + joueur.getIdentifiant() + ":\nDé n°1: " + joueur.getDes()[0] + "\nDé n°2 : " + joueur.getDes()[1] + "\n";
    }

    void ressourcesDisponibles(Joueur joueur) {
        if (modeVerbeux)
            info +="\nLe joueur n°" + joueur.getIdentifiant() + " possède:\nOr: " + joueur.getOr() + "/" + joueur.getMaxOr() + "\nSoleil: " + joueur.getSoleil() + "/" + joueur.getMaxSoleil() + "\nLune: " + joueur.getLune() + "/" + joueur.getMaxLune() + "\nPoints de gloire: " + joueur.getPointDeGloire() + "\n";
            if (!joueur.getMarteau().isEmpty())
                for (Marteau marteau: joueur.getMarteau())
                    info += marteau + "\n";
    }

    private void carteRenfortJetonDisponible(Joueur joueur){
        if (modeVerbeux)
            info += "\nCartes possédées: " + joueur.getCartes() + "\nRenforts disponibles: " + joueur.getRenforts() + "\nJetons Disponibles: " + joueur.getJetons() + "\n";
    }

    void recapJoueur(Joueur joueur){
        if (modeVerbeux){
            grandTrait();
            info += "\n\t\t|Information joueur|\n";
            ressourcesDisponibles(joueur);
            desActuels(joueur);
            carteRenfortJetonDisponible(joueur);

         }
    }

    void biche(int deChoisi, Face faceObtenue,Joueur joueur){
        if (modeVerbeux){
            deChoisi++;
            info += "\nLe joueur n°" + joueur.getIdentifiant() +" active le renfort biche et lance le dé n°" + deChoisi + ", il gagne " + faceObtenue;
        }
    }

    void ancien(Joueur joueur){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort ancien, il consomme 3 or pour gagner 4 points de gloire";
        }
    }

    void hibou(Joueur joueur, Face face){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort hibou, il choisit " + face;
        }
    }

    void choixFace(Joueur joueur, Face face, int choix){
        if (modeVerbeux){
            if (face.getRessource().length > 1) {
                info += "\nLe joueur n°" + joueur.getIdentifiant() + " choisit: " + face.getRessource()[choix][0].getQuantite() + face.getRessource()[choix][0] + "\n";
            }
        }
    }
    void presentationLancerDes(){
        if (modeVerbeux) {
            info += "\n\t--Phase de lancer de dés--\n";
        }
    }

    void presentationRenforts(Joueur joueur){
        if (modeVerbeux && !joueur.getRenforts().isEmpty()) {
            grandTrait();
            info += "\n\t--Phase de renforts--\n";
        }
    }

    void presentationAction(){
        if (modeVerbeux) {
            grandTrait();
            info += "\n\t\t--Action--\n";
        }
    }

    void chasse(Joueur chasseur, Joueur chasse){
        if (modeVerbeux && chasse != null)
            info += "\nLe joueur n°" + chasseur.getIdentifiant() + " chasse le joueur n°" + chasse.getIdentifiant() + ", ce dernier lance ses dés\n";
    }

    void ours(Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " gagne 3 points de gloire grace à sa carte ours\n";
    }

    void remplissageMarteau(Joueur joueur, int or, int quantité){
        if (modeVerbeux){
            int pointMarteau = quantité - or;
            if (pointMarteau !=0)
                info += "Le joueur n°" + joueur.getIdentifiant() + " donne " + pointMarteau + " or a son marteau\n";
        }
    }

    void actionForger(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide de forger-\n\n";
    }

    void forger(Joueur joueur, int numDe, Face faceForgee, Face faceRetiree, Bassin bassin){
        if (modeVerbeux){
            numDe++;
            info += "Le joueur n°" + joueur.getIdentifiant() + " paye " + bassin.getCout() + " or et forge la face " + faceForgee + "sur la face " + faceRetiree + "de son dé n°" + numDe + "\n";
        }
    }

    void actionExploit(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide d'accomplir un exploit-\n";
    }

    void achatCarte(Carte carte, Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " achète la carte " + carte + "\n";
    }

    void actionPasser(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide de ne pas effectuer d'action-\n";
    }

    void secondeAction(Joueur joueur){
        if (modeVerbeux) {
            grandTrait();
            info += "\n\t\t--Le joueur n°" + joueur.getIdentifiant() + " paye 2 soleil et effectue une deuxième action--\n";
        }
    }

    void actionBete(Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " n'a pas assez de ressource pour acheter une carte, il passe son tour\n";
    }

    void grandTrait(){
        if (modeVerbeux)
            info += "\n--------------------------------------------------\n"; //pas nécessaire mais bon faut bien s'amuser
    }

    void petitTrait(){
        if (modeVerbeux)
            info += "\n-----------------------------------\n"; //idem
    }

    void statsPlusieursPartie(int[] nbrVictoire, int[] nbrEgalite, int[] ptsGloireCumules, int nbrPartie){
        for (int i = 0; i != nbrVictoire.length; ++i){
            info += "Joueur "+(i+1)+": "+(nbrVictoire[i]*100/(float)nbrPartie)+"% de victoire; "+(nbrEgalite[i]*100/(float)nbrPartie)+"% d'égalité; avec en moyenne "+ptsGloireCumules[i]/nbrPartie+" points de gloire\n";
        }
    }

    @Override
    public String toString(){
        String x = info;
        info = "";
        return x;
    }
}

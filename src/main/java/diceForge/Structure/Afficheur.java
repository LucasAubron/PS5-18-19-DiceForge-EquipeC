package diceForge.Structure;

import diceForge.Cartes.Carte;
import diceForge.Cartes.Marteau;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Ile;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Cette classe a été faite et finie avant d'avoir connaissance de la classe log, elle sere potentiellement refaite si
 * et seulement si le temps le permet (le refactor des ressources prends du temps)
 */

public class Afficheur {

    private String info = "";
    private boolean modeVerbeux;
    private Plateau plateau;

    public Afficheur(boolean modeVerbeux) {
        this.modeVerbeux = modeVerbeux;
    }

    public void setPlateau(Plateau plateau){
        this.plateau = plateau;
    }

    public void presentationModeVerbeux() {
        if (modeVerbeux) {
            info += "\n\n\n\t\t| Cette partie oppose les bots (affichés dans l'odre de jeu): ";
            for (int i = 0; i < plateau.getJoueurs().size(); i++)
                info += plateau.getJoueurs().get(i) + ", ";
            info += "|\n\n\n\n\nL'affichage pour chaque tour apparait dans l'ordre suivant, chaque étape étant séparée par des pointillés:\n1. Phase de lancer des dés\n2. Phase d'activation des renforts\n3. Phase d'action\n4. Seconde Action s'il le joueur décide de rejouer";
        }
    }

    public void presentationCartesEtBassin(Plateau plateau){
        if (modeVerbeux) {
            info += "\n\nCartes tirées et disponibles aux joueurs():\n";
            for (Ile ile: plateau.getIles()) {
                for (List<Carte> carte : ile.getCartes())
                    info += carte.get(0).getNom().toString() + "\t||\t";
            }
            info += "\n";
            if (plateau.getJoueurs().size() == 2){
                info += "\nIl n'y a que deux joueurs, les bassins sont incomplets et les faces disponibles aux joueurs sont tirées au hasard pour les bassins de coût 4 or et 12 or:";
                int i = 0;
                for (Bassin bassin: plateau.getTemple().getSanctuaire()) {
                    i++;
                    if (i==5){
                        info += "\nBassin au coût de 4 or: ";
                        for (Face face : bassin.getFaces())
                            info += face + " || ";
                    }
                    else if (i==10){
                        info += "\nBassin au coût de 12 or: ";
                        for (Face face : bassin.getFaces())
                            info += face + " || ";
                    }
                }
            }
        }
    }

    public void manche(int numManche) {
        if (modeVerbeux)
            info += "\n\n\n-----------------------------------------------------------------------------------------------\n\t\t\t\t\t\t\t\t\t\tMANCHE " + numManche + "\n-----------------------------------------------------------------------------------------------\n";
    }

    public void tour(Joueur joueur) {
        if (modeVerbeux) {
            info += "\n";
            petitTrait();
            info += "\t\t--Tour du joueur n°" + joueur.getIdentifiant() + "--";
            petitTrait();
        }
    }

    public void resultatDe(Joueur joueur, int idDe) {
        if (modeVerbeux) {
            idDe++;
            info += "Le joueur n°" + joueur.getIdentifiant() + " lance le dé n°" + idDe + " et obtient " + joueur.getDesFaceCourante()[idDe-1] + "\n";
        }
    }

    public void desActuels(Joueur joueur) {
        if (modeVerbeux)
            info += "\nDés du joueur n°" + joueur.getIdentifiant() + ":\nDé n°1: " + joueur.getDes()[0] + "\nDé n°2 : " + joueur.getDes()[1] + "\n";
    }

    public void ressourcesDisponibles(Joueur joueur) {
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

    public void recapJoueur(Joueur joueur){
        if (modeVerbeux){
            grandTrait();
            info += "\n\t\t|Information joueur n°" + joueur.getIdentifiant() + "|\n";
            ressourcesDisponibles(joueur);
            desActuels(joueur);
            carteRenfortJetonDisponible(joueur);

         }
    }

    public void biche(int deChoisi, Face faceObtenue, Joueur joueur){
        if (modeVerbeux){
            deChoisi++;
            info += "\nLe joueur n°" + joueur.getIdentifiant() +" active le renfort biche et lance le dé n°" + deChoisi + ", il gagne " + faceObtenue;
        }
    }

    public void ancien(Joueur joueur){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort ancien, il consomme 3 or pour gagner 4 points de gloire";
        }
    }

    public void hibou(Joueur joueur, Ressource ressource){
        if (modeVerbeux){
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " active le renfort hibou, il choisit " + ressource;
        }
    }

    public void presentationLancerDes(){
        if (modeVerbeux) {
            info += "\n\t--Phase de lancer de dés--\n";
        }
    }

    public void lancerDes(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " lance ses dés-\n";
    }

    public void presentationRenforts(Joueur joueur){
        if (modeVerbeux && !joueur.getRenforts().isEmpty()) {
            grandTrait();
            info += "\n\t--Phase de renforts--\n";
        }
    }

    public void presentationAction(){
        if (modeVerbeux) {
            grandTrait();
            info += "\n\t\t--Action--\n";
        }
    }

    public void estChasse(Joueur chasse){
        if (modeVerbeux)
            info += "\nLe joueur n°" + chasse.getIdentifiant() + " est chassé\n";
    }

    public void ours(Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " gagne 3 points de gloire grace à sa carte ours\n";
    }

    public void remplissageMarteau(Joueur joueur, int or, int quantité){
        if (modeVerbeux){
            int pointMarteau = quantité - or;
            if (pointMarteau !=0)
                info += "Le joueur n°" + joueur.getIdentifiant() + " donne " + pointMarteau + " or a son marteau\n";
        }
    }

    public void actionForger(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide de forger-\n\n";
    }

    public void forger(Joueur joueur, int numDe, Face faceForgee, Face faceRetiree, Bassin bassin){
        if (modeVerbeux){
            numDe++;
            info += "Le joueur n°" + joueur.getIdentifiant() + " paye " + bassin.getCout() + " or et forge la face " + faceForgee + "sur la face " + faceRetiree + "de son dé n°" + numDe + "\n";
        }
    }

    public void actionExploit(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide d'accomplir un exploit-\n";
    }

    public void achatCarte(Carte carte, Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " achète la carte " + carte + "\n\n";
    }

    public void actionPasser(Joueur joueur){
        if (modeVerbeux)
            info += "\n-Le joueur n°" + joueur.getIdentifiant() + " décide de ne pas effectuer d'action-\n";
    }

    public void secondeAction(Joueur joueur){
        if (modeVerbeux) {
            grandTrait();
            info += "\n\t\t--Le joueur n°" + joueur.getIdentifiant() + " paye 2 soleil et effectue une deuxième action--\n";
        }
    }

    public void actionBete(Joueur joueur){
        if (modeVerbeux)
            info += "\nLe joueur n°" + joueur.getIdentifiant() + " n'a pas assez de ressource pour acheter une carte, il passe son tour\n";
    }

    public void actionDebile(int compteur, Joueur joueur, Coordinateur coordinateur){
        if (modeVerbeux && compteur == 0) {
            int coutMin = 0;
            for (Bassin bassin : plateau.getTemple().getSanctuaire())//on regarde la raison qui a poussé le joueur a ne rien forger (sa décision ou forcé)
                if (!bassin.getFaces().isEmpty() && bassin.getCout() < coutMin)
                    coutMin = bassin.getCout();
            if (joueur.getOr()<coutMin)
                info += "\nLe joueur n'a pas assez d'or pour forger la moindre face, il passe son tour\n";
            else
                info += "\nLe joueur revient sur sa décision et passe son tour\n";
        }
    }

    public void grandTrait(){
        if (modeVerbeux)
            info += "\n--------------------------------------------------\n"; //pas nécessaire mais bon faut bien s'amuser
    }

    public void petitTrait(){
        if (modeVerbeux)
            info += "\n-----------------------------------\n"; //idem
    }

    public void retourALaLigne(){
        if (modeVerbeux)
            info += "\n";
    }

    public void statsPlusieursPartie(int[] nbrVictoire, int[] nbrEgalite, int[] ptsGloireCumules, int nbrPartie, Coordinateur coordinateur){
        for (int i = 0; i != nbrVictoire.length; ++i){
            info += coordinateur.getPlateau().getJoueurs().get(i) + ": "+(nbrVictoire[i]*100/(float)nbrPartie)+"% de victoire, soit "+nbrVictoire[i]+" victoires; "+(nbrEgalite[i]*100/(float)nbrPartie)+"% d'égalité, soit "+nbrEgalite[i]+" égalités; avec en moyenne "+ptsGloireCumules[i]/nbrPartie+" points de gloire";
            if (i != nbrVictoire.length-1)
                info += "\n";
        }
    }

    public void finDePartie() {
        info += "\n\n\n";
        grandTrait();
        info += "\t\t\t\t--FIN DE PARTIE--";
        additionPointCarte(plateau.getJoueurs());
    }

    public void typhonPointBonus(int totalPdgGagne){
        if (modeVerbeux)
            info += "\t|" + "Le typhon accorde " + totalPdgGagne + " grâce aux faces forgées\n";
    }

    public void additionPointCarte(List<Joueur> joueurs) {
        int total;
        int totalMax = -1;
        int id;
        List<Integer> idGagnant = new ArrayList<>(Arrays.asList(-1));
        grandTrait();
        retourALaLigne();
        for (Joueur joueur : joueurs) {
            id = joueur.getIdentifiant();
            total = joueur.getPointDeGloire();
            info += "Points de gloire initiaux du joueur n°" + id + ": " + joueur.getPointDeGloire() + "\nCartes:\n";
            for (Carte carte : joueur.getCartes()) {
                total += carte.getNbrPointGloire();
                info += "\t|" + carte.getNom() + ": " + carte.getNbrPointGloire() + " points de gloire\n";
            }
            info += "\nTOTAL: " + total + " points de gloire";
            grandTrait();
            if (total > totalMax) {
                totalMax = total;
                idGagnant.clear();
                idGagnant.add(id);
            } else if (total == totalMax) {
                idGagnant.add(id);
            }
        }
        int nombreDeGagnants = idGagnant.size();
        if (nombreDeGagnants==1)
            info += "Le joueur n°" + idGagnant.get(0) + " gagne !";
        else if (nombreDeGagnants == 2)
            info += "Les joueurs n°" + idGagnant.get(0) + " et n°" + idGagnant.get(1) + " se partagent la victoire";
        else if(nombreDeGagnants == 3)
            info += "Les joueurs n°" + idGagnant.get(0) + " et n°" + idGagnant.get(1) + " et n°" + idGagnant.get(2) + " se partagent la victoire";
        else if(nombreDeGagnants == 4)
            info += "Les 4 joueurs sont à égalité ! Incroyable !";
    }

    public void NidoBotAfficheur(String str){
        info += str + "\n";
    }

    @Override
    public String toString(){
        String x = info;
        info = "";
        return x;
    }
}

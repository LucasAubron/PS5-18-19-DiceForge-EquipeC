package bot.ResteDesBot;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;

import java.util.List;
import java.util.Random;

/**
 * Sert d'exemple pour montrer ce que un bot doit avoir au minimum
 * pour compiler et executer correctement
 */

public class TemplateBot extends Joueur {

    private Random random;
    private int numManche = 0;

    public TemplateBot(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
    }
    public Action choisirAction(){
        numManche++;
        return Action.PASSER;
    }

    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins){
        return new ChoixJoueurForge(bassins.get(0), 0, 0, 0);
    }

    public int[] choisirOuForgerFaceSpeciale(Face faceSpeciale){
        return new int[]{0,0};
    }

    public Carte choisirCarte(List<Carte> cartes){
        return cartes.get(0);
    }

    public boolean choisirActionSupplementaire(){
        return false;
    }

    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        return ressources[0];
    }

    public int choisirOrQueLeMarteauNePrendPas(int nbrOr){
        return 0;
    }

    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){
        return renfortsUtilisables;
    }

    public Face choisirFaceACopier(List<Face> faces){
        return faces.get(0);
    }

    public Ressource choisirRessourceAPerdre(Ressource[] ressources){
        return ressources[0];
    }

    public int choisirDeFaveurMineure(){
        return 0;
    }

    public int choisirDeCyclope(){
        return 0;
    }

    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return joueurs.get(0).getIdentifiant();
    }

    public choixJetonTriton utiliserJetonTriton(){
        return choixJetonTriton.Soleil;
    }

    public boolean utiliserJetonCerbere(){
        return true;
    }

    public boolean choisirPdgPlutotQueRessource(Ressource ressource){
        return true;
    }

}


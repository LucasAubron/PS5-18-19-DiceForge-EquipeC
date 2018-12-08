package bot.ResteDesBot;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EasyBot extends Joueur {

    private Random random;
    private int numManche = 0;

    public EasyBot(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
        this.random = new Random();
    }

    @Override
    public Action choisirAction(){
        numManche++;
        if (numManche < 3 && getOr() >= 4 || numManche < 6 && getOr() >= 8) //Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;//Sinon on passe
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassinsAbordables){

        Bassin bassinAChoisir = null; // On va le choisir par la suite
        List<Bassin.typeBassin> ordrePrioBassin = new ArrayList<>();

        int numFaceAChoisirDansBassin = 0;
        int numDeSurLequelForger = getIdDuDeLePlusFaible();
        int numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(numDeSurLequelForger));
        //A partir d'ici on choisit le bassin qui nous interesse
        if (numManche <= 2) { //si on est dans les deux premières manches
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceOr); // on priorise
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceOr); // l'achat d'or
        }
        for (Bassin.typeBassin bassinPrio: ordrePrioBassin)
            for (Bassin bassin: bassinsAbordables)
                if (bassin.estLeBassin(bassinPrio) && bassinAChoisir == null)
                        bassinAChoisir = bassin;
        if (bassinAChoisir == null) // Sinon on achète la face la plus chère disponible !
            bassinAChoisir = getBassinLePlusCher(bassinsAbordables);
        return new ChoixJoueurForge(bassinAChoisir, numFaceAChoisirDansBassin, numDeSurLequelForger, numFaceARemplacerSurLeDe);
    }

    @Override
    public int[] choisirOuForgerFaceSpeciale(Face faceSpeciale){
        int numDeSurLequelForger = getIdDuDeLePlusFaible();
        int numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(numDeSurLequelForger));
        return new int[]{numDeSurLequelForger, numFaceARemplacerSurLeDe};
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes){
        for (Carte carte:cartes){
            if (carte.getNom().equals(Carte.Noms.Marteau) && !possedeCarte(Carte.Noms.Marteau))//Au moins 1 marteau
                return carte;
            if (carte.getNom().equals(Carte.Noms.Coffre) && !possedeCarte(Carte.Noms.Coffre))//Et un coffre
                return carte;
        }
        return getCarteLaPlusChere(cartes); // Si on a déjà un marteau et un coffre ou alors qu'ils ne
    }                                       // sont pas disponibles (rupture de stock ou manque de ressource)

    @Override
    public boolean choisirActionSupplementaire(){
        if ((getOr() > 10 && numManche < 6) || getSoleil() > 3 || getLune() > 1) {//Si on a assez de ressource pour refaire un tour
            numManche--;
            return true;
        }
        return false;
    }

    @Override
    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        for (Ressource ressource: ressources)
            if (ressource.estDuType(Ressource.type.SOLEIL) || ressource.estDuType(Ressource.type.LUNE))
                return ressource;
        return ressources[0]; //par défaut si on ne pas ce que l'on veut
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int nbrOr){
        return 0; ///On met tout dans le marteau quand on en a un
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        return renfortsUtilisables;//On appelle tous les renforts
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces){
        for (Face face: faces)
            if (face.getTypeFace() == Face.typeFace.SIMPLE)
                if (face.getRessource().estDuType(Ressource.type.SOLEIL) || face.getRessource().estDuType(Ressource.type.LUNE))
                    return face;
        return faces.get(0); //par défaut si on ne trouve rien
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources){
        for (Ressource ressource: ressources)
            if (ressource.estDuType(Ressource.type.OR))
                return ressource;
        return ressources[0]; //par défaut si on ne trouve pas notre bonheur
    }

    @Override
    public int choisirDeFaveurMineure(){
        return (getIdDuDeLePlusFaible()==0) ? 1:0;
    }

    @Override
    public int choisirDeCyclope(){
        return (getIdDuDeLePlusFaible()==0) ? 1:0;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (joueurs.get(0).getIdentifiant());
    }


    @Override
    public choixJetonTriton utiliserJetonTriton(){
        Random random = new Random();
        int choix = random.nextInt(choixJetonTriton.values().length);
        switch (choix){
            case 0:
                return choixJetonTriton.Rien;
            case 1:
                return choixJetonTriton.Or;
            case 2:
                return choixJetonTriton.Soleil;
            case 3:
                return choixJetonTriton.Lune;
        }
        throw new DiceForgeException("Bot","Problème avec le jeton triton");
    }

    @Override
    public boolean utiliserJetonCerbere(){
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirPdgPlutotQueRessource(Ressource ressource){
        return true;
    }


    //Méthodes propore à easyBot, fin des méthodes Override -----------------------------------------------

    private Bassin getBassinLePlusCher(List<Bassin> bassins) {
        int maxCout = 0;
        Bassin bassinLePlusCher = null;
        for (Bassin bassin: bassins) {
            if (bassin.getCout() > maxCout) {
                maxCout = bassin.getCout();
                bassinLePlusCher = bassin;
            }
        }
        return bassinLePlusCher;
    }

    private Carte getCarteLaPlusChere(List<Carte> cartes){
        int maxCout = 0;
        Carte carteLaPlusChere = null;
            for (Carte carte: cartes) {
                if (carte.getNom() == Carte.Noms.Typhon || carte.getNom() == Carte.Noms.Hydre)
                    return carte;
                if (carte.getCout()[0].getQuantite() > maxCout) {
                    maxCout = carte.getCout()[0].getQuantite();
                    carteLaPlusChere = carte;
                }

            }
        return carteLaPlusChere;
    }

    private int getIdDuDeLePlusFaible(){//Le dé le plus faible est celui qui possède le plus
        int compteurFaceUnOrDeZero= 0; // de face un or
        for (int i = 0; i<getDes().length; i++)
            for (Face face:getDe(0).getFaces())
                if (face.getTypeFace() == Face.typeFace.SIMPLE)
                    if (face.getRessource().getQuantite() == 1 && face.getRessource().estDuType(Ressource.type.OR)) {
                        if (i == 0)
                            compteurFaceUnOrDeZero++;
                        else
                            compteurFaceUnOrDeZero--;
                    }
        return (compteurFaceUnOrDeZero >= 0) ? 0 : 1;
    }

    private int getPosDeLaFaceLaPlusFaible(De de){
        for (int i=0; i < de.getFaces().length; i++)
            if (de.getFace(i).getTypeFace() == Face.typeFace.SIMPLE)
                if (de.getFace(i).getRessource().estDuType(Ressource.type.OR) && de.getFace(i).getRessource().getQuantite() == 1)
                        return i;
        return random.nextInt(6);
    }

    @Override
    public String toString(){return "EasyBot";}
}

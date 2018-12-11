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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZizaBot extends Joueur {

    private Random random = new Random();
    private int numManche = 0;

    public ZizaBot(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
    }

    @Override
    public Action choisirAction(){
        numManche++;
        if (numManche <= 4)
            return Action.FORGER;
        else
            return Action.EXPLOIT;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassinsAbordables){
        Bassin bassinAChoisir = null; // On va le choisir par la suite
        List<Bassin.typeBassin> ordrePrioBassin = new ArrayList<>();
        int numFaceAChoisirDansBassin = 0;
        int numDeSurLequelForger = getIdDuDeLePlusFaible();
        int numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(numDeSurLequelForger));

        //A partir d'ici on choisit le bassin qui nous interesse
        if (numManche == 1) { //si on est dans les deux premières manches
            // on priorise l'achat d'or
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceOr);
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceOr);
            // on add dans le sens inverse de priorité
        }
        if (numManche >= 2){
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceSoleil);
            ordrePrioBassin.add(Bassin.typeBassin.Cout6);
            ordrePrioBassin.add(Bassin.typeBassin.Cout8FaceSoleil);
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
        return getCarteLaPlusChere(cartes);
    }

    @Override
    public boolean choisirActionSupplementaire(){
        if (numManche <= 2 && (getLune() >= 1 || getSoleil() >= 3)) {
            numManche--;
            return true;
        }
        else if (numManche > 2 && (getLune() >= 3 || getSoleil() >= 4)) {
            numManche--;
            return true;
        }
        return false;
    }

    @Override
    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        Ressource ressourceAChoisir = null;
        for (Ressource ressource: ressources) {
            if (ressource.getType() == Ressource.type.SOLEIL && ressource.getQuantite() == 2) {
                ressourceAChoisir = ressource;
                break;
            }
            if (ressource.getType() == Ressource.type.LUNE && ressource.getQuantite() == 2) {
                ressourceAChoisir = ressource;
                break;
            }
            if (ressource.getType() == Ressource.type.SOLEIL && ressource.getQuantite() == 1) {
                ressourceAChoisir = ressource;
                break;
            }
            if (ressource.getType() == Ressource.type.LUNE && ressource.getQuantite() == 1) {
                ressourceAChoisir = ressource;
                break;
            }
        }

        if (ressourceAChoisir == null)
            ressourceAChoisir = ressources[random.nextInt(ressources.length)];

        return ressourceAChoisir;
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int nbrOr){
        return 0;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){
        return renfortsUtilisables;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces){
        Face faceAChoisir = null;
        for (Face face: faces) {
            if (face.getTypeFace() == Face.typeFace.CHOIX || !face.faitGagnerUneRessource() || face.getTypeFace() == Face.typeFace.ADDITION) {
                faceAChoisir = face;
                break;
            }
        }
        if (faceAChoisir != null)
            return faceAChoisir;
        else
            return faces.get(random.nextInt(faces.size()));
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources){
        Ressource ressourceAChoisir = null;
        for (Ressource ressource: ressources) {
            if (ressource.estDuType(Ressource.type.OR) && ressource.getQuantite() == 1) {
                ressourceAChoisir = ressource;
                break;
            }
            if (ressource.estDuType(Ressource.type.OR)){
                ressourceAChoisir = ressource;
                break;
            }
        }
        if (ressourceAChoisir != null)
            return ressourceAChoisir;
        else
            return ressources[random.nextInt(ressources.length)];
    }

    @Override
    public int choisirDeFaveurMineure(){
        int res;
        if (getIdDuDeLePlusFaible() == 0)
            res = 1;
        else
            res = 0;
        return res;
    }

    @Override
    public int choisirDeCyclope(){
        int res;
        if (getIdDuDeLePlusFaible() == 0)
            res = 1;
        else
            res = 0;
        return res;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return joueurs.get(0).getIdentifiant();
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        return choixJetonTriton.Soleil;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        return true;
    }

    @Override
    public boolean choisirPdgPlutotQueRessource(Ressource ressource){
        return true;
    }

    //---------------------------------------------------------------------------------------


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
            for (Face face:getDe(i).getFaces())
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
    public String toString(){
        return ("ZizaBot");
    }

}



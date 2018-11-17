package bot;

import diceForge.*;

import java.lang.reflect.AnnotatedArrayType;
import java.util.List;
import java.util.Random;

import static diceForge.Carte.Noms.*;
import static diceForge.Carte.Noms.Bouclier;

public class AubotLeGrand extends Joueur{
    private Random random;
    private boolean desComplet;
    private int nombreAncien = 0;
    private boolean troisJoueurs;
    private int manche = 0;
    private int nombreDeJoueurs;
    private Joueur.Action[] historiqueAction;
    private int compteurForge;
    private Carte cartesDispo[];
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
        compteurForge = 0;
        cartesDispo = new Carte[15];
        desComplet = false;
        random = new Random();
        }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            int indice = 0;
            troisJoueurs = (nombreDeJoueurs == 3) ? true : false;
            }
        manche++;
        switch (compteurForge){
            case 0:
                if (getOr() >= 5)
                    return Action.FORGER;
            case 2:
                if (getOr() >= 11)
                    return Action.FORGER;
            case 3:
                if (getOr() >= 11)
                    return Action.FORGER;
            default:
                return Action.EXPLOIT;
        }
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        Bassin bassin;
        int numFace = 0;
        int numDe;
        int posFace;
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        switch (compteurForge) {
            case 0:
                bassin = trouveBassinCout(bassins, 3, "Or");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                break;
            case 1:
                bassin = trouveBassinCout(bassins, 2, "Or");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                break;
            case 2:
                if (getOr() >= 3) {
                    bassin = trouveBassinCout(bassins, 3, "Soleil");
                    numDe = 1;
                    posFace = getPosFace1Or(numDe);
                    break;
                }
                else{
                    bassin = trouveBassinCout(bassins, 2, "Lune");
                    numDe = 0;
                    posFace = getPosFace1Or(numDe);
                    break;
                }
            case 3:
                bassin = trouveBassinCout(bassins, 8, "Soleil");
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                break;
            case 4:
                bassin = trouveBassinCout(bassins, 3, "Soleil");
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                desComplet = true;
                break;
            default:
                bassin = trouveBassinCout(bassins, 8, "Soleil");
                if (bassin == null) {
                    bassin = trouveBassinCout(bassins, 12, "Tout");
                    if (bassin == null) {
                        bassin = trouveBassinCout(bassins, 6, "Lune");
                        if (bassin == null){
                            bassin = trouveBassinCout(bassins, 5, "Tout");
                        }
                    }
                }
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                break;
        }
        compteurForge++;
        return new ChoixJoueurForge(bassin, numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals(Carte.Noms.Marteau) && !possedeCarte(Carte.Noms.Marteau))
                return carte;
            if (carte.getNom().equals(Ancien) && !possedeCarte(Ancien))
                return carte;
            if (carte.getNom().equals(BateauCeleste) && !possedeCarte(BateauCeleste))
                return carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        if (carteAChoisir.getNom() == Ancien)
            nombreAncien++;
        return carteAChoisir;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if ((!desComplet || getOr() < 3*nombreAncien) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return false;
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        int indiceAEnlever = -1;
        for (int indice = 0; indice < renfortsUtilisables.size(); indice++)
            if (renfortsUtilisables.get(indice) == Ancien)
                indiceAEnlever = indice;
        if (indiceAEnlever != -1)
            renfortsUtilisables.remove(indiceAEnlever);
        return renfortsUtilisables;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix){
        int indice = -1;
        for (Ressource[] ressources: faceAChoix.getRessource()) {
            indice++;
            if (ressources[0] instanceof Or)
                return indice;
        }
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        return 1;
    }

    @Override
    public int choisirDeCyclope(){
        return 0;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        int numDe = 1;
        int numFace = getPosFace1Or(numDe);
        forgerDe(0, face, numFace);
    }


    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        for (int i = 0; i != faces.size(); ++i){
            for (Ressource[] ressources:faces.get(i).getRessource()){
                for(Ressource ressource:ressources){
                    if (ressource instanceof Soleil && ressource.getQuantite() > maxSoleil){
                        posMaxSoleil = i;
                        maxSoleil = ressource.getQuantite();
                    }
                    else if (ressource instanceof Lune && ressource.getQuantite() > maxLune){
                        posMaxLune = i;
                        maxLune = ressource.getQuantite();
                    }
                    else if (ressource instanceof Or && ressource.getQuantite() > maxOr){
                        posMaxOr = i;
                        maxOr = ressource.getQuantite();
                    }
                }
            }
        }
        if (posMaxSoleil != -1) return posMaxSoleil;
        if (posMaxLune != -1) return posMaxLune;
        if (posMaxOr != -1) return posMaxOr;
        return 0;
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
        throw new DiceForgeException("Bot","Impossible, utiliserJetonTriton ne renvoit rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere(){
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource){
        return true;
    }

    @Override
    public String toString(){
        return "AubotLeGrand (bot de Lucas)";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Extrêmement utile pour connaitre la force des dés du bot, lui permet de savoir quelles stratégies
     * il devra adopter.
     * @param numDe
     * @return un tableau des ressources moyennes gagnées par lancé par le dé dans l'ordre suivant: or/soleil/lune/pdg
     */
    private void statsDe(int numDe) {
        int or = 0;
        int lune = 0;
        int soleil = 0;
        if (desComplet)
            for (De de : getDes())
                for (Face face : de.getFaces())
                    for (Ressource[] ressources : face.getRessource())
                        for (Ressource ressource : ressources) {
                            if (ressource instanceof Or)
                                or += ressource.getQuantite();
                            if (ressource instanceof Soleil)
                                soleil += ressource.getQuantite();
                            if (ressource instanceof Lune)
                                lune += ressource.getQuantite();
                        }
    }

    private int getPosFace1Or(int numDe){
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        return random.nextInt(6);
    }

    private Bassin trouveBassinCout(List<Bassin> bassins, int cout, String typeRessource){
        if (typeRessource.equals("Or")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Or)
                                    return bassin;
        }
        if (typeRessource.equals("Soleil")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Soleil)
                                    return bassin;
        }
        if (typeRessource.equals("Lune")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Lune)
                                    return bassin;
        }
        return null;
    }
}

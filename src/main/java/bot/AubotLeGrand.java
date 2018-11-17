package bot;

import diceForge.*;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class AubotLeGrand extends Joueur{
    private Random random;
    private boolean desComplet;
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
                if (getOr() >= 11 && (getLune() != 0 || getSoleil() != 0))
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
        int numFace;
        int numDe;
        int posFace;
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        switch (compteurForge) {
            case 0:
                bassin = trouveBassinCout(bassins, 3, "Or");
                numFace = 0;
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                break;
            case 1:
                bassin = trouveBassinCout(bassins, 2, "Or");
                numFace = 0;
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                break;
            case 2:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                break;
            case 3:
                bassin = trouveBassinCout(bassins, 8, "Soleil");
                numFace = 0;
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                break;
            case 4:
                bassin = trouveBassinCout(bassins, 3, "Soleil");
                numFace = 0;
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                desComplet = true;
                break;
            default:
                bassin = bassins.get(random.nextInt(bassins.size()));
                numFace = 0;
                numDe = random.nextInt(2);
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
            if (carte.getNom().equals(Carte.Noms.Marteau) && !possedeCarte(Carte.Noms.Marteau))//Au moins 1 marteau
                return carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        return carteAChoisir;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if (!desComplet)
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return ((getOr() > 10 && numManche < 6) || getSoleil() > 3 || getLune() > 1);//Si on a assez de ressource pour refaire un tour
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        return renfortsUtilisables;//On appelle tous les renforts
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil) {
                    return i;//On cherche un résultat sur la face qui donne des soleils ou des lunes
                }
            }
        }
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix){
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirDeCyclope(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        boolean aForge = false;
        int[] posFace = getPosFace1Or();
        if(posFace[0] != -1) { //si on a trouvé une face 1 or sur un des dés)
            forgerDe(posFace[0], face, posFace[1]);
            aForge = true;
        }
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            forgerDe(0, face, 0);
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
        //Toutes les faces
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

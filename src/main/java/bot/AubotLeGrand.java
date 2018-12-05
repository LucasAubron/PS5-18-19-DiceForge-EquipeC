package bot;

import diceForge.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;
import static diceForge.Carte.Noms.Bouclier;

public class AubotLeGrand extends Joueur{
    private int nombreDeLancerParManche;
    private Random random;
    private boolean secondeAction = false;
    private boolean desComplet;
    private int nombreDeJoueurs;
    private int manche = 0;
    private int compteurForge;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;


    //--------------------------------------------
    private int orDe = 11;
    private int soleilDe = 3;
    private int luneDe = 2;
    //--------------------------------------------
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
        compteurForge = 0;
        desComplet = false;
        random = new Random();
        }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            int indice = 0;
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            nombreDeLancerParManche = nombreDeJoueurs ==3  ? 3:4;
        }
        manche++;
        statsDe();
        if (secondeAction) {
            secondeAction = false;
            return Action.EXPLOIT;
        }
        if (desComplet || manche == 1 && getOr() < 5 || manche == 2 && getOr() < 8 || manche >= 4)
            return Action.EXPLOIT;
        else
            return Action.FORGER;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        Bassin bassin;
        int numFace = 0;
        int numDe;
        int posFace;
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        switch(compteurForge) {
            case 0:
                bassin = trouveBassinCout(bassins, 2, "Or");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                if (posFace == -1)
                    posFace = random.nextInt(6);
                break;
            case 1:
                bassin = trouveBassinCout(bassins, 3, "Or");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                if (posFace == -1)
                    posFace = random.nextInt(6);
                break;
            case 2:
                bassin = trouveBassinCout(bassins, 2, "Lune");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
                if (posFace == -1)
                    posFace = random.nextInt(6);
                break;
            case 3:
                bassin = trouveBassinCout(bassins, 8, "Soleil");
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                if (posFace == -1)
                    posFace = random.nextInt(6);
                break;
            case 5:
                bassin = trouveBassinCout(bassins, 3, "Soleil");
                numDe = 1;
                posFace = getPosFace1Or(numDe);
                if (posFace == -1)
                    posFace = random.nextInt(6);
                break;
            default:
                bassin = bassins.get(0);
                for (Bassin bassinAcheter: bassins){
                    if (bassinAcheter.getCout()>bassin.getCout()){
                        bassin = bassinAcheter;
                    }
                }
                numFace = 0;
                int posFaceTest;
                posFace = -1;
                numDe = -1;
                for (int i=1; i>0; i--) {
                    posFaceTest = getPosFace1Or(i);
                    if (numFace != -1) {
                        posFace = posFaceTest;
                        numDe = i;
                        break;
                    }
                }
                if (posFace == -1){
                    posFace = random.nextInt(6);
                }
                if (numDe == -1){
                    numDe = 0;
                }
        }
        if (bassin != null)
            compteurForge++;
        return new ChoixJoueurForge(bassin, numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteLaPlusChere = cartes.get(0);
        for (Carte carte: cartes) {
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 1)
                return carte;
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 2 && nombreDeJoueurs == 2)//cheese du marteau
                return carte;
            if (carte.getNom() == Ours && nombreDeJoueurs == 4)
                return carte;
            if (carte.getNom() == Ancien && nombreCartePossedee(Ancien) < 1 && manche <=5)
                return carte;
            if (carte.getNom() == HerbesFolles && nombreCartePossedee(HerbesFolles) == 0 && manche <= 2)
                return carte;
            if (carte.getNom() == BateauCeleste && nombreCartePossedee(BateauCeleste) == 0 && manche <= 4)
                return carte;
            if (carte.getNom() == Bouclier && nombreCartePossedee(Bouclier) == 0)
                return carte;
            if (carte.getNom() == Hydre || carte.getNom() == Typhon && manche >=5)
                return carte;
            if (carteLaPlusChere.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteLaPlusChere = carte;
        }
        return carteLaPlusChere;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if ((!desComplet || getOr() < 3* nombreCartePossedee(Ancien)) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        boolean rejouer = (nombreCartePossedee(Ancien) == 0 && getSoleil()>=3 || nombreCartePossedee(Marteau)==0 && getLune()>=1)? true:false;
        if (rejouer)
            manche--;
        secondeAction = true;
        return rejouer;
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        if (!desComplet) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            return new ArrayList<>();
        }
        return renfortsUtilisables;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        ressourceManquante();
        int i;
        if (soleilManquant != 0){
            for (i=0; i<faceAChoix.getRessource().length; i++)
                if (faceAChoix.getRessource()[i][0] instanceof Soleil)
                    return i;
        }
        if (luneManquant != 0){
            for (i=0; i<faceAChoix.getRessource().length; i++)
                if (faceAChoix.getRessource()[i][0] instanceof Lune)
                    return i;
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
        return 0;
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
        int numFace;
        for (int i=1; i>0; i--){
            numFace = getPosFace1Or(i);
            if (numFace !=-1) {
                forgerDe(i, face, numFace);
                break;
            }
        }
    }


    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
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
        ressourceManquante();
        if (soleilManquant >= 3 && getDesFaceCourante()[0].getRessource()[0][0] instanceof Soleil)
            return true;
        return false;
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
     * actualise le booléen deComplet (false si on est pas arrivé au niveau de dé suffisant, true sinon)
     */
    private void statsDe() {
        int or = 0;
        int lune = 0;
        int soleil = 0;
        if (!desComplet)
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
        //System.out.println("or: " + or + "\nsoleil:" + soleil + "\nlune: " + lune);
        //System.out.println("\n\n\n");
        if (or >= orDe && lune >= luneDe && soleil >= soleilDe)
            desComplet = true;
    }

    private int getPosFace1Or(int numDe){
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        return -1;
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

    private int nombreCartePossedee(Carte.Noms nom){
        int compte = 0;
        for (Carte carte: getCartes())
            if (carte.getNom() == nom)
                compte++;
        return compte;
    }

    private int trouveFaceRessourceBassin(Bassin bassin, String typeRessource){
        if (typeRessource.equals("Or"))
            for (int i=0; i < bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Or)
                            return i;
        if (typeRessource.equals("Soleil"))
            for (int i=0; i < bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Soleil)
                            return i;
        if (typeRessource.equals("Lune"))
            for (int i=0; i < bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Lune)
                            return i;
        return -1;
    }

    private void ressourceManquante(){
        orManquant = getMaxOr() - getOr();
        luneManquant = getMaxLune() - getLune();
        soleilManquant = getMaxSoleil() - getSoleil();
    }
}

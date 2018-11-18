package bot.AubotV2;

import diceForge.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;
import static diceForge.Carte.Noms.Bouclier;

public class AubotV2 extends Joueur{
    private int nombreDeLancerParManche;
    private Random random;
    private boolean secondeAction = false;
    private int nombreDeJoueurs;
    private int manche = 0;
    private int compteurForge;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;
    private boolean desComplet;

    //--------------------------------------------
    private int orDeIdeal = 11;
    private int soleilDeIdeal = 3;
    private int luneDeIdeal = 2;
    private int maxMancheForge = 3;
    //--------------------------------------------
    private boolean orComplet = false;
    private boolean soleilComplet = false;
    private boolean luneComplet = false;
    //--------------------------------------------

    /**
     * @param identifiant comprit entre 1 et 4 inclus
     * @param afficheur
     * @param plateau
     */
    public AubotV2(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
        compteurForge = 0;
        random = new Random();
    }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            nombreDeLancerParManche = nombreDeJoueurs ==3  ? 3:4;
        }
        manche++;
        statsDe();
        if (secondeAction){
            secondeAction = false;
            return Action.EXPLOIT;
        }
        if (orComplet && soleilComplet && luneComplet || manche>maxMancheForge)
            return Action.EXPLOIT;
        return Action.FORGER;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        statsDe();
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        Bassin bassin = bassins.get(0);
        int numFace = 0;
        int numDe = 0;
        int posFace = 0;
        if (!orComplet){
            bassin = trouveBassinCout(bassins, -1, "Or");
            if (bassin != null) {
                numFace = trouveFaceRessourceBassin(bassin, "Or");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
            }
        }
        else if (!soleilComplet){
            bassin = trouveBassinCout(bassins, -1, "Soleil");
            if (bassin != null) {
                numFace = trouveFaceRessourceBassin(bassin, "Soleil");
                numDe = 1;
                posFace = getPosFace1Or(numDe);
            }
        }
        else if (!orComplet){
            if (bassin != null) {
                bassin = trouveBassinCout(bassins, -1, "Lune");
                numFace = trouveFaceRessourceBassin(bassin, "Lune");
                numDe = 0;
                posFace = getPosFace1Or(numDe);
            }
        }
        else{
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
            if (numDe == -1) {
                numDe = 0;
            }
        }
        if (bassin == null)
            bassin = bassins.get(0);
        if (numFace == -1)
            numFace = 0;
        if (posFace == -1)
            posFace = random.nextInt(6);
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
        return "AubotV2 (bot de Lucas)";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Extrêmement utile pour connaitre la force des dés du bot, lui permet de savoir quelles stratégies
     * il devra adopter.
     * @return un tableau des ressources moyennes gagnées par lancé par le dé dans l'ordre suivant: or/soleil/lune/pdg
     */
    private void statsDe() {
        int or = 0;
        int lune = 0;
        int soleil = 0;
        if (!(orComplet && luneComplet && soleilComplet))
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
        if (or >= orDeIdeal)
            orComplet = true;
        if (lune >= luneDeIdeal)
            luneComplet = true;
        if (soleil >= soleilDeIdeal)
            luneComplet = true;
        if (orComplet && luneComplet && soleilComplet)
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
                if (bassin.getCout() == cout || cout == -1)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Or)
                                    return bassin;
        }
        if (typeRessource.equals("Soleil")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout || cout == -1)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Soleil)
                                    return bassin;
        }
        if (typeRessource.equals("Lune")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout || cout == -1)
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

    private void ressourceManquante(){
        orManquant = getMaxOr() - getOr();
        luneManquant = getMaxLune() - getLune();
        soleilManquant = getMaxSoleil() - getSoleil();
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
}

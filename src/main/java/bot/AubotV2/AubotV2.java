package bot.AubotV2;

import diceForge.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;
import static diceForge.Carte.Noms.Bouclier;

/**
 * lire un fichier d'information bot
 * première ligne (en hexadécimal): Manche a partir de laquelle on ne forge plus/ nombre de point d'or sur dé / idem pour soleil / idem pour lune/ Nombre d'or a avoir pour forger manche 1 / idem manche 2/ .../ idem manche 6
 * de la deuxième ligne à la 11ème: ordre de priorité des cartes à acheter pour chacun des tours dans l'ordre croissant
 */
public class AubotV2 extends Joueur{
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
    private int desOpti[] = new int[3]; //orDe, soleilDe, luneDe
    private int orPourForgerManche[] = new int[6];
    private int mancheExploit;
    private Hashtable[] ordrePrioManche = new Hashtable[10];
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
            int indice = 0;
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            nombreDeLancerParManche = nombreDeJoueurs ==3  ? 3:4;
            initValeur();
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

    private void initValeur(){

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
        Hashtable ordrePrio = new Hashtable(24);
        switch (manche){
            case 1:
                ordrePrio.putAll(ordrePrioManche[0]);
                break;
            case 2:
                ordrePrio.putAll(ordrePrioManche[2]);
                break;
            case 3:
                ordrePrio.putAll(ordrePrioManche[3]);
                break;
            case 4:
                ordrePrio.putAll(ordrePrioManche[4]);
                break;
            case 5:
                ordrePrio.putAll(ordrePrioManche[5]);
                break;
            case 6:
                ordrePrio.putAll(ordrePrioManche[6]);
                break;
            case 7:
                ordrePrio.putAll(ordrePrioManche[7]);
                break;
            case 8:
                ordrePrio.putAll(ordrePrioManche[8]);
                break;
            case 9:
                ordrePrio.putAll(ordrePrioManche[9]);
                break;
            case 10:
                ordrePrio.putAll(ordrePrioManche[10]);
                break;
        }
        for (Carte carte: cartes) {
            if (carte.getNom() == ordrePrio.get(1))
                return carte;
            if (carte.getNom() == ordrePrio.get(2))
                return carte;
            if (carte.getNom() == ordrePrio.get(3))
                return carte;
            if (carte.getNom() == ordrePrio.get(4))
                return carte;
            if (carte.getNom() == ordrePrio.get(5))
                return carte;
            if (carte.getNom() == ordrePrio.get(6))
                return carte;
            if (carte.getNom() == ordrePrio.get(7))
                return carte;
            if (carte.getNom() == ordrePrio.get(8))
                return carte;
            if (carte.getNom() == ordrePrio.get(9))
                return carte;
            if (carte.getNom() == ordrePrio.get(10))
                return carte;
            if (carte.getNom() == ordrePrio.get(11))
                return carte;
            if (carte.getNom() == ordrePrio.get(12))
                return carte;
            if (carte.getNom() == ordrePrio.get(13))
                return carte;
            if (carte.getNom() == ordrePrio.get(14))
                return carte;
            if (carte.getNom() == ordrePrio.get(15))
                return carte;
            if (carte.getNom() == ordrePrio.get(16))
                return carte;
            if (carte.getNom() == ordrePrio.get(17))
                return carte;
            if (carte.getNom() == ordrePrio.get(18))
                return carte;
            if (carte.getNom() == ordrePrio.get(19))
                return carte;
            if (carte.getNom() == ordrePrio.get(20))
                return carte;
            if (carte.getNom() == ordrePrio.get(21))
                return carte;
            if (carte.getNom() == ordrePrio.get(22))
                return carte;
            if (carte.getNom() == ordrePrio.get(23))
                return carte;
            if (carte.getNom() == ordrePrio.get(24))
                return carte;
            else
                throw new DiceForgeException("AubotV2", "une carte n'est pas reconnue:" + carte);
        }
        throw new DiceForgeException("AubotV2", "une carte n'est pas reconnue");
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if ((!desComplet || getOr() < 3* nombreCartePossedee(Ancien)) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        if (manche < mancheExploit && (getSoleil() >= 6 || getLune() >= 4))
            return false;
        manche--;
        return true;

    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        if (!desComplet) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            try{
                renfortsUtilisables.remove(Ancien);
            }
            finally {
                return renfortsUtilisables;
            }
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
        return choixJetonTriton.Soleil;
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
     * @return un tableau des ressources moyennes gagnées par lancé par le dé dans l'ordre suivant: or/soleil/lune/pdg
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
        if (or >= desOpti[0] && lune >= desOpti[2] && soleil >= desOpti[1])
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

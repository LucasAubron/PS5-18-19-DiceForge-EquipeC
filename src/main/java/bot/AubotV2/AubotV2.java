package bot.AubotV2;

import diceForge.*;

import java.io.*;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;

/**
 * lire un fichier d'information bot
 * première ligne (en hexadécimal): Manche a partir de laquelle on ne forge plus/ nombre de point d'or sur dé / idem pour soleil / idem pour lune/ Nombre d'or a avoir pour forger manche 1 / idem manche 2/ .../ idem manche 6
 *
 * De la 2ème ligne à la 11ème: ordre de priorité des cartes à acheter pour chacun des tours dans l'ordre croissant
 * a: Marteau
 * b: Coffre
 * c: Sabots d'argent (aka biche)
 * d: Ours
 * e: Satyres
 * f: Sanglier
 * g: Passeur
 * h: Cerbères
 * i: Casque d'invisibilité
 * j: Pince (aka cancer)
 * k: Sentinelle
 * l: Hydre
 * m: Typhon
 * n: Enigme(aka sphynx)
 * o: Cyclope
 * p: Miroir abyssal
 * q: Méduse
 * r: Triton
 * s: Minotaure
 * t: Bouclier de la gardienne (aka boulier)
 * u: Aile de la gardienne (aka hibou)
 * v: Voile celeste (bateau celeste)
 * w: Herbes Folles
 * x: Ancien
 *
 * 12ème ligne: nombre de carte max a acheter pour les cartes a faible cout (deux ou moins de cout) dans l'ordre suivant: Marteau, Coffre, Biche, Ours, Satyres, Sanglier, Passeur, Cerbères, Méduse, Triton, Minotaure, Bouclier, Hibou, Bateau celeste, Herbes Folles, Ancien
 */

public class AubotV2 extends Joueur{
    private boolean montrerInfo = false;
    private int nombreDeLancerParManche;
    private Random random;
    private boolean secondeAction = false;
    private boolean desComplet;
    private int nombreDeJoueurs;
    private int manche = 0;
    private int compteurForge = 0;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;
    private File f;
    private FileReader fr;
    private BufferedReader br;

    //--------------------------------------------
    private int desOpti[] = new int[3]; //orDe, soleilDe, luneDe
    private int orPourForgerManche[] = new int[6];//or minimum pour que le joueur forge de la manche 1 a 6 (à condition que manche < mancheExploit)
    private int mancheExploit;//Manche à partir de laquelle on ne fait plus que acheter des exploits
    private Enum[][] ordrePrioManche = new Enum[10][6];//Quelles cartes a acheter en priorité et a quelle manche (lorsqu'on choisit de forger)
    private int[] nombreCarteMax = new int[16];//Nombre de carte de même type max que le joueur peut acheter (pour les cartes coutant 2 et moins)
    //--------------------------------------------
    /**
     * @param identifiant comprit entre 1 et 4 inclus
     * @param afficheur
     * @param plateau
     */
    public AubotV2(int identifiant, Afficheur afficheur, Plateau plateau, String file) {
        super(identifiant, afficheur, plateau);
        this.f = new File(file);
        try{
            this.fr = new FileReader(f);
        }
        catch (IOException e) {
            System.out.println("Le fichier n'a pas été trouvé");
        }
        this.br = new BufferedReader(fr);
        this.random = new Random();
        initValeur();
    }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            int indice = 0;
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            nombreDeLancerParManche = nombreDeJoueurs ==3  ? 3:4;
            printInfo();
        }
        manche++;
        statsDe();
        if (secondeAction) {
            secondeAction = false;
            return Action.EXPLOIT;
        }
        if (desComplet ||
            manche == 1 && getOr() < orPourForgerManche[0] ||
            manche == 2 && getOr() < orPourForgerManche[1] ||
            manche == 3 && getOr() < orPourForgerManche[2] ||
            manche == 4 && getOr() < orPourForgerManche[3] ||
            manche == 5 && getOr() < orPourForgerManche[4] ||
            manche == 6 && getOr() < orPourForgerManche[5] ||
            manche >= mancheExploit)
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
        Enum[] ordrePrio = ordrePrioManche[manche-1];
        Carte carteLaPlusChere = cartes.get(0);
        for (Carte carte: cartes) {
            if ((carte.getNom() == Carte.Noms.Marteau && nombreCartePossedee(Carte.Noms.Marteau) >= nombreCarteMax[0]||
                 carte.getNom() == Carte.Noms.Coffre && nombreCartePossedee(Carte.Noms.Coffre) >= nombreCarteMax[1]||
                 carte.getNom() == Carte.Noms.Biche && nombreCartePossedee(Carte.Noms.Biche) >= nombreCarteMax[2]||
                 carte.getNom() == Carte.Noms.Ours && nombreCartePossedee(Carte.Noms.Ours) >= nombreCarteMax[3]||
                 carte.getNom() == Carte.Noms.Satyres && nombreCartePossedee(Carte.Noms.Satyres) >= nombreCarteMax[4]||
                 carte.getNom() == Carte.Noms.Sanglier && nombreCartePossedee(Carte.Noms.Sanglier) >= nombreCarteMax[5]||
                 carte.getNom() == Carte.Noms.Passeur && nombreCartePossedee(Carte.Noms.Passeur) >= nombreCarteMax[6]||
                 carte.getNom() == Carte.Noms.Cerbere && nombreCartePossedee(Carte.Noms.Cerbere) >= nombreCarteMax[7]||
                 carte.getNom() == Carte.Noms.Meduse && nombreCartePossedee(Carte.Noms.Meduse) >= nombreCarteMax[8]||
                 carte.getNom() == Carte.Noms.Triton && nombreCartePossedee(Carte.Noms.Triton) >= nombreCarteMax[9]||
                 carte.getNom() == Carte.Noms.Minautore && nombreCartePossedee(Carte.Noms.Minautore) >= nombreCarteMax[10]||
                 carte.getNom() == Carte.Noms.Bouclier && nombreCartePossedee(Carte.Noms.Bouclier) >= nombreCarteMax[11]||
                 carte.getNom() == Carte.Noms.Hibou && nombreCartePossedee(Carte.Noms.Hibou) >= nombreCarteMax[12]||
                 carte.getNom() == Carte.Noms.BateauCeleste && nombreCartePossedee(Carte.Noms.BateauCeleste) >= nombreCarteMax[13]||
                 carte.getNom() == Carte.Noms.HerbesFolles && nombreCartePossedee(Carte.Noms.HerbesFolles) >= nombreCarteMax[14]||
                 carte.getNom() == Carte.Noms.Ancien && nombreCartePossedee(Carte.Noms.Ancien) >= nombreCarteMax[15]))
                continue;
            if (carte.getNom() == ordrePrio[0])
                return carte;
            else if (carte.getNom() == ordrePrio[1])
                return carte;
            else if (carte.getNom() == ordrePrio[2])
                return carte;
            else if (carte.getNom() == ordrePrio[3])
                return carte;
            else if (carte.getNom() == ordrePrio[4])
                return carte;
            else if (carte.getNom() == ordrePrio[5])
                return carte;
            if (carteLaPlusChere.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteLaPlusChere = carte;
        }
            return carteLaPlusChere;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if ((!desComplet && manche < mancheExploit|| getOr() < 3* nombreCartePossedee(Ancien)) && getOr() + quantiteOr <= getMaxOr())
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
        if (soleilManquant >= 3 &&
                (getDesFaceCourante()[0].getRessource()[0][0] instanceof Soleil ||
                getDesFaceCourante()[0].getRessource()[0][0] instanceof PointDeGloire))
            return true;
        return false;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource){
        return true;
    }

    @Override
    public String toString(){
        return "AubotLeGrandV2 (bot de Lucas)";
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
        if (or >= desOpti[0] && soleil >= desOpti[1] && lune >= desOpti[2])
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
//---------------------------------------------------------------------------------------------------------------------------
    private void initValeur() {
        int l = 0;
        try {
            String ligne = br.readLine();
            char[] tabL1 = ligne.toCharArray();//On décompose la première ligne string en tableau d'array
            char[] tabLx = new char[6];//Le reste des lignes
            Enum[] tabOrMx = new Enum[6];
            while (ligne != null) {
                if (l == 0) {//première ligne, voir commentaire de la classe
                    for (int i = 0; i <= 9; i++) {//conversion de héxa à décimal
                        if (tabL1[i] == 'a')
                            tabL1[i] = 58;
                        else if (tabL1[i] == 'b')
                            tabL1[i] = 59;
                        else if (tabL1[i] == 'c')
                            tabL1[i] = 60;
                        else if (tabL1[i] == 'd')
                            tabL1[i] = 61;
                        else if (tabL1[i] == 'e')
                            tabL1[i] = 62;
                        else if (tabL1[i] == 'f')
                            tabL1[i] = 63;
                        else if (tabL1[i] == 'g')
                            tabL1[i] = 64;
                        if (i == 0)
                            mancheExploit = (int) tabL1[i] - 48;
                        else if (i > 0 && i < 4)
                            desOpti[i - 1] = (int) tabL1[i] - 48;
                        else if (i > 3)
                            orPourForgerManche[i - 4] = (int) tabL1[i] - 48;
                    }
                }
                else if(l>0 && l<11){
                    l--;
                    tabLx = ligne.toCharArray();
                    for (int indice=0; indice <6; indice++){
                        if (tabLx[indice] == 'A')
                            tabOrMx[indice] = Carte.Noms.Marteau;
                        else if (tabLx[indice] == 'B')
                            tabOrMx[indice] = Carte.Noms.Coffre;
                        else if (tabLx[indice] == 'C')
                            tabOrMx[indice] = Carte.Noms.Biche;
                        else if (tabLx[indice] == 'D')
                            tabOrMx[indice] = Carte.Noms.Ours;
                        else if (tabLx[indice] == 'E')
                            tabOrMx[indice] = Carte.Noms.Satyres;
                        else if (tabLx[indice] == 'F')
                            tabOrMx[indice] = Carte.Noms.Sanglier;
                        else if (tabLx[indice] == 'G')
                            tabOrMx[indice] = Carte.Noms.Passeur;
                        else if (tabLx[indice] == 'H')
                            tabOrMx[indice] = Carte.Noms.Cerbere;
                        else if (tabLx[indice] == 'I')
                            tabOrMx[indice] = Carte.Noms.CasqueDinvisibilite;
                        else if (tabLx[indice] == 'J')
                            tabOrMx[indice] = Carte.Noms.Cancer;
                        else if (tabLx[indice] == 'K')
                            tabOrMx[indice] = Carte.Noms.Sentinelle;
                        else if (tabLx[indice] == 'L')
                            tabOrMx[indice] = Carte.Noms.Hydre;
                        else if (tabLx[indice] == 'M')
                            tabOrMx[indice] = Carte.Noms.Typhon;
                        else if (tabLx[indice] == 'N')
                            tabOrMx[indice] = Carte.Noms.Sphinx;
                        else if (tabLx[indice] == 'O')
                            tabOrMx[indice] = Carte.Noms.Cyclope;
                        else if (tabLx[indice] == 'P')
                            tabOrMx[indice] = Carte.Noms.MiroirAbyssal;
                        else if (tabLx[indice] == 'Q')
                            tabOrMx[indice] = Carte.Noms.Meduse;
                        else if (tabLx[indice] == 'R')
                            tabOrMx[indice] = Carte.Noms.Triton;
                        else if (tabLx[indice] == 'S')
                            tabOrMx[indice] = Carte.Noms.Minautore;
                        else if (tabLx[indice] == 'T')
                            tabOrMx[indice] = Carte.Noms.Bouclier;
                        else if (tabLx[indice] == 'U')
                            tabOrMx[indice] = Carte.Noms.Hibou;
                        else if (tabLx[indice] == 'V')
                            tabOrMx[indice] = Carte.Noms.BateauCeleste;
                        else if (tabLx[indice] == 'W')
                            tabOrMx[indice] = Carte.Noms.HerbesFolles;
                        else if (tabLx[indice] == 'X')
                            tabOrMx[indice] = Carte.Noms.Ancien;
                    }
                    for (int i=0; i<6; i++){
                        ordrePrioManche[l][i] = tabOrMx[i];
                    }
                    l++;
                }
                else if(l==11){
                    char[] tabCarteMax;
                    tabCarteMax = ligne.toCharArray();
                    for (int k=0; k<16; k++)
                        nombreCarteMax[k] = (int) tabCarteMax[k] - 48;
                }
                l++;
                ligne = br.readLine();
            }
        }
        catch (IOException exception) {
            System.out.println("Erreur lors de l'ouverture du fichier: " + exception);
        }
        try {
            br.close();
            fr.close();
        }
        catch (IOException exception) {
            System.out.println("Erreur lors de la fermeture du fichier: " + exception);
        }
    }

    private void printInfo(){
        if (montrerInfo){
            System.out.println("MancheExploit: "+ mancheExploit);
            System.out.println("Or pour forger 1: " + orPourForgerManche[0]);
            System.out.println("Or pour forger 2: " + orPourForgerManche[1]);
            System.out.println("Or pour forger 3: " + orPourForgerManche[2]);
            System.out.println("Or pour forger 4: " + orPourForgerManche[3]);
            System.out.println("Or pour forger 5: " + orPourForgerManche[4]);
            System.out.println("Or pour forger 6: " + orPourForgerManche[5]);
            System.out.println("Or a avoir sur le dé: " + desOpti[0]);
            System.out.println("Soleil a avoir sur le dé: " + desOpti[1]);
            System.out.println("Lune a avoir sur le dé: " + desOpti[2]);
            for (Enum e: ordrePrioManche[0])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[1])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[2])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[3])
                System.out.println(e);
            for (Enum e: ordrePrioManche[4])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[5])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[6])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[7])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[8])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioManche[9])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (int i: nombreCarteMax)
                System.out.println(i);
        }
    }
}

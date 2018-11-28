package bot.AubotV2.src;

import diceForge.*;

import java.io.*;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;

/**
 * Commentaire à faire
 */

public class AubotV2 extends Joueur{
    private boolean montrerInfo = false;
    private boolean rejouer = false;
    private int nombreDeLancerParManche;
    private Random random;
    private int nombreDeJoueurs;
    private int manche = 0;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;
    private File f;
    private FileReader fr;
    private BufferedReader br;

    //--------------------------------------------
    private int mancheExploit;//Manche à partir de laquelle on ne fait plus que acheter des exploits
    private int orPourForgerManche[] = new int[6];//or minimum pour que le joueur forge de la manche 1 a 6 (à condition que manche < mancheExploit)
    private int soleilPourExploitManche[] = new int[10];
    private int lunePourExploitManche[] = new int[10];
    private int [][][] ordrePrioForgeManche = new int[6][7][3];//Quelle face forger en prio sur quel dé et pendant quelle manche
    private Enum[][] ordrePrioCarteManche = new Enum[10][18];//Quelles cartes a acheter en priorité et a quelle manche (lorsqu'on choisit de forger)
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
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            nombreDeLancerParManche = nombreDeJoueurs ==3  ? 3 : 4;
            printInfo();
        }
        manche++;
        if (rejouer){
            rejouer = false;
            return Action.EXPLOIT;
        }
        if (manche < mancheExploit) {
            for (int i = 0; i < mancheExploit; i++) {
                if (i + 1 == manche && getOr() >= orPourForgerManche[i])
                    return Action.FORGER;
            }
        }
        for (int i = 0; i < 10; i++)
            if (i+1 == manche && (getSoleil() >=  soleilPourExploitManche[i] || getLune() >= lunePourExploitManche[i]))
                return Action.EXPLOIT;
        return Action.PASSER;
    }


    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        int[][] choixForgePrio;
        String ressource = "";
        Bassin bassin;
        int numDe;
        int numFaceBassin = -1;
        int numFaceDe;
        if (bassins.get(0).getFace(0) instanceof FaceBouclier){//on gère spécifiquement le cas de la face bouclier qui est une face extrêmement puissante quand elle utilisé a bon escient
            for (int i=0; i<bassins.get(0).getFaces().size(); i++)
                if (bassins.get(0).getFace(i).getRessource()[0][0] instanceof Soleil){
                    numDe = getDeMoinsFort();
                    numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
                    return new ChoixJoueurForge(bassins.get(0), i, numDe, numFaceDe);
                }
            for (int i=0; i<4; i++)
                if (bassins.get(0).getFace(i).getRessource()[0][0] instanceof Or){
                    numDe = (getDeOr() == 0) ? 1 : 0; //on pose la face bouclier sur la face ayant le moins d'or
                    numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
                    return new ChoixJoueurForge(bassins.get(0), i, numDe, numFaceDe);
                }
            for (int i=0; i<4; i++)
                if (bassins.get(0).getFace(i).getRessource()[0][0] instanceof Lune){
                    numDe = getDeMoinsFort();
                    numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
                    return new ChoixJoueurForge(bassins.get(0), i, numDe, numFaceDe);
                }
            for (int i=0; i<4; i++)
                if (bassins.get(0).getFace(i).getRessource()[0][0] instanceof PointDeGloire){
                    numDe = (getDeMoinsFort() == 0) ? 1 : 0;
                    numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
                    return new ChoixJoueurForge(bassins.get(0), i, numDe, numFaceDe);
                }

        }
        if (manche >= mancheExploit) {//obligé de vérifier à cause des faces spéciales achetables par des cartes
            numDe = getDeMoinsFort();
            numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
            return new ChoixJoueurForge(bassins.get(0), 0, numDe, numFaceDe);
        }
        choixForgePrio = ordrePrioForgeManche[manche-1];
        for (int forgePrio = 0; forgePrio < choixForgePrio.length; forgePrio++) {
            if (choixForgePrio[forgePrio][2] == 0)
                ressource = "Or";
            if (choixForgePrio[forgePrio][2] == 1)
                ressource = "Soleil";
            if (choixForgePrio[forgePrio][2] == 2)
                ressource = "Lune";
            bassin = trouveBassinCout(bassins, choixForgePrio[forgePrio][0], ressource);
            numDe = choixForgePrio[forgePrio][1];
            numFaceDe = getFaceLaPlusFaibleSurDe(numDe);
            if (bassin != null)
                numFaceBassin = trouveFaceRessourceBassin(bassin, ressource);
            if (numFaceBassin != -1)
                return new ChoixJoueurForge(bassin, numFaceBassin, numDe, numFaceDe);
        }
        return new ChoixJoueurForge(null, 0, 0, 0);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Enum[] ordrePrio = ordrePrioCarteManche[manche-1];
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
            for (Enum nom: ordrePrio)
                if (carte.getNom() == nom)
                    return carte;
                if (carteLaPlusChere.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                    carteLaPlusChere = carte;
        }
            return carteLaPlusChere;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){//on renvoit le nombre d'or que l'on veut garder
        int nombreDeMarteauIncomplet = 0;
        for (Carte carte: getCartes())
            if (carte.getNom() == Carte.Noms.Marteau && carte.getNbrPointGloire() != 25)
                nombreDeMarteauIncomplet++;
        boolean marteauxRemplis = (nombreDeMarteauIncomplet>0) ? false : true;
        if (manche > 7 && !marteauxRemplis)
            return 0;
        if (manche < mancheExploit || getOr() < 3* nombreCartePossedee(Ancien) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        int luneNecessaire = lunePourExploitManche[manche-1];
        int soleilNecessaire = soleilPourExploitManche[manche-1];
        if ((getSoleil() < soleilNecessaire + 2 && getLune() < luneNecessaire))
            return false;
        manche--;
        rejouer = true;
        return true;

    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        if (manche < mancheExploit) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            int nombreAncien = nombreCartePossedee(Carte.Noms.Ancien);
            for (int i=0; i<nombreAncien; i++)
                renfortsUtilisables.remove(Ancien);
        }
        return renfortsUtilisables;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        ressourceManquante();
        int i;
        if (manche < mancheExploit)
            for (i=0; i<faceAChoix.getRessource().length; i++)
                if (faceAChoix.getRessource()[i][0] instanceof Or && faceAChoix.getRessource()[i][0].getQuantite()>1)
                    return i;
        if (soleilManquant > 0){
            for (i=0; i<faceAChoix.getRessource().length; i++)
                if (faceAChoix.getRessource()[i][0] instanceof Soleil)
                    return i;
        }
        if (luneManquant > 0){
            for (i=0; i<faceAChoix.getRessource().length; i++)
                if (faceAChoix.getRessource()[i][0] instanceof Lune)
                    return i;
        }
        for (i=0; i<faceAChoix.getRessource().length; i++)
            if (faceAChoix.getRessource()[i][0] instanceof PointDeGloire)
                return i;
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix){
        ressourceManquante();
        int indice = -1;
        if (orManquant == getMaxOr())
            for (Ressource[] ressources: faceAChoix.getRessource()) {
                indice++;
                if (ressources[0] instanceof Or)
                    return indice;
            }
        indice = -1;
        if (luneManquant == getMaxLune())
            for (Ressource[] ressources: faceAChoix.getRessource()) {
                indice++;
                if (ressources[0] instanceof Soleil)
                    return indice;
            }
        indice = -1;
        if (soleilManquant == getMaxSoleil())
            for (Ressource[] ressources: faceAChoix.getRessource()) {
                indice++;
                if (ressources[0] instanceof Soleil)
                    return indice;
            }
        indice = -1;
        if (getPointDeGloire() <= 1)
            for (Ressource[] ressources: faceAChoix.getRessource()) {
                indice++;
                if (ressources[0] instanceof PointDeGloire)
                    return indice;
            }
        indice = -1;
        for (Ressource[] ressources: faceAChoix.getRessource()) {
            indice++;
            if (manche == mancheExploit)
                if (ressources[0] instanceof Lune)
                    return indice;
        }
        indice = -1;
        for (Ressource[] ressources: faceAChoix.getRessource()) {
            indice++;
            if (manche >= mancheExploit)
                if (ressources[0] instanceof Or)
                    return indice;
        }
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        int numDeAEviter = getDeMoinsFort();
        int numDe = (numDeAEviter == 0) ? 1 : 0;
        return numDe;
    }

    @Override
    public int choisirDeCyclope(){
        return getDeOr();
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        boolean aForge = false;
        int numFace;
        for (int i=1; i>0; i--){
            numFace = getFaceLaPlusFaibleSurDe(i);
            if (numFace !=-1) {
                forgerDe(i, face, numFace);
                aForge = true;
                break;
            }
        }
        if (!aForge){
            forgerDe(0, face, random.nextInt(6));
        }
    }


    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        ressourceManquante();
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (manche < mancheExploit && ressource.getQuantite()>1 && ressource instanceof Or)
                        return i;
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (ressource instanceof PointDeGloire)
                        return i;
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (soleilManquant >= 2 && ressource.getQuantite()>1 && ressource instanceof Soleil)
                        return i;
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (luneManquant >= 2 && ressource.getQuantite()>1 && ressource instanceof Lune)
                        return i;
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (soleilManquant > 1 && ressource instanceof Soleil)
                        return i;
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (soleilManquant > 1 && ressource instanceof Lune)
                        return i;
        return 0;
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        ressourceManquante();
        if (nombreCartePossedee(Carte.Noms.Marteau) >= 1)
            for (Carte carte: getCartes())
                if (carte instanceof Marteau)
                    if (carte.getNbrPointGloire() < 25 && manche > 7)
                        return choixJetonTriton.Or;
        if (soleilManquant>=3)
            return choixJetonTriton.Soleil;
        if (manche >= 8 && soleilManquant >= 1)
            return  choixJetonTriton.Soleil;
        if (manche >= 8 && luneManquant >=1)
            return choixJetonTriton.Lune;

        return choixJetonTriton.Rien;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        ressourceManquante();
        if (getDesFaceCourante()[0].getRessource().length != 0 && getDesFaceCourante()[1].getRessource().length != 0)
            if ((soleilManquant >= 4 && getDesFaceCourante()[0].getRessource()[0][0] instanceof Soleil || getDesFaceCourante()[1].getRessource()[0][0] instanceof Soleil) || getDesFaceCourante()[0].getRessource()[0][0] instanceof PointDeGloire || getDesFaceCourante()[1].getRessource()[0][0] instanceof PointDeGloire)
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

    private int getFaceLaPlusFaibleSurDe(int numDe){
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 3)
                    return i;
        }
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 4)
                    return i;
        }
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Lune && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Lune && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof PointDeGloire && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 2)
                    return i;
        }
        return random.nextInt(6);
    }

    private int[] getFaceLaPlusFaible(){
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    return new int[]{i,j};
                }
            }
        }
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 3){
                    return new int[]{i,j};
                }
            }
        }
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 4){
                    return new int[]{i,j};
                }
            }
        }
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Lune && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    return new int[]{i,j};
                }
            }
        }
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof PointDeGloire && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 2){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{random.nextInt(2),random.nextInt(6)};
    }

    private int getDeMoinsFort(){
        int forceDe1 = 0;
        int forceDe2 = 0;
        for (Face face: getDe(0).getFaces()) {
            if (face.getRessource().length != 0) {
                if (face.getRessource()[0][0] instanceof Soleil)
                    forceDe1 += face.getRessource()[0][0].getQuantite() * 2;
                else if (face.getRessource()[0][0] instanceof Lune || face.getRessource()[0][0] instanceof PointDeGloire)
                    forceDe1 += face.getRessource()[0][0].getQuantite();
                else if (!(face.getRessource()[0][0] instanceof Or)) {
                    forceDe1 += 3;
                }
            }
        }
        for (Face face: getDe(1).getFaces()) {
            if (face.getRessource().length != 0) {
                if (face.getRessource()[0][0] instanceof Soleil)
                    forceDe2 += face.getRessource()[0][0].getQuantite() * 2;
                else if (face.getRessource()[0][0] instanceof Lune || face.getRessource()[0][0] instanceof PointDeGloire)
                    forceDe2 += face.getRessource()[0][0].getQuantite();
                else if (!(face.getRessource()[0][0] instanceof Or)) {
                    forceDe2 += 3;
                }
            }
        }
        int numDe = (forceDe1 > forceDe2) ? 0 : 1;
        return numDe;
    }

    int getDeOr(){
        int forceDe1 = 0;
        int forceDe2 = 0;
        for (Face face: getDe(0).getFaces()) {
            if (face.getRessource().length != 0) {
                if (face.getRessource()[0][0] instanceof Or)
                    forceDe1 += face.getRessource()[0][0].getQuantite();
            }
        }
        for (Face face: getDe(0).getFaces()) {
            if (face.getRessource().length != 0) {
                if (face.getRessource()[0][0] instanceof Or)
                    forceDe2 += face.getRessource()[0][0].getQuantite();
            }
        }
        int numDe = (forceDe1 > forceDe2) ? 0 : 1;
        return numDe;
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
        int l = 1;
        try {
            String ligne = br.readLine();
            char[] tabL1 = ligne.toCharArray();//On décompose la première ligne string en tableau d'array
            char[] tabLTaille18; //Ligne 10 à 19
            char[] tabLTaille10; //Ligne 2 et 3
            char[] tabLTaille21; //Ligne 4 à 9
            char[] tabLTaille16; //Ligne 20
            Enum[] tabPrioCarte = new Enum[18];
            while (ligne != null) {
                if (l == 1) {//première ligne, voir commentaire de la classe
                    for (int i = 0; i < 7; i++) {
                        if (i == 0)
                            mancheExploit = (int) tabL1[i] - 48;
                        else if (i > 0)
                            orPourForgerManche[i - 1] = (int) tabL1[i] - 48;
                    }
                }
                else if(l==2){
                    tabLTaille10 = ligne.toCharArray();
                    for (int i=0; i<10; i++)
                        soleilPourExploitManche[i] = tabLTaille10[i] - 48;
                }
                else if(l==3){
                    tabLTaille10 = ligne.toCharArray();
                    for (int i=0; i<10; i++)
                        lunePourExploitManche[i] = tabLTaille10[i] - 48;
                }
                else if(l>=4 && l<=9){
                    tabLTaille21 = ligne.toCharArray();
                    int[] tabTaille21 = new int[21];
                    for (int i = 0; i < ordrePrioForgeManche[0].length * 3; i++) {
                            tabTaille21[i] = (int) tabLTaille21[i] - 48;
                    }
                    int[] tab1 = new int[]{tabTaille21[0], tabTaille21[1], tabTaille21[2]};
                    int[] tab2 = new int[]{tabTaille21[3], tabTaille21[4], tabTaille21[5]};
                    int[] tab3 = new int[]{tabTaille21[6], tabTaille21[7], tabTaille21[8]};
                    int[] tab4 = new int[]{tabTaille21[9], tabTaille21[10], tabTaille21[11]};
                    int[] tab5 = new int[]{tabTaille21[12], tabTaille21[13], tabTaille21[14]};
                    int[] tab6 = new int[]{tabTaille21[15], tabTaille21[16], tabTaille21[17]};
                    int[] tab7 = new int[]{tabTaille21[18], tabTaille21[19], tabTaille21[20]};
                    ordrePrioForgeManche[l-4] = new int[][]{tab1, tab2, tab3, tab4, tab5, tab6, tab7};
                }
                else if(l>=10 && l<=19){
                    l = l -10;
                    tabLTaille18 = ligne.toCharArray();
                    for (int indice=0; indice <ordrePrioCarteManche[0].length; indice++){
                        if (tabLTaille18[indice] == 'a')
                            tabPrioCarte[indice] = Carte.Noms.Marteau;
                        else if (tabLTaille18[indice] == 'b')
                            tabPrioCarte[indice] = Carte.Noms.Coffre;
                        else if (tabLTaille18[indice] == 'c')
                            tabPrioCarte[indice] = Carte.Noms.Biche;
                        else if (tabLTaille18[indice] == 'd')
                            tabPrioCarte[indice] = Carte.Noms.Ours;
                        else if (tabLTaille18[indice] == 'e')
                            tabPrioCarte[indice] = Carte.Noms.Satyres;
                        else if (tabLTaille18[indice] == 'f')
                            tabPrioCarte[indice] = Carte.Noms.Sanglier;
                        else if (tabLTaille18[indice] == 'g')
                            tabPrioCarte[indice] = Carte.Noms.Passeur;
                        else if (tabLTaille18[indice] == 'h')
                            tabPrioCarte[indice] = Carte.Noms.Cerbere;
                        else if (tabLTaille18[indice] == 'i')
                            tabPrioCarte[indice] = Carte.Noms.CasqueDinvisibilite;
                        else if (tabLTaille18[indice] == 'j')
                            tabPrioCarte[indice] = Carte.Noms.Cancer;
                        else if (tabLTaille18[indice] == 'k')
                            tabPrioCarte[indice] = Carte.Noms.Sentinelle;
                        else if (tabLTaille18[indice] == 'l')
                            tabPrioCarte[indice] = Carte.Noms.Hydre;
                        else if (tabLTaille18[indice] == 'm')
                            tabPrioCarte[indice] = Carte.Noms.Typhon;
                        else if (tabLTaille18[indice] == 'n')
                            tabPrioCarte[indice] = Carte.Noms.Sphinx;
                        else if (tabLTaille18[indice] == 'o')
                            tabPrioCarte[indice] = Carte.Noms.Cyclope;
                        else if (tabLTaille18[indice] == 'p')
                            tabPrioCarte[indice] = Carte.Noms.MiroirAbyssal;
                        else if (tabLTaille18[indice] == 'q')
                            tabPrioCarte[indice] = Carte.Noms.Meduse;
                        else if (tabLTaille18[indice] == 'r')
                            tabPrioCarte[indice] = Carte.Noms.Triton;
                        else if (tabLTaille18[indice] == 's')
                            tabPrioCarte[indice] = Carte.Noms.Minautore;
                        else if (tabLTaille18[indice] == 't')
                            tabPrioCarte[indice] = Carte.Noms.Bouclier;
                        else if (tabLTaille18[indice] == 'u')
                            tabPrioCarte[indice] = Carte.Noms.Hibou;
                        else if (tabLTaille18[indice] == 'v')
                            tabPrioCarte[indice] = Carte.Noms.BateauCeleste;
                        else if (tabLTaille18[indice] == 'w')
                            tabPrioCarte[indice] = Carte.Noms.HerbesFolles;
                        else if (tabLTaille18[indice] == 'x')
                            tabPrioCarte[indice] = Carte.Noms.Ancien;
                    }
                    for (int i=0; i<ordrePrioCarteManche[0].length; i++){
                        ordrePrioCarteManche[l][i] = tabPrioCarte[i];
                    }
                    l = l + 10;
                }
                else if(l==20){
                    tabLTaille16 = ligne.toCharArray();
                    for (int k=0; k<16; k++)
                        nombreCarteMax[k] = (int) tabLTaille16[k] - 48;
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
            System.out.println("--------------------------------------------");
            System.out.println("SoleilPourExploitManche:");
            for (int n: soleilPourExploitManche)
                System.out.println(n);
            System.out.println("--------------------------------------------");
            System.out.println("LunePourExploitManche:");
            for (int n: lunePourExploitManche)
                System.out.println(n);
            for (int[][] nnn: ordrePrioForgeManche) {
                System.out.println("--------------------------------------------");
                for (int[] nn : nnn)
                    for (int n: nn)
                        System.out.println(n);
            }
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[0])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[1])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[2])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[3])
                System.out.println(e);
            for (Enum e: ordrePrioCarteManche[4])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[5])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[6])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[7])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[8])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (Enum e: ordrePrioCarteManche[9])
                System.out.println(e);
            System.out.println("--------------------------------------------");
            for (int i: nombreCarteMax)
                System.out.println(i);
        }
    }
}

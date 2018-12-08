package bot.AubotV2.src;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.Faces.FaceBouclier;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;

import java.io.*;
import java.util.List;
import java.util.Random;


/**
 * Commentaire à faire
 */

public class AubotV2 extends Joueur {
    private boolean montrerInfo = false;
    private boolean rejouer = false;
    private Random random;
    private int nombreDeJoueurs;
    private int manche = 0;
    private int derniereManche;
    int compteurDeManchePasseeAForger = 0;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;
    private String pathFile;
    private File f;
    private FileReader fr;
    private BufferedReader br;
    //--------------------------------------------
    private int nombreDeTourForgeOptimal;//Nombre de tour max dédié a la forge
    private int orPourForgerManche[] = new int[6];//or minimum pour que le joueur forge de la manche 1 a 6 (à condition que manche <= 6)
    private int [][][] ordrePrioForgeManche = new int[3][5][2];//Les trois premiers tours de forge sont preset car ils sont importants, sinon on forgera toujours la face la plus chère
    private Carte.Noms[] ordrePrioCarte = new Carte.Noms[][24];//Quelles cartes a acheter en priorité
    private int[] nombreCarteMax = new int[18];//Nombre de carte de même type max que le joueur doir acheter
    //--------------------------------------------
    public AubotV2(int identifiant, Afficheur afficheur, Plateau plateau, String file) {
        super(identifiant, afficheur, plateau);
        this.pathFile = file;
    }

    @Override
    public Joueur.Action choisirAction() {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            derniereManche = (nombreDeJoueurs == 3) ? 10 : 9;
            initBuffers();
            initValeur();
            printInfo();
        }
        manche++;
        if (rejouer){ // S'il s'agit d'une deuxième action, on achète forcément une carte
            rejouer = false;
            return Action.EXPLOIT;
        }
        if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal && getOr() >= orPourForgerManche[manche-1] && manche <= 6)
            return Action.FORGER;
        else
            return Action.EXPLOIT;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins){
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes){
        if (manche >= derniereManche -1) // A la fin de la partie la seule caractéristique d'une carte qui importe vraiment est les points qu'elle rapporte
            return carteQuiApporteLePlusDePoint(cartes);
        for (Carte.Noms nom: ordrePrioCarte) // Sinon on regarde la liste de priorité des cartes
            for (Carte carte: cartes)
                if (nom == carte.getNom() && nombreCartePossedee(nom) <= maximumDeCarteDeType(nom))
                    return (carte);
        return getCarteLaPlusChere(cartes); // Sinon, cas rare, on achète la carte la plus chère que l'ont peut acheter
    }

    @Override
    public boolean choisirActionSupplementaire(){
        refreshInfoRessourceManquante();
        if (manche == derniereManche){// A la fin de la partie on rejoue forcément
            rejouer = true;
            manche--;
            return true;
        }
        if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal)// au début de la partie
            if (getSoleil() >= 3 || getLune() >= 1){               // on est friand de petite carte
                rejouer = true;
                manche--;                                         // il faut donc moins de ressource
                return true;                                       // pour décider de rejouer
            }
            else
            if (soleilManquant <= 2 || luneManquant <= 2) {        // sinon on regarde si on a trop de
                rejouer = true;
                manche--;                                          // ressource (et donc risque de gasillage)
                return true;                                       // pour faire notre choix
            }
        return false;
    }

    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        refreshInfoRessourceManquante();
        int quantiteMax = 0;
        Ressource ressourceAChoisir = null;
        Ressource.type typePrio;
        // A partir d'ici on défini quelle type ressource est prioritaire en fonction de la situation
        if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal)
            typePrio = Ressource.type.OR;
        else if (manche >= derniereManche - 2)
            typePrio = Ressource.type.PDG;
        else if(luneManquant >= 3 && soleilManquant <= 2)
            typePrio = Ressource.type.LUNE;
        else
            typePrio = Ressource.type.SOLEIL;
        // Et ensuite on choisit simplement la ressource du type voulu et qui a la plus grosse quantité
        for (Ressource ressource: ressources)
            if (ressource.estDuType(typePrio) && ressource.getQuantite() > quantiteMax){
                ressourceAChoisir = ressource;
                quantiteMax = ressource.getQuantite();
            }
        if (ressourceAChoisir != null)
            return ressourceAChoisir;
        else //Si la recherche a échoué, tant pis \_(^-^)_/
            return ressources[0];
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int nbrOr){//on renvoit le nombre d'or que l'on veut garder
        boolean possedeUnMarteauIncomplet = false;
        for (Carte carte: getCartes())
            if (carte.getNom() == Carte.Noms.Marteau && carte.getNbrPointGloire() != 25)
                possedeUnMarteauIncomplet = true;
        if (manche >= derniereManche - 2 && possedeUnMarteauIncomplet) // On fait tout pour remplir le marteau vers la fin de la partie
            return 0;
        if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal || getOr() < 3* nombreCartePossedee(Carte.Noms.Ancien) && getOr() + nbrOr <= getMaxOr())//ici on s'assure
            return nbrOr;                                                                                       // de garder l'or en début de partie, et d'avoir assez d'or
        return 0;                                                                                               // pour utiliser ses anciens au prochain tour
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            int nombreAncien = nombreCartePossedee(Carte.Noms.Ancien);
            for (int i=0; i<nombreAncien; i++)
                renfortsUtilisables.remove(Carte.Noms.Ancien);
        }
        return renfortsUtilisables;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces){
        refreshInfoRessourceManquante();
        Face faceAChoisir = null;
        for (Face face: faces)
            if (faceAChoisir == null) {
                if (face.estUneFaceAyantBesoinDuDeuxiemeDe() || face.estFaceAChoix())
                    faceAChoisir = face;
                else if (faceAChoisir.getTypeFace() == Face.typeFace.ADDITION)
                    faceAChoisir = face;
                else if (faceAChoisir.getTypeFace() == Face.typeFace.SIMPLE)
                    if ()

            }
        if (faceAChoisir != null)
            return faceAChoisir;
        else
            return faces.get(0); //Si la recherche a échoué, tant pis \_(^-^)_/
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources){}

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
        refreshInfoRessourceManquante();
        for (int i=0; i<faces.size(); i++)
            for (Ressource[] ressources: faces.get(i).getRessource())
                for (Ressource ressource: ressources)
                    if (manche < nombreDeTourForgeOptimal && ressource.getQuantite()>1 && ressource instanceof Or)
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
        refreshInfoRessourceManquante();
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
        refreshInfoRessourceManquante();
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
    private Carte carteQuiApporteLePlusDePoint(List<Carte> cartes) {
    }

    private int maximumDeCarteDeType(Carte.Noms nom) {
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
                    if (face.getRessource().getQuantite() == 1)
                        if (face.getRessource().estDuType(Ressource.type.OR))
                            if (i==0)
                                compteurFaceUnOrDeZero++;
                            else
                                compteurFaceUnOrDeZero--;
        return (compteurFaceUnOrDeZero >= 0) ? 0 : 1;
    }

    private int getPosDeLaFaceLaPlusFaible(De de){
        for (int i=0; i < de.getFaces().length; i++)
            if (de.getFace(i).getTypeFace() == Face.typeFace.SIMPLE)
                if (de.getFace(i).getRessource().estDuType(Ressource.type.OR))
                    if (de.getFace(i).getRessource().getQuantite() == 1)
                        return i;
        return random.nextInt(6);
    }


    private int nombreCartePossedee(Carte.Noms nom){
        int compte = 0;
        for (Carte carte: getCartes())
            if (carte.getNom() == nom)
                compte++;
        return compte;
    }

    private void refreshInfoRessourceManquante(){
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
                            nombreDeTourForgeOptimal = (int) tabL1[i] - 48;
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
                    for (int indice=0; indice <ordrePrioCarte[0].length; indice++){
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
                    for (int i=0; i<ordrePrioCarte[0].length; i++){
                        ordrePrioCarte[l][i] = tabPrioCarte[i];
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

    private void initBuffers() {
        switch (nombreDeJoueurs){
            case 2:
                pathFile += "1V1/";
            case 3:
                pathFile += "1V1V1/";
            case 4:
                pathFile += "1V1V1V1/";
        }
        this.f = new File(pathFile);
        try{
            this.fr = new FileReader(f);
        }
        catch (IOException e) {
            System.out.println("Le fichier n'a pas été trouvé");
        }
        this.br = new BufferedReader(fr);
        this.random = new Random();
    }

    private void printInfo(){
    }
}

package bot.AubotV2.src;

import diceForge.Cartes.Carte;
import diceForge.Cartes.Marteau;
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
import java.util.*;


/**
 * Meilleur bot du set de bot, il a la particularité d'avoir des paramètres qui peuvent varier.
 * Pour compléter ces paramètres il lit dans un fichier source qui doit ressembler à ceci:
 *
 * 3468888                          //première ligne: un entier [1,6], six entier [2,9]
 * 555666                           // six entier [1,6]
 * 444555                           //six entier [1,6]
 * 0123456789                       //dix entier [0,9] différents un à un
 * 0123456789                       //dix entier [0,9] différents un à un
 * 0123456789                       //dix entier [0,9] différents un à un
 * abcdefghijklmnopqrstuvwx         // Les 24 premières lettres de l'alphabet dans un certain ordre
 * 123401203112312412031201         // 24 entier [0,4]
 *
 * PREMIERE LIGNE
 * le premier entier de la première ligne est le nombre de tour maximum que doit allouer le joueur
 * a la forge et UNIQUEMENT si la manche <= 6, les 6 entiers qui suivent sont l'or minimum que
 * doit avoir le joueur pour décider de forger durant chaque manche (de 1 a 6)
 *
 * DEUXIEME 2 ET 3
 * la deuxieme ligne donne le nombre minimum de soleil que l'on doit avoir pour les manches
 * 4 à dernière manche-1 (8 ou 9 selon le nombre de joueur)
 * la troisème est la même chose mais pour les lunes, le joueur doit respecter l'exigence
 * des lunes OU (pas et) des soleils.
 * Si l'on ne définit pas toutes les manches ainsi c'est parce que les trois premières manches
 * on décide de rejouer si on a >=3 soleils ou >= 1 lune car les petites cartes sont fortes en début de partie.
 * La dernière manche n'est pas défini non plus car on va forcément rejouer, c'est la dernière manche on ne doit
 * pas essayer d'économiser des ressources !
 *
 * LIGNE 4 A 6
 *Y figure les bassins dans lesquels piocher en priorité pour les 3 premiers tours de forge (et pas de jeu!), il y
 * a exactement 10 bassins donc 10 entiers différents pour les identifier. Passé le 3ème tour de forge (et si manche<=6)
 * on achetera toujours dans le bassin qui coute le plus cher (et qui est dans nos moyens).
 * La ligne 4 correspond au premier tour de forge, la ligne 5 au deuxième et la ligne 6 au troisème.
 * Pour savoir quel entier correpspond a quel bassin, se référer à la méthode initValeur.
 *
 * LIGNE 7
 * Ordre des cartes à acheter en priorité quand on décide de faire un exploit.
 * De la plus prioritaire à la moins. Chaque lettre de l'alphabet correspond à une carte.
 * On peut retrouver leur ordre de A a Z dans le constructeur. (a = marteau, b = coffre, etc...)
 *
 * LIGNE 8
 * La ligne précédente tuerait la réussite du bot sans celle-ci, a chaque carte, dans l'ordre
 * du constructeur, est associé un entier [0,4] indiquanr le nombre maximum d'exemplaire que le
 * bot doit se procurer avant de passer à la carte suivante. Ainsi on ne se retrouve pas avec 4 marteaux
 * dont 3 incomplets.
 *
 * Ce bot a également la capacité d'évoluer grâce a un algorithme génétique.
 * Il effectue des tournois entre plusieurs fichier sources, il garde les meilleurs,
 * en créer de nouveaux à partir gagnants (qui parfois peuvent muter) et
 * recommence. BestBot est le fichier source qui est lu quand AubotV2
 * est utilisé dans le main du projet. Il faut le remplacer manuellement
 * après une simulation de plusieurs générations (copier coller
 * le fichier du meilleur bot du dernier tournoi effectué).
 *
 * Petit à petit, de génération en génération le bot s'améliore.
 * Il faut utiliser le main de AubotV2 pour lancer les tournois,
 * certains paramètres sont changeables (chance de mutation,
 * nombre de population, etc...)
 *
 * Ce bot possède une version pour chaque type de partie (2, 3 ou 4 joueurs)
 * car certaines cartes ou face ont un impact très différents dépendant du
 * nombre de joueurs.
 */


public class AubotV2 extends Joueur {
    private boolean montrerInfo = false;
    private boolean Arejoue = false;
    private Random random = new Random();
    private int nombreDeJoueurs;
    private int manche = 0;
    private int derniereManche;
    int compteurDeManchePasseeAForger = 0;
    private int orManquant;
    private int luneManquant;
    private int soleilManquant;
    private int aForgeManche1 = 0;
    private int aForgeManche2 = 0;
    private int aForgeManche3 = 0;
    private int aForgeManche4 = 0;
    private int aForgeManche5 = 0;
    private int aForgeManche6 = 0;
    private String pathFile;
    private File f;
    private FileReader fr;
    private BufferedReader br;
    private  boolean aDejaEteInit = false;
    //--------------------------------------------
    private int nombreDeTourForgeOptimal;//Nombre de tour max dédié a la forge
    private int orPourForgerManche[] = new int[6];//or minimum pour que le joueur forge de la manche 1 a 6 (à condition que manche <= 6)
    private Bassin.typeBassin [][] ordrePrioBassinManche = new Bassin.typeBassin[3][10];//Les trois premiers tours de forge sont preset car ils sont importants, sinon on forgera toujours la face la plus chère
    private Carte.Noms[] ordrePrioCarte = new Carte.Noms[24];//Quelles cartes a acheter en priorité
    private int[] nombreCarteMax = new int[24];//Nombre de carte de même type max que le joueur doir acheter
    private int[] nombreDeSoleilPourRejouer = new int[6]; //parle de lui même
    private int[] nombreDeLunePourRejouer = new int[6]; // idem
    private Map<Carte.Noms, Integer> indiceCarte = new HashMap(){}; //va indiquer au bot ou chercher les infos concernant une carte
    //--------------------------------------------
    public AubotV2(int identifiant, Afficheur afficheur, Plateau plateau, String file) {
        super(identifiant, afficheur, plateau);
        this.pathFile = file;
        indiceCarte.put(Carte.Noms.Marteau, 0); // Les clés sont les noms,
        indiceCarte.put(Carte.Noms.Coffre, 1);  // les value sont la
        indiceCarte.put(Carte.Noms.Biche, 2);   // position dans les
        indiceCarte.put(Carte.Noms.Ours, 3);    // tableaux qui contiennent
        indiceCarte.put(Carte.Noms.Satyres, 4); // des infos pour les 24 cartes
        indiceCarte.put(Carte.Noms.Sanglier, 5);
        indiceCarte.put(Carte.Noms.Passeur, 6);
        indiceCarte.put(Carte.Noms.Cerbere, 7);
        indiceCarte.put(Carte.Noms.CasqueDinvisibilite, 8);
        indiceCarte.put(Carte.Noms.Cancer, 9);
        indiceCarte.put(Carte.Noms.Sentinelle, 10);
        indiceCarte.put(Carte.Noms.Hydre, 11);
        indiceCarte.put(Carte.Noms.Typhon, 12);
        indiceCarte.put(Carte.Noms.Sphinx, 13);
        indiceCarte.put(Carte.Noms.Cyclope, 14);
        indiceCarte.put(Carte.Noms.MiroirAbyssal, 15);
        indiceCarte.put(Carte.Noms.Meduse, 16);
        indiceCarte.put(Carte.Noms.Triton, 17);
        indiceCarte.put(Carte.Noms.Minautore, 18);
        indiceCarte.put(Carte.Noms.Bouclier, 19);
        indiceCarte.put(Carte.Noms.Hibou, 20);
        indiceCarte.put(Carte.Noms.VoileCeleste, 21);
        indiceCarte.put(Carte.Noms.HerbesFolles, 22);
        indiceCarte.put(Carte.Noms.Ancien, 23);
    }

    @Override
    public Joueur.Action choisirAction() {
        if (manche == 0 && !aDejaEteInit) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            aDejaEteInit = true; // Pour éviter les bug quand on rejoue manche 1 ...
            nombreDeJoueurs = getPlateau().getJoueurs().size();
            derniereManche = (nombreDeJoueurs == 3) ? 10 : 9;
            initBuffers();
            initValeur();
            printInfo();
        }
        refreshInfoForgeManche();
        manche++;
        if (Arejoue){ // S'il s'agit d'une deuxième action, on achète forcément une carte
            Arejoue = false; //Arejoue = A rejoué
            return Action.EXPLOIT;
        }
        if (manche <= 6)
            if (compteurDeManchePasseeAForger < nombreDeTourForgeOptimal && getOr() >= orPourForgerManche[manche - 1])
                return Action.FORGER;
        return Action.EXPLOIT;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassinsAbordables) {
        switch(manche){
            case 1:
                aForgeManche1 = 1;
                break;
            case 2:
                aForgeManche2 = 1;
                break;
            case 3:
                aForgeManche3 = 1;
                break;
            case 4:
                aForgeManche4 = 1;
                break;
            case 5:
                aForgeManche5 = 1;
                break;
            case 6:
                aForgeManche6 = 1;
                break;
        }
        Bassin bassinAChoisir = null;
        Bassin.typeBassin[] ordrePrioBassin;
        int numFaceAChoisirDansBassin = 0;
        int numDeSurLequelForger = getIdDuDeLePlusFaible();
        int numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(numDeSurLequelForger));

        //A partir d'ici on choisit le bassin qui nous interesse
        if (compteurDeManchePasseeAForger <= 2)
            ordrePrioBassin = ordrePrioBassinManche[compteurDeManchePasseeAForger];
        else
            ordrePrioBassin = new Bassin.typeBassin[]{}; //vide

        for (Bassin.typeBassin bassinPrio: ordrePrioBassin)
            for (Bassin bassin: bassinsAbordables)
                if (bassinAChoisir == null && bassin.estLeBassin(bassinPrio))
                    bassinAChoisir = bassin;

        //On gère l'ordre de priorité spécifiquement pour les boucliers
        if (bassinsAbordables.get(0).estLeBassin(Bassin.typeBassin.Bouclier)) {
            bassinAChoisir = bassinsAbordables.get(0);
            for (int i = 0; i < bassinAChoisir.getFaces().size(); i++) {
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.SOLEIL)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.LUNE)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.PDG)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.OR)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
            }
        }

        if (bassinAChoisir == null)
            bassinAChoisir = getBassinLePlusCher(bassinsAbordables); //Si on a toujours rien trouvé

        if (bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout12) || bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout4)) //Bassin avec des faces différentes
            numFaceAChoisirDansBassin = choisirFaceDansBassinCout4Ou12();

        return new ChoixJoueurForge(
                bassinAChoisir, numFaceAChoisirDansBassin,
                numDeSurLequelForger, numFaceARemplacerSurLeDe);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes){
        if (manche >= derniereManche -1) // A la fin de la partie la seule caractéristique d'une carte qui importe vraiment est les points qu'elle rapporte
            return carteQuiApporteLePlusDePoint(cartes);
        for (Carte.Noms nom: ordrePrioCarte) // Sinon on regarde la liste de priorité des cartes
            for (Carte carte: cartes)
                if (nom == carte.getNom() && nombreCartePossedee(nom) <= nombreCarteMax[indiceCarte.get(carte.getNom())])
                    return (carte);
        return getCarteLaPlusChere(cartes); // Sinon on achète la carte la plus chère que l'ont peut acheter
    }                                       // est sensé ne jamais arriver

    @Override
    public boolean choisirActionSupplementaire(){
        refreshInfoRessourceManquante();
        if (manche == derniereManche){// A la fin de la partie on rejoue forcément
            Arejoue = true;
            manche--;
            return true;
        }
        if (manche <= 3) {                                          // au début de la partie
            if (getSoleil() >= 3 || getLune() >= 1) {               // on est friand de petites cartes
                Arejoue = true;                                     // il faut donc peu de ressource
                manche--;                                           // pour décider de Arejoue
                return true;
            }
        }
        else
            if (getSoleil() >= nombreDeSoleilPourRejouer[manche - 4] || nombreDeSoleilPourRejouer[manche - 4] <= 2) {        // sinon on regarde les carac du bot
                Arejoue = true;
                manche--;
                return true;
            }
        return false;
    }

    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        refreshInfoRessourceManquante();
        int quantiteMax = 0;
        Ressource ressourceAChoisir = null;
        Ressource.type typePrio;
        // A partir d'ici on défini quelle type ressource est prioritaire en fonction de la situation
        if (manche >= derniereManche - 2)
            typePrio = Ressource.type.PDG;
        else if(luneManquant >= 3 && soleilManquant <= 2)
            typePrio = Ressource.type.LUNE;
        else if (soleilManquant >= 1)
            typePrio = Ressource.type.SOLEIL;
        else
            typePrio = Ressource.type.OR;
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
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){
        if (manche <= 3) {//on préfère garder l'or en début de partie
            int nombreAncien = nombreCartePossedee(Carte.Noms.Ancien);
            for (int i=0; i<nombreAncien; i++)
                renfortsUtilisables.remove(Renfort.ANCIEN);
        }
        return renfortsUtilisables;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces){
        refreshInfoRessourceManquante();
        Face faceAChoisir = null;
        for (Face face: faces) {
            if (faceAChoisir == null) {
                if (face.estUneFaceAyantBesoinDuDeuxiemeDe() || face.estFaceAChoix())
                    faceAChoisir = face;
            }
        }
        for (Face face: faces) {
            if (faceAChoisir == null) {
                if (face.getTypeFace() == Face.typeFace.ADDITION)
                    faceAChoisir = face;
            }
        }
        for (Face face: faces) {
            if (faceAChoisir == null) {
                if (face.getTypeFace() == Face.typeFace.SIMPLE) {
                    if (manche >= derniereManche - 2)
                        if (face.getRessource().estDuType(Ressource.type.PDG) && face.getRessource().getQuantite() == 2)
                            faceAChoisir = face;
                    else if (face.getRessource().estDuType(Ressource.type.PDG) && face.getRessource().getQuantite() == 3)
                        faceAChoisir = face;
                    else if (face.getRessource().estDuType(Ressource.type.PDG) && face.getRessource().getQuantite() == 4)
                        faceAChoisir = face;
                    else if (soleilManquant >= 3)
                        if (face.getRessource().estDuType(Ressource.type.SOLEIL) && face.getRessource().getQuantite() == 2)
                            faceAChoisir = face;
                    else if (face.getRessource().estDuType(Ressource.type.SOLEIL) && face.getRessource().getQuantite() == 1)
                        faceAChoisir = face;
                    else if (luneManquant >= 3)
                        if (face.getRessource().estDuType(Ressource.type.LUNE) && face.getRessource().getQuantite() == 2)
                            faceAChoisir = face;
                    else if (face.getRessource().estDuType(Ressource.type.LUNE) && face.getRessource().getQuantite() == 1)
                        faceAChoisir = face;
                }
            }
        }
        if (faceAChoisir != null)
            return faceAChoisir;
        else
            return faces.get(0); //Si la recherche a échoué, tant pis \_(^-^)_/
    }

    @Override
    public int[] choisirOuForgerFaceSpeciale(Face face){
        return new int[]{0,0};
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources){
        for (Ressource ressource: ressources)
            if (manche <= 3 && ressource.estDuType(Ressource.type.PDG))
                return ressource;
            else if (manche >= 6 && ressource.estDuType(Ressource.type.OR))
                return ressource;
        return ressources[0];
    }

    @Override
    public int choisirDeFaveurMineure(){
        int numDeAEviter = getIdDuDeLePlusFaible();
        int numDe = (numDeAEviter == 0) ? 1 : 0;
        return numDe;
    }

    @Override
    public int choisirDeCyclope(){
        return getIdDuDeLePlusFaible(); // car il contient encore des faces 1 OR
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return joueurs.get(0).getIdentifiant();
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        refreshInfoRessourceManquante();
        if (nombreCartePossedee(Carte.Noms.Marteau) >= 1)
            for (Carte carte: getCartes())
                if (carte.getNom() == Carte.Noms.Marteau)
                    if (carte.getNbrPointGloire() < 25 && manche >= derniereManche - 2) //On essaye de remplir le marteau coute
                        return choixJetonTriton.Or;                   // que coute en fin de partie
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
    return true;
    }

    @Override
    public boolean choisirPdgPlutotQueRessource(Ressource ressource){
        return true;
    }



    @Override
    public String toString(){
        return "AubotLeGrandV2 (bot de Lucas)";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------
    private Carte carteQuiApporteLePlusDePoint(List<Carte> cartes) {
        Carte carteAChoisir = null;
        int pdgMax = 0;
        for (Carte carte: cartes)
            if (carte.getNbrPointGloire() >= pdgMax) {
                pdgMax = carte.getNbrPointGloire();
                carteAChoisir = carte;
            }
        if (carteAChoisir == null)
            throw new DiceForgeException("AubotV2", "carteQuiApporteLePlusDePoint ne fonctionne pas");
        else
            return carteAChoisir;
    }

    private int choisirFaceDansBassinCout4Ou12() {
        return 0;
    }

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

    private void refreshInfoForgeManche() {
        compteurDeManchePasseeAForger = aForgeManche1 + aForgeManche2 + aForgeManche3 + aForgeManche4 + aForgeManche5 +aForgeManche6;
    }

    //---------------------------------------------------------------------------------------------------------------------------
    private void initValeur() {
        int l = 1;
        String ligne;
        try{
            ligne = br.readLine();
        }
        catch (IOException e){
            throw new DiceForgeException("AubotV2", "problème lors de la lecture d'une ligne");
        }
        char[] tabL1 = ligne.toCharArray();//On décompose la première ligne string en tableau d'array
        char[] tabL2et3;    //Ligne 2 et 3
        char[] tabL4et5et6; //Ligne 4 à 6
        char[] tabL7et8;    //Ligne 7 et 8
        while (ligne != null) {
            switch (l) {
                case 1: {//première ligne, voir commentaire de la classe si il y en a
                    for (int i = 0; i < orPourForgerManche.length + 1; i++) {
                        if (i == 0)
                            nombreDeTourForgeOptimal = (int) tabL1[i] - 48;
                        else if (i > 0)
                            orPourForgerManche[i - 1] = (int) tabL1[i] - 48;
                    }
                    break;
                }
                case 2: {//deuxième ligne
                    tabL2et3 = ligne.toCharArray();
                    for (int i = 0; i < nombreDeSoleilPourRejouer.length; i++)
                        nombreDeSoleilPourRejouer[i] = tabL2et3[i] - 48;
                    break;
                }
                case 3: {//etc ..
                    tabL2et3 = ligne.toCharArray();
                    for (int i = 0; i < nombreDeLunePourRejouer.length; i++)
                        nombreDeLunePourRejouer[i] = tabL2et3[i] - 48;
                    break;
                }
                case 4: {
                    tabL4et5et6 = ligne.toCharArray();
                    for (int i = 0; i < ordrePrioBassinManche[0].length; i++) {
                        switch ((int) tabL4et5et6[i] - 48) {
                            case 0:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout2FaceOr;
                                break;
                            case 1:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout2FaceLune;
                                break;
                            case 2:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout3FaceOr;
                                break;
                            case 3:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout3FaceSoleil;
                                break;
                            case 4:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout4;
                                break;
                            case 5:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout5;
                                break;
                            case 6:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout6;
                                break;
                            case 7:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout8FacePdg;
                                break;
                            case 8:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout8FaceSoleil;
                                break;
                            case 9:
                                ordrePrioBassinManche[0][i] = Bassin.typeBassin.Cout12;
                                break;
                        }
                    }
                    break;
                }
                case 5: {
                    tabL4et5et6 = ligne.toCharArray();
                    for (int i = 0; i < ordrePrioBassinManche[1].length; i++) {
                        switch ((int) tabL4et5et6[i] - 48) {
                            case 0:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout2FaceOr;
                                break;
                            case 1:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout2FaceLune;
                                break;
                            case 2:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout3FaceOr;
                                break;
                            case 3:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout3FaceSoleil;
                                break;
                            case 4:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout4;
                                break;
                            case 5:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout5;
                                break;
                            case 6:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout6;
                                break;
                            case 7:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout8FacePdg;
                                break;
                            case 8:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout8FaceSoleil;
                                break;
                            case 9:
                                ordrePrioBassinManche[1][i] = Bassin.typeBassin.Cout12;
                                break;
                        }
                    }
                }
                case 6: {
                    tabL4et5et6 = ligne.toCharArray();
                    for (int i = 0; i < ordrePrioBassinManche[2].length; i++) {
                        switch ((int) tabL4et5et6[i] - 48) {
                            case 0:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout2FaceOr;
                            case 1:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout2FaceLune;
                                break;
                            case 2:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout3FaceOr;
                                break;
                            case 3:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout3FaceSoleil;
                                break;
                            case 4:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout4;
                                break;
                            case 5:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout5;
                                break;
                            case 6:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout6;
                                break;
                            case 7:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout8FacePdg;
                                break;
                            case 8:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout8FaceSoleil;
                                break;
                            case 9:
                                ordrePrioBassinManche[2][i] = Bassin.typeBassin.Cout12;
                                break;
                        }
                    }
                    break;
                }
                case 7: {
                    tabL7et8 = ligne.toCharArray();
                    for (int indice = 0; indice < ordrePrioCarte.length; indice++) {
                        if (tabL7et8[indice] == 'a')
                            ordrePrioCarte[indice] = Carte.Noms.Marteau;
                        else if (tabL7et8[indice] == 'b')
                            ordrePrioCarte[indice] = Carte.Noms.Coffre;
                        else if (tabL7et8[indice] == 'c')
                            ordrePrioCarte[indice] = Carte.Noms.Biche;
                        else if (tabL7et8[indice] == 'd')
                            ordrePrioCarte[indice] = Carte.Noms.Ours;
                        else if (tabL7et8[indice] == 'e')
                            ordrePrioCarte[indice] = Carte.Noms.Satyres;
                        else if (tabL7et8[indice] == 'f')
                            ordrePrioCarte[indice] = Carte.Noms.Sanglier;
                        else if (tabL7et8[indice] == 'g')
                            ordrePrioCarte[indice] = Carte.Noms.Passeur;
                        else if (tabL7et8[indice] == 'h')
                            ordrePrioCarte[indice] = Carte.Noms.Cerbere;
                        else if (tabL7et8[indice] == 'i')
                            ordrePrioCarte[indice] = Carte.Noms.CasqueDinvisibilite;
                        else if (tabL7et8[indice] == 'j')
                            ordrePrioCarte[indice] = Carte.Noms.Cancer;
                        else if (tabL7et8[indice] == 'k')
                            ordrePrioCarte[indice] = Carte.Noms.Sentinelle;
                        else if (tabL7et8[indice] == 'l')
                            ordrePrioCarte[indice] = Carte.Noms.Hydre;
                        else if (tabL7et8[indice] == 'm')
                            ordrePrioCarte[indice] = Carte.Noms.Typhon;
                        else if (tabL7et8[indice] == 'n')
                            ordrePrioCarte[indice] = Carte.Noms.Sphinx;
                        else if (tabL7et8[indice] == 'o')
                            ordrePrioCarte[indice] = Carte.Noms.Cyclope;
                        else if (tabL7et8[indice] == 'p')
                            ordrePrioCarte[indice] = Carte.Noms.MiroirAbyssal;
                        else if (tabL7et8[indice] == 'q')
                            ordrePrioCarte[indice] = Carte.Noms.Meduse;
                        else if (tabL7et8[indice] == 'r')
                            ordrePrioCarte[indice] = Carte.Noms.Triton;
                        else if (tabL7et8[indice] == 's')
                            ordrePrioCarte[indice] = Carte.Noms.Minautore;
                        else if (tabL7et8[indice] == 't')
                            ordrePrioCarte[indice] = Carte.Noms.Bouclier;
                        else if (tabL7et8[indice] == 'u')
                            ordrePrioCarte[indice] = Carte.Noms.Hibou;
                        else if (tabL7et8[indice] == 'v')
                            ordrePrioCarte[indice] = Carte.Noms.VoileCeleste;
                        else if (tabL7et8[indice] == 'w')
                            ordrePrioCarte[indice] = Carte.Noms.HerbesFolles;
                        else if (tabL7et8[indice] == 'x')
                            ordrePrioCarte[indice] = Carte.Noms.Ancien;
                    }
                    break;
                }
                case 8: {
                    tabL7et8 = ligne.toCharArray();
                    for (int i = 0; i < nombreCarteMax.length; i++)
                        nombreCarteMax[i] = (int) tabL7et8[i] - 48;
                    break;
                }
            }
            try {
                ligne = br.readLine(); // On passe à la ligne suivante
            } catch (IOException exception) {
                throw new DiceForgeException("AubotV2", "erreur lors de la lecture d'une ligne");
            }
            l++; // Pour indiquer qu'on est à la ligne suivante
        }
        try {
            br.close();
            fr.close();
        } catch (IOException exception) {
           throw  new  DiceForgeException("AuborV2", "Erreur lors de la fermeture du fichier: " + exception);
        }
    }

    private void initBuffers() {
        switch (nombreDeJoueurs){
            case 2:
                pathFile += "/1V1";
                break;
            case 3:
                pathFile += "/1V1V1";
                break;
            case 4:
                pathFile += "/1V1V1V1";
                break;
        }
        this.f = new File(pathFile);
        try{
            this.fr = new FileReader(f);
        }
        catch (IOException e) {
            throw new DiceForgeException("AubotV2", "Le fichier n'a pas été trouvé !");
        }
        this.br = new BufferedReader(fr);
        this.random = new Random();
    }

    private void printInfo(){
        if (montrerInfo){
            System.out.println("nombre de tour de forge :" + nombreDeTourForgeOptimal);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("or pour forger a chaque manche: ");
            for (int or: orPourForgerManche)
                System.out.println(or);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("soleil pour rejouer: ");
            for (int soleil: nombreDeSoleilPourRejouer)
                System.out.println(soleil);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("lune pour rejouer: ");
            for (int lune: nombreDeLunePourRejouer)
                System.out.println(lune);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("priorité sur 3 premiers tours de forge:" + "\n");
            System.out.println("ordre Bassin Manche 1" + "\n");
            for (Bassin.typeBassin b: ordrePrioBassinManche[0])
                System.out.println(b);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("ordre Bassin Manche2" + "\n");
            for (Bassin.typeBassin b: ordrePrioBassinManche[1])
                System.out.println(b);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("ordre Bassin Manche 3" + "\n");
            for (Bassin.typeBassin b: ordrePrioBassinManche[2])
                System.out.println(b);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("carte prioritaire: ");
            for (Carte.Noms nom: ordrePrioCarte)
                System.out.println(nom);
            System.out.println("\n----------------------------------------------\n");
            System.out.println("carte max : ");
            for (int n: nombreCarteMax)
                System.out.println(n);
        }

    }
}
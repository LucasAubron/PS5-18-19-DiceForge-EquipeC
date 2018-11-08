package diceForge;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe joueur. Ici on utilise plus d'objet pour les ressources, mais des variables distinctes.
 * Bien entendu ça peut changer. Pourquoi faire ça :
 * Parce la plupart des choses que l'on achete ne coute que d'une ressource, donc je pense
 * qu'avoir un unique tableau de ressource compliquerait les choses.
 * La classe ne doit contenir AUCUN élément d'un bot, la classe bot (il y en aura plusieurs) sera une classe à part.
 * Ainsi elle doit permettre d'avoir une grande communication avec l'extérieur
 * Chaque joueur possède un identifiant, allant de 0 à 3 (s'il y a 4 joueurs, sinon moins)
 * qui permet d'identifier le joueur par rapport au autre (un peu comme dans une base de donnée).
 * Cette classe est abstraite, on ne peut pas en faire un objet, il faut instancier un bot
 */
abstract class Joueur {
    private int or;
    private int maxOr = 12;
    private int soleil = 0;
    private int maxSoleil = 6;
    private int lune = 0;
    private int maxLune = 6;
    private int pointDeGloire = 0;
    private int identifiant;
    private De[] des;
    private Face premierDeFaceCourante;
    private Face deuxiemeDeFaceCourante;
    private Face[] DesFaceCourante;
    private List<Carte> cartes = new ArrayList<>();
    private List<Renfort> renforts = new ArrayList<>();

    protected String affichage = "";

    enum Action {FORGER, EXPLOIT, PASSER}
    enum Renfort{ANCIEN, BICHE, HIBOU}

    Joueur(int indentifiant){
        if (identifiant < 0 || identifiant > 3)
            throw new DiceForgeException("Joueur","L'identifiant est invalide. Min : 0, max : 3, actuel : "+identifiant);
        this.identifiant = indentifiant;
        or = 3-identifiant;
        des = new De[]{new De(new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Lune(1)}}),
                new Face(new Ressource[][]{{new PointDeGloire(2)}}),
                new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Or(1)}})}),
        new De(new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Soleil(1)}}),
                new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Or(1)}})})};
    }

    int getOr() {return or;}

    void ajouterOr (int quantite){
        int ajoutOr = quantite;
        if (quantite > 0 && !getMarteau().isEmpty()){//C'est ici que l'on gere le marteau
            ajoutOr = choisirRepartitionOrMarteau(quantite);
            List<Marteau> marteaux = getMarteau();
            int i = 0;
            int restant = 0;
            while ((restant = marteaux.get(i).ajouterPoints(restant == 0 ? quantite-ajoutOr : restant)) != 0){//On ajoute la quantité de point et on regarde si elle est != 0
                if (marteaux.get(i).getNbrPointGloire() == 25) {//Si le marteau est rempli
                    ++i;//On passe au marteau suivant
                }
                if (i == marteaux.size()) {//S'il n'y a pas de marteau suivant
                    ajoutOr += restant;//On ajoute l'or que le marteau n'a pas gobbé
                    break;//On arrete
                }
            }
        }
        or = (or + ajoutOr > maxOr) ? maxOr : or + ajoutOr;
        if (or < 0) or = 0;
    }

    void augmenterMaxOr(int augmentation) {maxOr += augmentation;}

    int getSoleil() {return soleil;}

    void ajouterSoleil(int quantite) {
        soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;
        if (soleil < 0) soleil = 0;
    }

    void augmenterMaxSoleil(int augmentation) {maxSoleil += augmentation;}

    int getLune() {return lune;}

    void ajouterLune(int quantite) {
        lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;
        if (lune < 0) lune = 0;
    }

    void augmenterMaxLune(int augmentation) {maxLune += augmentation;}

    void ajouterPointDeGloire(int quantite) {
        pointDeGloire += quantite;
        if (pointDeGloire < 0) pointDeGloire = 0;
    }

    int getPointDeGloire() {return pointDeGloire;}

    int getIdentifiant() {return identifiant;}

    De[] getDes() {return des;}

    De getDe(int num) {return des[num];}

    List<Renfort> getRenforts() {return renforts;}

    void ajouterRenfort(Renfort renfort){
        renforts.add(renfort);
    }

    /**
     * C'est à partir d'ice qu'on lance les des, et que les problèmes arrivent...
     * Cette version ne marche que pour la version minimale, il faudra peut etre tout refaire /!\
     */
    void lancerLesDes(){
        Face[] faces = new Face[2];
        faces[0] = des[0].lancerLeDe();//On stocke les deux lancers
        faces[1] = des[1].lancerLeDe();
        premierDeFaceCourante = faces[0];//pour l'affichage, mais ne va pas tarder à disparaitre
        deuxiemeDeFaceCourante = faces[1];

        Boolean[] gagnerFace = new Boolean[]{true, true};//Pour savoir si on ajoute a la fin les ressources de la face
        for (int i = 0; i != faces.length; ++i){//on parcours les faces que l'on a obtenu
            if (faces[i] instanceof FaceBouclier){//On traite le cas faceBouclier
                int x = 0, j = i==0?1:0;//j est 1 si i est 0, et 0 sinon
                if (faces[j].getRessource().length != 1)//Si l'autre de est une face à choix
                    x = choisirRessource(faces[j]);//le joueur choisis (oui il peut faire 2 choix différents...  :(
                for (Ressource ressource:faces[j].getRessource()[x]){
                    if (ressource.getClass().equals(faces[i].getRessource()[0][0].getClass())){
                        pointDeGloire += 5;
                        gagnerFace[i] = false;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i != gagnerFace.length; ++i)
            if (gagnerFace[i])
                gagnerRessourceFace(faces[i]);
        this.DesFaceCourante = new Face[]{premierDeFaceCourante, deuxiemeDeFaceCourante};
    }

    Face[] getDesFaceCourante(){return DesFaceCourante;}

    /**
     * Méthode à appeler lorsque le joueur est chassé
     */
    void estChasse(){
        for (Carte carte:cartes)
            if (carte.getNom().equals("Ours"))
                pointDeGloire += 3;
        lancerLesDes();
        affichage += "J"+identifiant+" est chassé\n";
    }

    /**
     * Méthode à appeler lorsque le joueur en chasse un autre
     * Elle servira uniquement lorsque l'ours sera introduit
     */
    void chasse() {
        for (Carte carte:cartes)
            if (carte.getNom().equals("Ours"))
                pointDeGloire += 3;
    }

    String returnStringRessourcesEtDes(int numeroManche){
        String res = "\nJ" + identifiant + "\t||\t";
        res += "1er dé:" +  premierDeFaceCourante.toString() + "\t||\t" + "2ème dé:"+deuxiemeDeFaceCourante.toString();
        res += "\t||\tOr: " + or + "\t||\t" + "Soleil: " + soleil + "\t||\t" + "Lune: "+lune + "\t||\t" + "PointDeGloire: " + pointDeGloire + "\n";
        return res;
    }

    /**
     * La méthode ne gére que la partie dépense et ingestion de la carte,
     * elle ne regarde pas si il reste de cette carte.
     * @param carte
     * @return true si la carte à pu être acheté, false sinon
     */
    void acheterExploit(Carte carte){
        affichage += "J"+identifiant+" achete l'exploit: "+carte+"\n";
        for (Ressource ressource:carte.getCout()){//En premier on retire les ressources au joueurs
            if (ressource instanceof Soleil)
                ajouterSoleil(-ressource.getQuantite());
            if (ressource instanceof Lune)
                ajouterLune(-ressource.getQuantite());
        }
        carte.effetDirect(this);
        cartes.add(carte);
    }

    /**
     * Permet de savoir si le joueur posséde un certaine carte
     * @param nom le nom de la carte demandé
     * @return true si le joueur possède la carte, false sinon
     */
    boolean possedeCarte(String nom){
        for (Carte carte:cartes)
            if (carte.getNom().equals(nom))
                return true;
        return false;
    }

    /**
     * @return la liste des marteaux dans la liste des cartes. C'est une liste vide s'il n'y en a pas
     */
    List<Marteau> getMarteau(){
        List<Marteau> position = new ArrayList<>();
        for (int i = 0; i != cartes.size(); ++i)
            if (cartes.get(i).getNom().equals("Marteau")) {
                Marteau marteau = (Marteau) cartes.get(i);
                position.add(marteau);
            }
        return position;
    }

    void appelerRenforts(List<Renfort> renfortsUtilisables){
        if (!renfortsUtilisables.isEmpty())
            affichage += "J"+identifiant+" appelle: ";
        for (Renfort renfort:renfortsUtilisables){
            switch (renfort){
                case ANCIEN:
                    or -= 3;
                    pointDeGloire += 4;
                    break;
                case BICHE:
                    Face face = des[choisirDeBiche()].lancerLeDe();
                    gagnerRessourceFace(face);
                case HIBOU:
                    gagnerRessourceFace(new Face(new Ressource[][]{{new Or(1)}, {new Soleil(1)}, {new Lune(1)}}));
            }
            affichage += renfort+"; ";
        }
        affichage += "\n";
    }

    /**
     * Permet de forger une face sur un dé du joueur
     */
    void forgerDe(int numDe, Face faceAForger, int numFace){
        if (numDe < 0 || numDe > 1)
            throw new DiceForgeException("Joueur","Le numéro du dé est invalide. Min : 0, max : 1, actuel : "+numDe);
        des[numDe].forger(faceAForger, numFace);
        affichage += "J"+identifiant+" forge "+faceAForger+"\n";
    }

    /**
     * Sert à additionner les points donné par les cartes.
     * Est appelé une fois à la fin de la partie
     */
    void additionnerPointsCartes() {
        affichage += "Décompte des points de J"+identifiant+": ";
        for (Carte carte:cartes){
            pointDeGloire += carte.getNbrPointGloire();
            affichage += carte.getNom()+": "+carte.getNbrPointGloire()+"; ";
        }
        affichage += "Total : +"+pointDeGloire+"\n";
    }

    /**
     * Méthode ajoutant les gains lié à une face
     * @param face
     */
    void gagnerRessourceFace(Face face) {
        int choix = 0;//Représente quelle choix le joueur prend (pour les dés à plusieurs choix)
        if (face.getRessource().length != 1)
            choix = choisirRessource(face);
        affichage += "J"+identifiant+" obtient: ";
        for (Ressource ressource : face.getRessource()[choix]) {//On regarde de quelle ressource il s'agit
            if (ressource instanceof Or) {
                ajouterOr(ressource.getQuantite());
                affichage += ressource.getQuantite() + "Or; ";
            }
            else if (ressource instanceof Soleil) {
                ajouterSoleil(ressource.getQuantite());
                affichage += ressource.getQuantite() + "Sol, ";
            }
            else if (ressource instanceof Lune) {
                ajouterLune(ressource.getQuantite());
                affichage += ressource.getQuantite() + "Lune; ";
            }
            else if (ressource instanceof PointDeGloire) {
                pointDeGloire += ressource.getQuantite();
                affichage += ressource.getQuantite() + "Pdg; ";
            }
        }
        if (face instanceof FaceSanglier) {//On gere le cas du sanglier, qui doit faire choisir au joueur maitre de la carte une ressource
            FaceSanglier faceSanglier = (FaceSanglier) face;
            faceSanglier.getJoueurMaitre().gagnerRessourceFace(
                    new Face(new Ressource[][]{
                            {new Soleil(1)},
                            {new Lune(1)},
                            {new PointDeGloire(3)}}));
        } else if (face instanceof  FaceMiroirAbyssal){
            FaceMiroirAbyssal faceMiroir = (FaceMiroirAbyssal) face;
            faceMiroir.getJoueurMaitre().gagnerRessourceFace(new Face(new Ressource[][]{{new Lune(1)}}));
        } else if (face instanceof FaceBateauCeleste) {//Si c'est une face de bateau celeste
            FaceBateauCeleste faceBateauCeleste = (FaceBateauCeleste) face;//on fait comme dans le coordinateur
            List<Bassin> bassinsAbordables = new ArrayList<>();
            for (Bassin bassin : faceBateauCeleste.getTemple().getSanctuaire())
                if (bassin.getCout() - 2 >= or)
                    bassinsAbordables.add(bassin);
            if (!bassinsAbordables.isEmpty()) {
                ChoixJoueurForge choixJoueurForge = choisirFaceAForger(bassinsAbordables, 5);//numManche au pif, parce qu'on ne le connais pas
                if (choixJoueurForge.getBassin() != null) {
                    forgerDe(choixJoueurForge.getNumDe(), choixJoueurForge.getBassin().retirerFace(choixJoueurForge.getNumFace()), choixJoueurForge.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
                    ajouterOr(-choixJoueurForge.getBassin().getCout()+2);//On oublie pas de faire payer le joueur
                }
            }
            affichage += "Face Celeste; ";
        }
        affichage += " || ";
    }

    @Override
    public String toString(){
        String s = affichage;
        affichage = "";
        return s;
    }

    /**
     * C'est une classe abstraite, on est obligé de l'override dans une classe dérivée
     * @param numManche
     * @return L'action que le bot à choisi de prendre
     */
    abstract Action choisirAction(int numManche);

    /**
     * Permet de forger une face sur le dé à partir de la liste des bassins abordables.
     * Il faut donc choisir un bassin et une face à l'intérieur de se bassin
     * @param bassins la liste des bassins abordables
     */
    abstract ChoixJoueurForge choisirFaceAForger(List<Bassin> bassins, int numManche);

    /**
     * Permet de choisir une carte parmis une liste de carte affordable
     * @return La carte choisie
     */
    abstract Carte choisirCarte(List<Carte> cartes, int numManche);

    /**
     * Permet de choisir d'effectuer une action supplémentaire
     * @return true si le bot veut une action supplémentaire, false sinon
     */
    abstract boolean choisirActionSupplementaire(int numManche);

    /**
     * Permet de choisir la répartition en or/point de marteau que le bot souhaite effectué
     * @param nbrOr l'or total disponnible
     * @return le nombre d'or que le bot souhaite garder en or.
     */
    abstract int choisirRepartitionOrMarteau(int nbrOr);

    /**
     * Permet de choisir quel renfort appeler
     * @return la liste des renforts à appeler
     */
    abstract List<Renfort> choisirRenforts(List renfortsUtilisables);

    /**
     * Permet de choisir quelle ressource le joueur choisi sur une face de dé où il y a plusieur choix possible
     * @param faceAChoix la face en question
     * @return le numéro de la face choisi
     */
    abstract int choisirRessource(Face faceAChoix);

    /**
     * La meme que la méthode au dessus, mais pour perdre la ressource
     */
    abstract int choisirRessourceAPerdre(Face faceAChoix);

    /**
     * Permet de choisir le dé à lancer lorsque le renfort BICHE est activé
     * @return 0 ou 1
     */
    abstract int choisirDeBiche();

    /**
     * Le joueur choisis à qui il veut faire forger le sanglier
     * @param joueurs la liste des joueurs présent dans le jeu
     * @return l'id du joueur que le joueur à choisi
     */
    abstract int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs);

    /**
     * Demande au joueur de forger une face
     * Utile lorsque les exploits demande de faire forger une face
     * @param face
     */
    abstract void forgerFace(Face face);

    /**
     * Demande au joueur de choisir une face parmit plusieurs
     * @param faces les faces disponibles
     * @return la face qu'il choisi
     */
    abstract int choisirFace(List<Face> faces);

    abstract int getChoisirFace();

    abstract int getChoisirDe();

    abstract int choisirFaceMiroir(Face[] tab);
}

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
    private Face[] desFaceCourante;
    private List<Carte> cartes = new ArrayList<>();
    private List<Renfort> renforts = new ArrayList<>();
    private boolean verbeux;
    private List<Jeton> jetons = new ArrayList<>();

    protected String affichage = "";

    enum Action {FORGER, EXPLOIT, PASSER}
    enum Renfort{ANCIEN, BICHE, HIBOU}
    enum Jeton {TRITON, CERBERE}
    private int dernierLanceDes;//vaut 0 si le joueur a lancé le dé 0 en dernier, 1 si c'est le cas du dé 1, 2 s'il s'agit des deux dés en même temps, sert au jetonCerbère

    Joueur(){}//Pour pouvoir indiquer quel type de joueur on veut utiliser dans main sans a avoir a mettre des arguments aléatoires (moche)

    Joueur(int indentifiant, boolean verbeux){
        System.out.println(identifiant);
        //if (identifiant < 1 || identifiant > 4)
            //throw new DiceForgeException("Joueur","L'identifiant est invalide. Min : 1, max : 4, actuel : "+identifiant);
        this.identifiant = indentifiant;
        or = 4-identifiant; // le premier joueur a 3 or, le deuxième 2 or, etc..
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
        this.verbeux = verbeux;
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

    int getDernierLanceDes(){return dernierLanceDes;}

    void setDernierLanceDes(int code){
        if (code < 0 || code > 2)
            throw new DiceForgeException("Joueur", "Le denier lancé de dés doit être un entier entre 0 et 2");
        this.dernierLanceDes = code;
    }

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
        if (pointDeGloire < 0) pointDeGloire = 0;//dans le cas où on perds plus de points de gloire qu'on ne possède à cause d'un minotaure ennemi (super rare)
    }

    int getPointDeGloire() {return pointDeGloire;}

    int getIdentifiant() {return identifiant;}

    De[] getDes() {return des;}

    De getDe(int num) {return des[num];}

    List<Renfort> getRenforts() {return renforts;}

    void ajouterRenfort(Renfort renfort){
        renforts.add(renfort);
    }

    void ajouterJeton(Jeton jeton) {jetons.add(jeton);}

    void retirerJeton(String nomJeton){
        for (Jeton jeton : this.jetons){
            if (jeton.equals(nomJeton)) {
                this.jetons.remove(jeton);
                break;
            }
        }
    }
    List<Jeton> getJetons(){return this.jetons;}

    /**
     * On lance ses dés, le résulat est stocké dans desFaceCourante, desFacesCourante est ensuite utilisé plus tard
     * pour réaliser ce pourquoi on a lancé les dés (pas toujours pour un gain ! --> minotaure, satyres)
     */
    void lancerLesDes(){
        desFaceCourante = new Face[]{des[0].lancerLeDe(), des[1].lancerLeDe()};
        setDernierLanceDes(2); //pour le jeton cerbère on indique quel est le dernier lancé de dé effectué (ici on lance les deux dés en même temps)
    }

    void gagnerRessource(){
        Boolean[] gagnerFace = new Boolean[]{true, true};//Pour savoir si on ajoute a la fin les ressources de la face
        for (int i = 0; i != desFaceCourante.length; ++i){//on parcours les desFaceCourante que l'on a obtenu
            int autreFace = i==0?1:0;//autreFace est 1 si i est 0, et 0 sinon
            if (desFaceCourante[i] instanceof FaceBouclier && desFaceCourante[autreFace].getRessource().length > 0){//On traite le cas faceBouclier
                int x = 0;
                if (desFaceCourante[autreFace].getRessource().length > 1) {//Si l'autre de est une face à choix
                    x = choisirRessource(desFaceCourante[autreFace]);//le autreFaceoueur choisis
                    gagnerRessourceFace(desFaceCourante[autreFace], x);//Il gagne les ressources conformément à son choix
                    gagnerFace[autreFace] = false;
                }
                for (Ressource ressource:desFaceCourante[autreFace].getRessource()[x]){
                    if (ressource.getClass().equals(desFaceCourante[i].getRessource()[0][0].getClass())){
                        pointDeGloire += 5;
                        gagnerFace[i] = false;
                        break;
                    }
                }
            }
            else if (desFaceCourante[i] instanceof FaceX3){//Si c'est une faceX3
                if (desFaceCourante[autreFace].getRessource().length > 0){//Si l'autre face est commune
                    gagnerFace[autreFace] = false;
                    int x = 0;
                    if (desFaceCourante[autreFace].getRessource().length > 1)
                        x = choisirRessource(desFaceCourante[autreFace]);
                    for (int j = 0; j != 3; ++j)//On applique la récompense 3x
                        gagnerRessourceFace(desFaceCourante[autreFace], x);
                }
                if (desFaceCourante[autreFace] instanceof FaceBateauCeleste){//Si c'est une face bateau celeste
                    gagnerFace[autreFace] = false;
                    FaceBateauCeleste faceBateauCeleste = (FaceBateauCeleste) desFaceCourante[autreFace];
                    faceBateauCeleste.multiplierX3Actif();//On l'active avec le bonus
                    faceBateauCeleste.effetActif(this);
                }
                else if (desFaceCourante[autreFace] instanceof FaceMiroirAbyssal){
                    FaceMiroirAbyssal faceMiroirAbyssal = (FaceMiroirAbyssal) desFaceCourante[autreFace];
                    int choix = choisirFacePourGagnerRessource(faceMiroirAbyssal.obtenirFacesAdversaires());
                    for (int j = 0; j != 3; j++){//On l'active 3 fois avec la meme face
                        faceMiroirAbyssal.setChoix(choix);
                        faceMiroirAbyssal.effetActif(this);
                    }
                }
            }
        }

        for (int i = 0; i != gagnerFace.length; ++i)
            if (gagnerFace[i])
                gagnerRessourceFace(desFaceCourante[i]);
    }

    Face[] getDesFaceCourante(){return desFaceCourante;}

    /**
     * Méthode à appeler lorsque le joueur est chassé
     */
    void estChasse(){
        for (Carte carte:cartes)
            if (carte.getNom() == Carte.Noms.Ours)
                pointDeGloire += 3;
        lancerLesDes();
        gagnerRessource();
    }

    /**
     * Méthode à appeler lorsque le joueur en chasse un autre
     * Elle servira uniquement lorsque l'ours sera introduit
     */
    void chasse() {
        for (Carte carte:cartes)
            if (carte.getNom() == Carte.Noms.Ours)
                pointDeGloire += 3;
    }

    /**
     * La méthode ne gére que la partie dépense et ingestion de la carte,
     * elle ne regarde pas si il reste de cette carte.
     * @param carte
     * @return true si la carte à pu être acheté, false sinon
     */
    void acheterExploit(Carte carte){
        if (verbeux) affichage += "J"+identifiant+" achete l'exploit: "+carte+"\n";
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
            if (cartes.get(i).getNom() == Carte.Noms.Marteau) {
                Marteau marteau = (Marteau) cartes.get(i);
                position.add(marteau);
            }
        return position;
    }

    void appelerRenforts(List<Renfort> renfortsUtilisables){
        if (!renfortsUtilisables.isEmpty() && verbeux)
            affichage += "J"+identifiant+" appelle: ";
        for (Renfort renfort:renfortsUtilisables){
            if (verbeux) affichage += renfort.toString()+"; ";
            switch (renfort){
                case ANCIEN:
                    or -= 3;
                    pointDeGloire += 4;
                    break;
                case BICHE:
                    int choix = choisirDeBiche();
                    Face face = des[choix].lancerLeDe();
                    setDernierLanceDes(choix);
                    gagnerRessourceFace(face);
                    break;
                case HIBOU:
                    gagnerRessourceFace(new Face(new Ressource[][]{{new Or(1)}, {new Soleil(1)}, {new Lune(1)}}));
                    break;
            }
        }
    }

    /**
     * Permet de forger une face sur un dé du joueur, prends en argument:
     * (numéro du dé sur lequel on forge,
     * Face a forger,
     * numéro de la face à remplacer)
     */
    void forgerDe(int numDe, Face faceAForger, int numFace){
        if (numDe < 0 || numDe > 1)
            throw new DiceForgeException("Joueur","Le numéro du dé est invalide. Min : 0, max : 1, actuel : "+numDe);
        des[numDe].forger(faceAForger, numFace);
    }

    /**
     * Sert à additionner les points donné par les cartes.
     * Est appelé une fois à la fin de la partie
     */
    void additionnerPointsCartes() {
        for (Carte carte:cartes){
            pointDeGloire += carte.getNbrPointGloire();
        }
    }

    /**
     * Méthode ajoutant les gains lié à une face, avec un choix (si face à choix) prédéfini
     * @param face
     */
    void gagnerRessourceFace(Face face, int choix){
        if (face.getRessource().length > 0) {
            for (Ressource ressource : face.getRessource()[choix]) {//On regarde de quelle ressource il s'agit
                if (ressource instanceof Or) {
                    ajouterOr(ressource.getQuantite());
                } else if (ressource instanceof Soleil) {
                    ajouterSoleil(ressource.getQuantite());
                } else if (ressource instanceof Lune) {
                    ajouterLune(ressource.getQuantite());
                } else if (ressource instanceof PointDeGloire) {
                    pointDeGloire += ressource.getQuantite();
                }
            }
        }
        face.effetActif(this);
    }

    /**
     * Méthode ajoutant les gains lié à une face
     * @param face
     */
    void gagnerRessourceFace(Face face) {
        int choix = 0;
        if (face.getRessource().length > 1)
            choix = choisirRessource(face);
        gagnerRessourceFace(face, choix);
    }


    /**
     * utile pour les bots autre que random
     * permet de chercher une face de base 1 or et de renvoyer sa position
     * @return un tableau = [numéro du dé, numéro de la face sur le dé en question]
     */
    int[] getPosFace1Or(){
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
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
    abstract ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche);

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
     * Permet de choisir quelle ressource le joueur choisi sur une face de dé où il y a plusieurs choix possible
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

    abstract int[] choisirFaceARemplacerPourMiroir();

    /**
     * Lorsqu'on doit choisir une face pour gagner les ressources indiquées dessus
     * @param faces les faces disponibles
     * @return position de la face dans la liste fournie
     */
    abstract int choisirFacePourGagnerRessource(List<Face> faces);

    abstract void utiliserJetonTriton();

    abstract void utiliserJetonCerbere();
}

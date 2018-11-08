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
public abstract class Joueur {
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
    private List<Carte> cartes = new ArrayList<>();
    private List<Renfort> renforts = new ArrayList<>();

    public enum Action {FORGER, EXPLOIT, PASSER}
    public enum Renfort{ANCIEN, BICHE, HIBOU}

    public Joueur(int indentifiant){
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

    public int getOr() {return or;}

    public void ajouterOr (int quantite){
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

    public void augmenterMaxOr(int augmentation) {maxOr += augmentation;}

    public int getSoleil() {return soleil;}

    public void ajouterSoleil(int quantite) {
        soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;
        if (soleil < 0) soleil = 0;
    }

    public void augmenterMaxSoleil(int augmentation) {maxSoleil += augmentation;}

    public int getLune() {return lune;}

    public void ajouterLune(int quantite) {
        lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;
        if (lune < 0) lune = 0;
    }

    public void augmenterMaxLune(int augmentation) {maxLune += augmentation;}

    public void ajouterPointDeGloire(int quantite) {
        pointDeGloire += quantite;
        if (pointDeGloire < 0) pointDeGloire = 0;
    }

    public int getPointDeGloire() {return pointDeGloire;}

    public int getIdentifiant() {return identifiant;}

    public De[] getDes() {return des;}

    public De getDe(int num) {return des[num];}

    public List<Renfort> getRenforts() {return renforts;}

    public void ajouterRenfort(Renfort renfort){
        renforts.add(renfort);
    }

    /**
     * C'est à partir d'ice qu'on lance les des, et que les problèmes arrivent...
     * Cette version ne marche que pour la version minimale, il faudra peut etre tout refaire /!\
     */
    public void lancerLesDes(){
        for (De de:des){
            Face face = de.lancerLeDe();
            if (de == des[0])//Pour l'affichage
                this.premierDeFaceCourante = face;
            else
                this.deuxiemeDeFaceCourante = face;
            gagnerRessourceFace(face);
        }
    }

    /**
     * Méthode à appeler lorsque le joueur est chassé
     */
    public void estChasse(){
        if(possedeCarte("Ours"))
            pointDeGloire += 3;
        lancerLesDes();
    }

    /**
     * Méthode à appeler lorsque le joueur en chasse un autre
     * Elle servira uniquement lorsque l'ours sera introduit
     */
    public void chasse() {
        if(possedeCarte("Ours"))
            pointDeGloire += 3;
    }

    public String returnStringRessourcesEtDes(int numeroManche){
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
    public void acheterExploit(Carte carte){
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
    public boolean possedeCarte(String nom){
        for (Carte carte:cartes)
            if (carte.getNom().equals(nom))
                return true;
        return false;
    }

    /**
     * @return la liste des marteaux dans la liste des cartes. C'est une liste vide s'il n'y en a pas
     */
    public List<Marteau> getMarteau(){
        List<Marteau> position = new ArrayList<>();
        for (int i = 0; i != cartes.size(); ++i)
            if (cartes.get(i).getNom().equals("Marteau")) {
                Marteau marteau = (Marteau) cartes.get(i);
                position.add(marteau);
            }
        return position;
    }

    public void appelerRenforts(List<Renfort> renfortsUtilisables){
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
        }
    }

    /**
     * Permet de forger une face sur un dé du joueur
     */
    public void forgerDe(int numDe, Face faceAForger, int numFace){
        if (numDe < 0 || numDe > 1)
            throw new DiceForgeException("Joueur","Le numéro du dé est invalide. Min : 0, max : 1, actuel : "+numDe);
        des[numDe].forger(faceAForger, numFace);
    }

    /**
     * Sert à additionner les points donné par les cartes.
     * Est appelé une fois à la fin de la partie
     */
    public void additionnerPointsCartes() {
        for (Carte carte:cartes){
            pointDeGloire += carte.getNbrPointGloire();
        }
    }

    /**
     * Méthode ajoutant les gains lié à une face
     * @param face
     */
    public void gagnerRessourceFace(Face face) {
        int choix = 0;//Représente quelle choix le joueur prend (pour les dés à plusieurs choix)
        if (face.getRessource().length != 1)
            choix = choisirRessource(face);
        for (Ressource ressource : face.getRessource()[choix]) {//On regarde de quelle ressource il s'agit
            if (ressource instanceof Or)
                ajouterOr(ressource.getQuantite());
            else if (ressource instanceof Soleil)
                ajouterSoleil(ressource.getQuantite());
            else if (ressource instanceof Lune)
                ajouterLune(ressource.getQuantite());
            else if (ressource instanceof PointDeGloire)
                pointDeGloire += ressource.getQuantite();
        }
        if (face instanceof FaceSanglier) {//On gere le cas du sanglier, qui doit faire choisir au joueur maitre de la carte une ressource
            FaceSanglier faceSanglier = (FaceSanglier) face;
            faceSanglier.getJoueurMaitre().gagnerRessourceFace(new Face(new Ressource[][]{{new Soleil(1)}, {new Lune(1)}, {new PointDeGloire(3)}}));
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
        }
    }

    /**
     * C'est une classe abstraite, on est obligé de l'override dans une classe dérivée
     * @param numManche
     * @return L'action que le bot à choisi de prendre
     */
    public abstract Action choisirAction(int numManche);

    /**
     * Permet de forger une face sur le dé à partir de la liste des bassins abordables.
     * Il faut donc choisir un bassin et une face à l'intérieur de se bassin
     * @param bassins la liste des bassins abordables
     */
    public abstract ChoixJoueurForge choisirFaceAForger(List<Bassin> bassins, int numManche);

    /**
     * Permet de choisir une carte parmis une liste de carte affordable
     * @return La carte choisie
     */
    public abstract Carte choisirCarte(List<Carte> cartes, int numManche);

    /**
     * Permet de choisir d'effectuer une action supplémentaire
     * @return true si le bot veut une action supplémentaire, false sinon
     */
    public abstract boolean choisirActionSupplementaire(int numManche);

    /**
     * Permet de choisir la répartition en or/point de marteau que le bot souhaite effectué
     * @param nbrOr l'or total disponnible
     * @return le nombre d'or que le bot souhaite garder en or.
     */
    public abstract int choisirRepartitionOrMarteau(int nbrOr);

    /**
     * Permet de choisir quel renfort appeler
     * @return la liste des renforts à appeler
     */
    public abstract List<Renfort> choisirRenforts(List renfortsUtilisables);

    /**
     * Permet de choisir quelle ressource le joueur choisi sur une face de dé où il y a plusieur choix possible
     * @param faceAChoix la face en question
     * @return le numéro de la face choisi
     */
    public abstract int choisirRessource(Face faceAChoix);

    /**
     * La meme que la méthode au dessus, mais pour perdre la ressource
     */
    public abstract int choisirRessourceAPerdre(Face faceAChoix);

    /**
     * Permet de choisir le dé à lancer lorsque le renfort BICHE est activé
     * @return 0 ou 1
     */
    public abstract int choisirDeBiche();

    /**
     * Le joueur choisis à qui il veut faire forger le sanglier
     * @param joueurs la liste des joueurs présent dans le jeu
     * @return l'id du joueur que le joueur à choisi
     */
    public abstract int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs);

    /**
     * Demande au joueur de forger une face
     * Utile lorsque les exploits demande de faire forger une face
     * @param face
     */
    public abstract void forgerFace(Face face);

    /**
     * Demande au joueur de choisir une face parmit plusieurs
     * @param faces les faces disponibles
     * @return la face qu'il choisi
     */
    public abstract int choisirFace(List<Face> faces);
}

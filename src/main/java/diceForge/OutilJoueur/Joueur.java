package diceForge.OutilJoueur;

import diceForge.Cartes.Carte;
import diceForge.Cartes.Marteau;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.Faces.FaceBouclier;
import diceForge.Faces.FaceMiroirAbyssal;
import diceForge.Faces.FaceVoileCeleste;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;
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

    //Attributs ------------------------------------------------------------------------------------------------

    private Plateau plateau;
    private int or;
    private int maxOr = 12;
    private int soleil = 0;
    private int maxSoleil = 6;
    private int lune = 0;
    private int maxLune = 6;
    private int pointDeGloire = 0;
    private int identifiant;
    private De[] des;
    private List<Carte> cartes = new ArrayList<>();
    private List<Renfort> renforts = new ArrayList<>();
    private List<Jeton> jetons = new ArrayList<>();
    protected Afficheur afficheur; //was protected

    private boolean jetRessourceOuPdg = false;
    private boolean jetOrOuPdg = false;

    protected String affichage = "";

    public enum Action {FORGER, EXPLOIT, PASSER}
    public enum Renfort{ANCIEN, BICHE, HIBOU}
    public enum Jeton {TRITON, CERBERE}
    public enum Bot{RandomBot, EasyBot, TestBot, PlanteBot, AubronBot, AubronBotV2, RomanetBot, NidoBot, NidoBotV2, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10}
    public enum choixJetonTriton{Rien, Or, Soleil, Lune}

    private int dernierLanceDes;//vaut 0 si le joueur a lancé le dé 0 en dernier, 1 si c'est le cas du dé 1, 2 s'il s'agit des deux dés en même temps, sert au jetonCerbère

    //Constructeur --------------------------------------------------------------------------------------------------------------------------------------
    /**
     * @param identifiant comprit entre 1 et 4 inclus
     * @param afficheur
     */
    public Joueur(int identifiant, Afficheur afficheur, Plateau plateau){
        this.afficheur = afficheur;
        if (identifiant < 1 || identifiant > 4)
            throw new DiceForgeException("Joueur","L'identifiant est invalide. Min : 1, max : 4, actuel : "+identifiant);
        this.identifiant = identifiant;
        or = 4-identifiant; // le premier joueur a 3 or, le deuxième 2 or, etc..

        Face unOr = new Face(new Ressource(1, Ressource.type.OR));
        Face unSoleil = new Face(new Ressource(1, Ressource.type.SOLEIL));
        Face uneLune = new Face(new Ressource(1, Ressource.type.LUNE));
        Face deuxPdg = new Face(new Ressource(2, Ressource.type.PDG));


        des = new De[]{
                new De(new Face[]{unOr, unOr, unOr, unOr, uneLune, deuxPdg }, afficheur, this, 0),
                new De(new Face[]{unOr, unOr, unOr, unOr, unOr, unSoleil}, afficheur, this, 1)};
        this.plateau = plateau;
    }

    //Méthodes définies ------------------------------------------------------------------------------------------------------------------------------------

    // -------- Gestion des ressources et des jetons -----------

    public int getOr() {return or;}

    public int getMaxOr(){return maxOr;} //sert uniquement à l'affichage

    public void ajouterOr (int quantite){
        int orAGarder = quantite;
        if (quantite > 0 && !getMarteau().isEmpty()){//C'est ici que l'on gere le marteau
            orAGarder = choisirOrQueLeMarteauNePrendPas(quantite);
            afficheur.remplissageMarteau(this, orAGarder, quantite);
            List<Marteau> marteaux = getMarteau();
            int i = 0;
            int restant = 0;
            while ((restant = marteaux.get(i).ajouterPoints(restant == 0 ? quantite-orAGarder : restant)) != 0){//On ajoute la quantité de point
                if (marteaux.get(i).getNbrPointGloire() == 25) {//Si le marteau est rempli                    //et on regarde si elle est != 0
                    ++i;//On passe au marteau suivant
                }
                if (i == marteaux.size()) {//S'il n'y a pas de marteau suivant
                    orAGarder += restant;//On ajoute l'or que le marteau n'a pas utilisé
                    break;//On arrête
                }
            }
        }
        or = (or + orAGarder > maxOr) ? maxOr : or + orAGarder;
        if (or < 0) or = 0;
    }

    public void augmenterMaxOr(int augmentation) {maxOr += augmentation;}

    public int getSoleil() {return soleil;}

    public int getMaxSoleil(){return maxSoleil;} //idem (juste pour l'affichage)

    public void ajouterSoleil(int quantite) {
        soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;
        if (soleil < 0) soleil = 0;
    }

    public void augmenterMaxSoleil(int augmentation) {maxSoleil += augmentation;}

    public int getLune() {return lune;}

    public int getMaxLune() {return maxLune;}

    public void ajouterLune(int quantite) {
        lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;
        if (lune < 0) lune = 0;
    }

    public void augmenterMaxLune(int augmentation) {maxLune += augmentation;}

    public void ajouterPointDeGloire(int quantite) {
        pointDeGloire += quantite;
        if (pointDeGloire < 0) pointDeGloire = 0;//dans le cas où on perds plus de points de gloire qu'on ne possède à cause d'un minotaure ennemi (super rare)
    }

    public int getPointDeGloire() {return pointDeGloire;}


    //----------- Reste des getter et setter --------------

    public int getDernierLanceDes(){return dernierLanceDes;} //Pour le jeton cerbère, pour savoir quel(s) dé(s)
                                                             //On été lancé(s) en dernier
    public int getIdentifiant() {return identifiant;}

    public De[] getDes() {return des;}

    public De getDe(int num) {return des[num];}

    public List<Renfort> getRenforts() {return renforts;}

    public void ajouterRenfort(Renfort renfort){
        renforts.add(renfort);
    }

    public void ajouterJeton(Jeton jeton) {jetons.add(jeton);}

    public void setDernierLanceDes(int code){// Pour le jeton cerbère (savoir si on est en présence d'une faveur mineure
        if (code < 0 || code > 2)     // ou d'une faveur des dieux, et dans le cas de la faveur mineur quel dé a été utilisé
            throw new DiceForgeException("Joueur", "Le denier lancé de dés doit être un entier entre 0 et 2");
        this.dernierLanceDes = code;
    }

    public void retirerJeton(Jeton jetonARetirer){
        for (Jeton jeton : jetons){
            if (jeton == jetonARetirer) {
                jetons.remove(jeton);
                break;
            }
        }
    }

    public List<Carte> getCartes(){ return  cartes; }

    protected Plateau getPlateau(){return plateau;}

    // Reste des méthodes non abstract -------------------------------------------------------------------------
    /**
     * On lance ses dés, le résulat est stocké dans l'attribut faceActive de chaque dé. On
     * ne gagne pas directement le résultat. Pourquoi ? Car cela permet d'une part d'épurer les méthodes
     * mais surtout parce que un lancer n'est pas toujours synonyme de gain (satyres, minautore).
     * De plus si un jour obtient un miroir abyssal il doit avoir accès aux résultats des autres joueurs,
     * et s'il veut utiliser un jeton cerbère il doit pouvoir avoir accès a ses propres résultats.
     * pour réaliser ce pourquoi on a lancé les dés (pas toujours pour un gain ! --> minotaure, satyres)
     */
    public void lancerLesDes(){
        afficheur.lancerDes(this);
        for (De de:des)
            de.lancerLeDe();
        setDernierLanceDes(2); //pour le jeton cerbère on indique quel est le dernier lancé de dé effectué (ici on lance les deux dés en même temps)
        afficheur.retourALaLigne();
    }

    /**
     * Méthode nécessairement longue :)
     * Si une face X3 ou bouclier arrive jusqu'ici, c'est qu'elle a été obtenue
     * en faveur mineure, donc les faces X3 sont ignorées et les faces boucliers sont
     * considérées comme des faces simples !
     * @param face
     */
    public void gagnerRessourceFace(Face face, boolean minautore) {
       List<Ressource> ressourcesAGagner = new ArrayList<>(); //On s'arme d'une liste, car dans le cas d'une face
                                                               //addition on aura plusieurs types de ressource à faire gagner
        boolean faceSpeciale = false;
        //face spéciales ----------------------------------------------------------------------------------
        if (face.getTypeFace() == Face.typeFace.VOILECELESTE ||
                face.getTypeFace() == Face.typeFace.SANGLIER ||
                face.getTypeFace() == Face.typeFace.MIROIR ||
                face.getTypeFace() == Face.typeFace.X3){//X3 n'a pas d'effet direct lors d'une faveur mineure, mais on a besoin de prévenir qu'on traite une face spéciale
            face.effetActif(this); //l'effet est directement géré dans les classes dédiées, X3 ne fait rien
            faceSpeciale = true;
        }

        //face simple et face bouclier----------------------------------------------------------------------
        if (face.getTypeFace() == Face.typeFace.SIMPLE || face.getTypeFace() == Face.typeFace.BOUCLIER) {
            ressourcesAGagner.add(face.getRessource());
        }

        //face à choix ----------------------------------------------------------
        //A ce stade le joueur propriétaire de la carte sanglier a déjà reçu sa récompense, le
        //joueur qui a obtenu le face va pouvoir choisir et obtenir don dû ici
        if (face.estFaceAChoix()) {
            Ressource choix = choisirRessourceFaceAchoix(face.getRessources());
            ressourcesAGagner.add(choix);
        }
        //face addition -------------------------------------------------------------
        if (face.getTypeFace() == Face.typeFace.ADDITION) {
            for (Ressource ressource: face.getRessources())
                ressourcesAGagner.add(ressource);
        }

        if (ressourcesAGagner.size() > 4 || ressourcesAGagner.size() < 0) //4 car la face la plus "longue" a 4 ressources différentes;
            throw new DiceForgeException("Joueur", "un type de face n'est pas reconnu: " + face.getTypeFace());

        // c'est ici que le joueur gagne son dû
        if (!faceSpeciale) {
            for (Ressource ressource : ressourcesAGagner) {
                switch (ressource.getType()) {
                    case OR: {
                        if (jetOrOuPdg && choisirPdgPlutotQueRessource(ressource)) //Dans le cas du cyclope
                            ajouterPointDeGloire(ressource.getQuantite());  //1 or peut valoir 1pdg,
                        else {                                                //selon la décision du joueur
                            if (minautore)
                                ajouterOr(-ressource.getQuantite());
                            else
                                ajouterOr(ressource.getQuantite());
                        }
                        break;
                    }
                    case LUNE: {
                        if (jetRessourceOuPdg && choisirPdgPlutotQueRessource(ressource)) //idem, pour le cas de la sentinelle
                            ajouterPointDeGloire(ressource.getQuantite() * 2);//sauf qu'ici une lune peut valoir 2 pdg !
                        else {
                            if (minautore)
                                ajouterLune(-ressource.getQuantite());
                            else
                                ajouterLune(ressource.getQuantite());
                        }
                        break;
                    }
                    case SOLEIL: {
                        if (jetRessourceOuPdg && choisirPdgPlutotQueRessource(ressource)) //jamais deux sans trois
                            ajouterPointDeGloire(ressource.getQuantite() * 2); //idem que la lune et la sentinelle
                        else {
                            if (minautore)
                                ajouterSoleil(-ressource.getQuantite());
                            else
                                ajouterSoleil(ressource.getQuantite());
                        }
                        break;
                    }
                    case PDG: {
                        if (minautore)
                            ajouterPointDeGloire(-ressource.getQuantite());
                        else
                            ajouterPointDeGloire(ressource.getQuantite());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Lorsqu'on veut gagner ce que l'on a obtenu lors d'une faveur des dieux
     */
    public void gagnerRessourceDesDeuxDes() {
        boolean faceAyantBesoinDeLautreDe = false;
        for (De de : des)
            if (de.getFaceActive().estUneFaceAyantBesoinDuDeuxiemeDe()) {  // c'est compliqué à gérer :/
                gainAvecFacesDependantes();                                // ---->méthode à part
                faceAyantBesoinDeLautreDe = true;
            }

        //Si faceAyantBesoinDeLautreDe = true, alors on a déjà traité le résultat des dés
        if (!faceAyantBesoinDeLautreDe)      // Si on a des résultats simples à traiter
            for (De de : des)                 // --> face simple, a choix, ou addition
                gagnerRessourceFace(de.getFaceActive(), false);
    }

    /**
     * Lors d'un lancer des deux dés, il existe des combinaisons de faces spéciales qui induisent des exceptions
     * qui méritent d'être traitée individuellement, de plus(et surtout !) certaines faces ont un résultat qui
     * dépendent du résultat du second dé (coucou X3, coucou bouclier)
     * Méthode longue car on veut s'assurer que chaque cas est traité correctement.
     */
    void gainAvecFacesDependantes(){
        //on check en premier les faces miroirs, les plus simples à traiter car elles peuvent
        //utiliser des faces plus simples par la suite
        for (De de: des)
            if (de.getFaceActive().getTypeFace() == Face.typeFace.MIROIR) {
                FaceMiroirAbyssal pourAvoirAccesALaMethode = new FaceMiroirAbyssal(this, plateau.getJoueurs()); // c'est laid mais pas trouvé mieux :-|
                de.setFaceActive(pourAvoirAccesALaMethode.copierFaceSelonChoixDuJoueur(this));//on change la face active par la face choisir par le joueur
                gagnerRessourceDesDeuxDes();   // On rappelle la méthode sauf que cette fois on a changé la face active grâce à la face miroir
                break;                          // du dé par la face choisie et copiée
            }

        // Et maintenant tous les cas compliqué un par un (: !! youpi.
        int compteVoile = 0; int compteX3 = 0; int compteBouclier = 0; int compteFaceSimple = 0; int compteFaceAChoix = 0; int compteFaceAddition  = 0;
        Ressource ressourceBouclier1 = null;
        Ressource ressourceBouclier2 = null;
        Ressource[] ressourceFaceSansEffet = new Ressource[]{};
        for (De de: des){
            if (de.getFaceActive().estFaceAChoix()) {
                ressourceFaceSansEffet = de.getFaceActive().getRessources();
                compteFaceAChoix++;
            }
            if (de.getFaceActive().getTypeFace() == Face.typeFace.SIMPLE ) {
                ressourceFaceSansEffet = de.getFaceActive().getRessources();
                compteFaceSimple++;
            }
            if (de.getFaceActive().getTypeFace() == Face.typeFace.ADDITION){
                ressourceFaceSansEffet = de.getFaceActive().getRessources();
                compteFaceAddition++;
            }
            if (de.getFaceActive().getTypeFace() == Face.typeFace.VOILECELESTE)
                compteVoile++;
            if (de.getFaceActive().getTypeFace() == Face.typeFace.X3)
                compteX3++;
            if (de.getFaceActive().getTypeFace() == Face.typeFace.BOUCLIER) {
                compteBouclier++;
                if (ressourceBouclier1 == null)
                    ressourceBouclier1 = de.getFaceActive().getRessource();
                else
                    ressourceBouclier2 = de.getFaceActive().getRessource();
            }
        }
        if (compteX3 == 2){
            //rien, pas de chance !
        }
        else if (compteX3 == 1 && compteBouclier == 1){
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
        }
        else if (compteX3 == 1 && compteFaceSimple == 1){
            gagnerRessourceFace(new Face(ressourceFaceSansEffet[0]), false);
            gagnerRessourceFace(new Face(ressourceFaceSansEffet[0]), false);
            gagnerRessourceFace(new Face(ressourceFaceSansEffet[0]), false);
        }
        else if (compteX3 == 1 && compteFaceAddition == 1){
            gagnerRessourceFace(new Face(Face.typeFace.ADDITION,ressourceFaceSansEffet), false);
            gagnerRessourceFace(new Face(Face.typeFace.ADDITION,ressourceFaceSansEffet), false);
            gagnerRessourceFace(new Face(Face.typeFace.ADDITION,ressourceFaceSansEffet), false);
        }
        else if (compteX3 == 1 && compteFaceAChoix == 1){
            gagnerRessourceFace(new Face(choisirRessourceFaceAchoix(ressourceFaceSansEffet)), false);
            gagnerRessourceFace(new Face(choisirRessourceFaceAchoix(ressourceFaceSansEffet)), false);
            gagnerRessourceFace(new Face(choisirRessourceFaceAchoix(ressourceFaceSansEffet)), false);
        }
        else if (compteX3 == 1 && compteVoile == 1){
            FaceVoileCeleste faceTemp = new FaceVoileCeleste(plateau.getTemple());
            faceTemp.multiplierX3Actif();
            gagnerRessourceFace(faceTemp, false);
        }
        else if (compteBouclier == 2) {
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier2), false);
        }
        else if (compteBouclier == 1 && compteFaceSimple == 1) {
            gagnerRessourceFace(new Face(ressourceFaceSansEffet[0]), false);
            if (ressourceFaceSansEffet[0].getType() == ressourceBouclier1.getType())
                gagnerRessourceFace(new Face(new Ressource(5, Ressource.type.PDG)), false);
            else
                gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
        }
        else if (compteBouclier == 1 && compteFaceAddition == 1) {
            boolean bouclierUtilise = false;
            gagnerRessourceFace(new Face(Face.typeFace.ADDITION, ressourceFaceSansEffet), false);
            for (Ressource ressource: ressourceFaceSansEffet)
                if (ressource.getType() == ressourceBouclier1.getType()){
                    gagnerRessourceFace(new Face(new Ressource(5, Ressource.type.PDG)),false);
                    bouclierUtilise = true;
                    break;
                }
            if (!bouclierUtilise)
                gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
        }
        else if (compteBouclier == 1 && compteFaceAChoix == 1) {
            Ressource ressourceChoisie = choisirRessourceFaceAchoix(ressourceFaceSansEffet);
            gagnerRessourceFace(new Face(ressourceChoisie), false);
            if (ressourceChoisie.getType() == ressourceBouclier1.getType())
                gagnerRessourceFace(new Face(new Ressource(5, Ressource.type.PDG)), false);
            else
                gagnerRessourceFace(new Face(ressourceBouclier1), false);
        }
        else if (compteBouclier == 1 && compteVoile == 1) {
            gagnerRessourceFace(new FaceBouclier(ressourceBouclier1), false);
            gagnerRessourceFace(new FaceVoileCeleste(plateau.getTemple()), false);
        }
        else
            throw  new DiceForgeException("Joueur", "gain d'une combinaison de faces non gérée lors d'une faveur des dieux");
    }


    public void appliquerJetonTriton(choixJetonTriton choix) {
        if (choix != choixJetonTriton.Rien) {
            retirerJeton(Jeton.TRITON);
            switch (choix) {
                case Or:
                    ajouterOr(6);
                    break;
                case Soleil:
                    ajouterSoleil(2);
                    break;
                case Lune:
                    ajouterLune(2);
                    break;
            }
        }
    }

    public void appliquerJetonCerbere(){
        retirerJeton(Jeton.CERBERE);
        switch (getDernierLanceDes()){
                case 0:
                    gagnerRessourceFace(getDesFaceCourante()[0], false);
                    break;
                case 1:
                    gagnerRessourceFace(getDesFaceCourante()[1], false);
                    break;
                case 2:
                    gagnerRessourceFace(getDesFaceCourante()[0], false);
                    gagnerRessourceFace(getDesFaceCourante()[1], false);
                    break;
            }
    }

    public List<Jeton> getJetons(){return this.jetons;}

    /**
     * Pour les cartes qui demandent de choisir entre gagner la ressource ou des points de gloire
     * lorsque on trouve une ressource, c'est-à dire sentinelle et cyclope
     * @param bo true lorsque on veut que le joueur puisse choisir, false sinon
     */
    public void setJetRessourceOuPdg(boolean bo){jetRessourceOuPdg = bo;}

    public void setJetOrOuPdg(boolean bo){jetOrOuPdg = bo;}

    public Face[] getDesFaceCourante(){
        return new Face[]{des[0].getFaceActive(), des[1].getFaceActive()};
    }


    /**
     * Méthode à appeler lorsque le joueur est chassé
     */
    public void estChasse(){
        afficheur.estChasse(this);
        for (Carte carte:cartes) {
            if (carte.getNom() == Carte.Noms.Ours) {
                pointDeGloire += 3;
                afficheur.ours(this);
            }
        }
        lancerLesDes();
        gagnerRessourceDesDeuxDes();
    }

    /**
     * Méthode à appeler lorsque le joueur en chasse un autre
     * Uniquement utile pour l'ours, car sinon peu importe qui
     * est le joueur chasseur
     */
    public void chasse(){
        for (Carte carte:cartes) {
            if (carte.getNom() == Carte.Noms.Ours) {
                pointDeGloire += 3;
                afficheur.ours(this);
            }
        }
    }

    /**
     * La méthode ne gére que la partie dépense et ingestion de la carte,
     * elle ne regarde pas si il reste de cette carte.
     * @param carte
     * @return true si la carte à pu être acheté, false sinon
     */
    public void acheterExploit(Carte carte){
        afficheur.achatCarte(carte, this);
        for (Ressource ressource:carte.getCout()){//En premier on retire les ressources au joueurs
            if (ressource.getType()== Ressource.type.SOLEIL)
                ajouterSoleil(-ressource.getQuantite());
            if (ressource.getType()== Ressource.type.LUNE)
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
    public boolean possedeCarte(Carte.Noms nom){
        for (Carte carte:cartes)
            if (carte.getNom() == nom)
                return true;
        return false;
    }

    /**
     * @return la liste des marteaux dans la liste des cartes. C'est une liste vide s'il n'y en a pas
     */
    public List<Marteau> getMarteau(){
        List<Marteau> position = new ArrayList<>();
        for (int i = 0; i != cartes.size(); ++i)
            if (cartes.get(i).getNom() == Carte.Noms.Marteau) {
                Marteau marteau = (Marteau) cartes.get(i);
                position.add(marteau);
            }
        return position;
    }

    public void appelerRenforts(List<Renfort> renfortsUtilisables){
        Ressource choixRessource;
        int choixDe;
        for (Renfort renfort:renfortsUtilisables){
            switch (renfort){
                case ANCIEN:
                    ajouterOr(-3);
                    pointDeGloire += 4;
                    afficheur.ancien(this);
                    break;
                case BICHE:
                    choixDe = choisirDeFaveurMineure();
                    Face face = des[choixDe].lancerLeDe();
                    setDernierLanceDes(choixDe); //pour le jeton cerbère
                    gagnerRessourceFace(face, false);
                    for (int j = 0; j < getJetons().size() && getJetons().get(j) == Joueur.Jeton.CERBERE && utiliserJetonCerbere(); ++j)
                        appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
                    afficheur.biche(choixDe, face, this);
                    break;
                case HIBOU:
                    Ressource[] proposition = new Ressource[]{
                            new Ressource(1, Ressource.type.SOLEIL),
                            new Ressource(1, Ressource.type.LUNE),
                           new Ressource(1, Ressource.type.OR)
                    };
                    choixRessource = choisirRessourceFaceAchoix(proposition);
                    gagnerRessourceFace(new Face(choixRessource), false);
                    afficheur.hibou(this, choixRessource);
                    break;
            }
        }
    }

    /**
     * Permet de forger une face spéciale sur un dé du joueur selon le choix du joueur
     * Sert uniquement pour la forge de face spéciale puisque la forge
     * "normale" se fait par choisirFaceAForgerEtARemplacer
     */
    public void forgerFaceSpeciale(Face faceSpeciale){
        int[] choix = choisirOuForgerFaceSpeciale(faceSpeciale);
        int numDe = choix[0];
        int numFaceSurDe = choix[1];
        if (numDe < 0 || numDe > 1)
            throw new DiceForgeException("Joueur","Le numéro du dé est invalide. Min : 0, max : 1, actuel : "+numDe);
        des[numDe].forger(faceSpeciale, numFaceSurDe);
    }

    /**
     * Sert à additionner les points donné par les cartes.
     * Est appelé une fois à la fin de la partie
     */
    public void additionnerPointsCartes() {
        for (Carte carte:cartes){
            pointDeGloire += carte.getNbrPointGloire();
            if (carte.getNom() == Carte.Noms.Typhon) { // le typhon donne des point supplémentaires en fonction du nombre de faces forgées
                int pointBonusTyphon = 0; //pour l'afficheur
                for (De de : des) {
                    pointBonusTyphon += de.getNbrFaceForge();
                    pointDeGloire += de.getNbrFaceForge();
                }
                afficheur.typhonPointBonus(pointBonusTyphon);
            }
        }
    }

    @Override
    public String toString(){
        String s = affichage;
        affichage = "joueur n°" + identifiant;
        return s;
    }

    // Méthodes abstract synonymes de choix à faire par les joueurs, à redéfinir dans chacun des bots -------------------------------------------------

    /*LISTE DES METHODES ABSTRACT: (pour créer un bot, copier coller et enlever les tirets  en selectionnant un tiret et en utilisant alt+maj+ctrl+j)

    -public Action choisirAction(){}
    -public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins){}
    -public int[] choisirOuForgerFaceSpeciale(Face faceSpeciale){}
    -public Carte choisirCarte(List<Carte> cartes){}
    -public boolean choisirActionSupplementaire(){}
    -public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){}
    -public int choisirOrQueLeMarteauNePrendPas(int nbrOr){}
    -public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){}
    -public Face choisirFaceACopier(List<Face> faces){}
    -public Ressource choisirRessourceAPerdre(Ressource[] ressources){}
    -public int choisirDeFaveurMineure(){}
    -public int choisirDeCyclope(){}
    -public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){}
    -public choixJetonTriton utiliserJetonTriton(){}
    -public boolean utiliserJetonCerbere(){}
    -public boolean choisirPdgPlutotQueRessource(Ressource ressource){}

    16 méthodes.
    */

    /**
     * C'est une classe abstraite, on est obligé de l'override dans une classe dérivée
     * Elle permet de choisir qu'elle action le bot choisi d'effectuer
     * @return L'action que le bot à choisi de prendre
     */
    public abstract Action choisirAction();

    /**
     * Permet de forger une face sur le dé à partir de la liste des bassins abordables.
     * Il faut donc choisir un bassin et une face à l'intérieur de se bassin
     * @param bassins la liste des bassins abordables
     */
    public abstract ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins);

    /**
     * renvoie un tableau d'entier de taille 2, premier entier pour
     * donner le numéro du dé, le deuxième pour donner le numéro
     * de la face à remplacer
     * @param faceSpeciale
     * @return
     */
    public abstract int[] choisirOuForgerFaceSpeciale(Face faceSpeciale);

    /**
     * Permet de choisir une carte parmis une liste de carte abordable
     * @return La carte choisie
     */
    public abstract Carte choisirCarte(List<Carte> cartes);

    /**
     * Permet de choisir d'effectuer une action supplémentaire
     * @return true si le bot veut faire une action supplémentaire, false sinon
     */
    public abstract boolean choisirActionSupplementaire();

    /**
     * Lors d'un choix de ressource à faire
     * ---> face a choix et aile de la gardienne
     * @param ressources
     * @return
     */
    public abstract Ressource choisirRessourceFaceAchoix(Ressource[] ressources);

    /**
     * Permet de choisir la répartition en or/point de marteau que le bot souhaite effectuer
     * @param nbrOr l'or total disponnible
     * @return le nombre d'or que le bot souhaite garder en or.
     */
    public abstract int choisirOrQueLeMarteauNePrendPas(int nbrOr);

    /**
     * Permet de choisir quel renfort appeler
     * @return la liste des renforts à appeler
     */
    public abstract List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables);

    /**
     * Lorsqu'on tombe sur une face miroir, le joueur reçoit toutes les faces actives de
     * ses adversaires, il return la face qu'il veut copier.
     * Même fonctionnement pour Satyre (en fait Satyre revient à faire lancer les dés de
     * ses adversaires et ne pas leur faire gagner de ressource, puis lancer ses deux dés et tomber
     * à coup sur sur deux faces miroir !)
     * @param faces
     * @return
     */
    public abstract  Face choisirFaceACopier(List<Face> faces);

    /**
     *Pour le minotaure.
     * @param ressources
     * @return
     */
    public abstract Ressource choisirRessourceAPerdre(Ressource[] ressources);

    /**
     * Permet de choisir le dé à lancer lorsque le joueur à le droit à une (ou plusieurs) faveur mineure
     * @return 0 ou 1
     */
    public abstract int choisirDeFaveurMineure();

    /**
     * Permet de choisir le dé à lancer avec la carte cyclope.
     * N'est pas compris avec la méthode choisirDeFaveurMineure
     * car ici on a la possibilité de convertir l'or en pdg,
     * ce qui influe sur le choix
     * @return 0 ou 1
     */
    public abstract int choisirDeCyclope();

    /**
     * Le joueur choisis à qui il veut faire forger le sanglier
     * @param joueurs la liste des joueurs présent dans le jeu
     * @return l'id du joueur que le joueur à choisi, compris entre 1 et le nombre de joueur
     */
    public abstract int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs);

    /**
     * Demande au joueur s'il veut utiliser un jeton triton
     * @return son choix sous forme d'une enum
     */
    public abstract choixJetonTriton utiliserJetonTriton();

    /**
     * Demande au joueur s'il veut utiliser un jeton cerbere
     * @return true si oui, false sinon
     */
    public abstract boolean utiliserJetonCerbere();

    /**
     * Permet de choisir si le joueur veut garder la ressource ou la transformer en point de gloire
     * pour les lancers de dés obtenu avec un cyclope ou une sentinelle
     * @param ressource
     * @return true s'il veut avoir des points de gloires, false sinon
     */
    public abstract boolean choisirPdgPlutotQueRessource(Ressource ressource);
}

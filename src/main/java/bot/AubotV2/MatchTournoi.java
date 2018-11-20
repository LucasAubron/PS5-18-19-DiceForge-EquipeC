package bot.AubotV2;

import diceForge.*;

import java.util.ArrayList;
import java.util.List;

import static diceForge.Joueur.Jeton.CERBERE;
import static diceForge.Joueur.Jeton.TRITON;

public class MatchTournoi extends Coordinateur {
    private int nombreDePartieParMatch;
    private int nombreDeJoueur;
    private String[] filePath;
    private int[] resultatMatch;
    private int nbrManche;
    private PlateauTournoi plateau;

    MatchTournoi(String[] filePath, int n){
        this.nombreDePartieParMatch = n;
        this.nombreDeJoueur = filePath.length;
        this.filePath = filePath;
        this.resultatMatch = new int[nombreDeJoueur];
        this.nbrManche = (nombreDeJoueur == 3) ? 10 : 9;
        initMatch();
    }

    private void initMatch(){
        for (int partie = 0; partie < nombreDePartieParMatch; partie++){
            PlateauTournoi plateau = new PlateauTournoi(filePath);
            this.plateau = plateau;
            for (int numManche = 1; numManche <= nbrManche; ++numManche) {//C'est ici que tout le jeu se déroule
                jouerManche(numManche);
            }
            for (int i = 0; i < nombreDeJoueur; i++)
                resultatMatch[i] += plateau.getJoueurs().get(i).getPointDeGloire();
        }
    }














//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//Recopiage de coordinateur (besoin d'enlever les appels de méthode d'afficheur)
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    public void jouerManche(int numeroManche){
        for (Joueur joueur:plateau.getJoueurs()){
            tour(joueur, numeroManche);
        }
    }

    /**
     * La méthode qui gére la gestion d'un tour, a appeler par manche autant de fois qu'il y a de joueur.
     * @param joueur c'est le joueur actif
     * @param numeroManche pour plus tard, permet au bot de compter un paramètre en plus pour leur prise de décision
     */
    private void tour(Joueur joueur, int numeroManche){
        phaseLanceDe(joueur, numeroManche);
        phaseJetonCerbere(joueur, numeroManche);
        phaseRenforts(joueur, numeroManche);
        phaseJetonCerbere(joueur, numeroManche);//on redemande au joueur s'il veut utiliser son jeton cerbère car s'il a utilisé le renfort sabot d'argent il a un nouveau résultat de dé
        phaseJetonTriton(joueur, numeroManche);//le jeton triton ne peut être utilisé qu'avant une action
        if (action(joueur, numeroManche) && joueur.getSoleil()>= 2) { //si le joueur n'a pas passé son tour (== n'a pas effectué d'action) alors on lui propose de refaire une action
            phaseJetonTriton(joueur, numeroManche);//idem
            secondeAction(joueur, numeroManche);
        }
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// Ici sont écrites les méthodes utilisées pour les étape d'un tour, dans l'ordre d'éxecution (certaines méthodes utilisent d'autres méthodes private uniquement dédiées a la méthode en question, dans ce cas les "sous méthodes" sont situés juste en dessous de celle qui les utilise).

    /**
     * Méthode qui parle d'elle même, première étape d'un tour de diceforge.
     * Si il n'y a que deux joueurs, alors les dés sont lancés deux fois.
     * @param joueur
     * @param numeroManche
     */
    private void phaseLanceDe(Joueur joueur, int numeroManche){
        for (Joueur x:plateau.getJoueurs()){//En premier, tout le monde lance les dés, on stocke les résultats dans un attribut du joueur (chaque joueur a un tableau de Face qui représente ses dernier résultat)
            x.lancerLesDes();
        }
        for (Joueur x:plateau.getJoueurs()) {//et gagne les ressources correspondantes
            x.gagnerRessource();
        }
        if (plateau.getJoueurs().size() == 2) {
            for (Joueur x:plateau.getJoueurs()) {
                x.lancerLesDes();//S'il n'y a que 2 joueurs, chaque joueur lance les dés une deuxième fois
            }
            for (Joueur x:plateau.getJoueurs()) {
                x.gagnerRessource();
            }
        }
    }

    /**
     * S'occupe d'envoyer la liste des renforts activable au bot, plus particulièrement retire
     * les renforts ANCIEN qu'il ne peut pas activer faute d'or, le reste des choix liés aux autres renforts sera
     * a ajouter par la suite. Les renforts sont activés après que le joueur ait fait son choix.
     * @param joueur
     * @param numeroManche
     */
    private void phaseRenforts(Joueur joueur, int numeroManche){
        //on créé une copie de liste des renforts du joueurs, on met les renforts ANCIEN au début de la liste
        List renfortsUtilisables = new ArrayList();
        int nbrAncientAjoute = 0;
        for (Joueur.Renfort renfort:joueur.getRenforts()){
            if (renfort == Joueur.Renfort.ANCIEN && (nbrAncientAjoute+1)*3 <= joueur.getOr()){//On ajoute les anciens si le joueur peut
                renfortsUtilisables.add(renfort);
                ++nbrAncientAjoute;
            }
            else if (renfort != Joueur.Renfort.ANCIEN) {//Et on ajoute les autres
                renfortsUtilisables.add(renfort);
            }
        }
        //On demande au joueur son plan de jeu pour les renforts
        List choixDuJoueur = joueur.choisirRenforts(renfortsUtilisables);
        //On active les renforts selon les choix du joueur
        joueur.appelerRenforts(choixDuJoueur);
    }

    private void phaseJetonTriton(Joueur joueur, int numeroManche) {
        for (int i = 0; i < joueur.getJetons().size() && joueur.getJetons().get(i) == TRITON; ++i) {//On parcours tout les tritons
            Joueur.choixJetonTriton choix = joueur.utiliserJetonTriton();//On stocke le choix
            if (choix != Joueur.choixJetonTriton.Rien)//Si il veut
                joueur.appliquerJetonTriton(choix);//on l'applique
        }
    }

    private void phaseJetonCerbere(Joueur joueur, int numeroManche) {
        for (int i = 0; i < joueur.getJetons().size() && joueur.getJetons().get(i) == CERBERE && joueur.utiliserJetonCerbere(); ++i)
            joueur.appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
    }

    /**
     * Demande ce que le bot veut faire et agit en fonction de sa réponse
     * return true si le joueur effectue une action, false s'il passe son tour,
     * utile pour lui demander s'il souhaite réaliser une seconde action.
     */
    private boolean action(Joueur joueur, int numeroManche){
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                List<Bassin> bassinsAEnlever = new ArrayList<>();
                //Il faut que le joueur puisse s'arreter de forger
                int compteurForge = -1; //compteur de faces forgées, si i == 0 alors l'afficheur prévient que le joueur ne veut ou ne peut finalement pas forger (sert uniquement à l'afficheur)
                do {
                    bassinsAEnlever = forger(joueur, numeroManche, bassinsAEnlever);//On stocke le bassin à enlever pour ne pas qu'il reforge dedans
                    compteurForge++;
                }
                while(bassinsAEnlever != null);
                break;
            case EXPLOIT:
                if (cartesAbordables(joueur).isEmpty()) { //Si le bot est suffisament "stupide" pour décider d'acheter un exploit sans avoir les ressources
                    return false;
                }
                exploit(joueur, numeroManche);
                break;
            case PASSER:
                return false;//Si le joueur passe, on averti plus haut
        }
        return true;
    }

    /**
     * Méthode qui demande à un joueur de choisir la face a crafter (dans un des bassins) et la face a éliminer (sur ses dés)
     * et qui effectue l'action chosie par le joueur.
     * @return Une List représentant les bassins que le joueur à déjà utilisés, ou null si le joueur ne peut plus ou ne veut plus forger
     */
    private List<Bassin> forger(Joueur joueur, int numeroManche, List<Bassin> bassinsUtilises) {
        List<Bassin> bassinAbordable = bassinAbordable(joueur, bassinsUtilises);
        if (bassinAbordable.isEmpty()) //Si le joueur n'a pas assez d'or pour acheter la moindre face, l'action s'arrête
            return null;
        ChoixJoueurForge choixDuJoueur = joueur.choisirFaceAForgerEtARemplacer(bassinAbordable, numeroManche);//Le joueur choisi
        if (choixDuJoueur.getBassin() != null) {
            joueur.forgerDe(choixDuJoueur.getNumDe(), choixDuJoueur.getBassin().retirerFace(choixDuJoueur.getNumFace()), choixDuJoueur.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
            joueur.ajouterOr(-choixDuJoueur.getBassin().getCout());//On oublie pas de faire payer le joueur
        }
        bassinsUtilises.add(choixDuJoueur.getBassin());//on indique quel bassin a été utilisé, null si n'il y pas eu de craft (signifiant pour le joueur la volonté de s'arrêter)
        if (bassinsUtilises.get(bassinsUtilises.size()-1) == null) //Si le joueur n'a pas crafté alors cela signifie qu'il veut s'arrêter
            return null;
        return bassinsUtilises;//on retourne la liste des bassins utilisés qui grossi d'appel en appel pour restreindre les choix du joueur (uniquement durant le même tour)
    }

    /**
     * Méthode qui calcule les bassins auxquels le joueur peut encore accéder en fonction de
     * ceux qu'il a utilisé précédemment et de son or, renvoit les bassins sous forme de liste.
     * @param joueur
     * @param bassinsUtilises
     * @return
     */
    List<Bassin> bassinAbordable(Joueur joueur, List<Bassin> bassinsUtilises) {
        List<Bassin> bassinAbordable = new ArrayList<>();//On créé la liste des bassins abordables
        for (Bassin bassin : plateau.getTemple().getSanctuaire()) {
            boolean estDejaUtilise = false;
            for (Bassin x : bassinsUtilises)
                if (x.equals(bassin))//On fait attention de ne pas réutiliser un bassin déjà utilisé
                    estDejaUtilise = true;
            if (!bassin.getFaces().isEmpty() && bassin.getCout() <= joueur.getOr() && !estDejaUtilise)//Si on peut ajouter ce bassin
                bassinAbordable.add(bassin);//On l'ajoute
        }
        return bassinAbordable;
    }
    /**
     * Action exploit, on envoit la liste des cartes achetables par le joueur, celui ci choisit et l'achat est effectué dans la foulée.
     * Gère également la chasse.
     */
    private void exploit(Joueur joueur, int numeroManche) {
        List cartesAbordables = cartesAbordables(joueur); //on a déjà vérifié en amont que le joueur peut acheter au moins une carte donc la liste n'est jamais vide
        Carte carteChoisie = joueur.choisirCarte(cartesAbordables, numeroManche); //On demande au joueur la carte qu'il veut acheter
        retirerJoueurDeSonEmplacement(joueur);//le joueur dont c'est le tour quitte son emplacement actuel
        Joueur joueurChasse = null;
        for (Ile ile : plateau.getIles()) {
            for (List<Carte> paquet : ile.getCartes())
                if (!paquet.isEmpty() && paquet.get(0).equals(carteChoisie)) {
                    joueurChasse = ile.prendreCarte(joueur, carteChoisie);//Ici on l'ajoute à l'ile ou il va, on lui fait prendre sa carte et on chasse le joueur présent sur l'ile si il y en avait un
                    //Le joueur paye son dû en même temps que l'acquisition de sa carte
                }
        }
        if (joueurChasse != null) {//S'il il y a bien un joueur qui a été chassé, on le renvoi au portails originels
            plateau.getPortail().ajouterJoueur(joueurChasse);
        }
    }

    private List cartesAbordables(Joueur joueur) {
        List<Carte> cartesAbordables = new ArrayList<>();//Notre liste qui va contenir les cartes abordables par le joueur
        for (Ile ile : plateau.getIles()) {//On parcours les iles
            for (List<Carte> paquet : ile.getCartes()) {//Et les paquets
                for (Carte carte : paquet) {//Et les cartes
                    int prixSoleil = 0, prixLune = 0;
                    for (Ressource prix : carte.getCout()) {//Convertisseur object -> int des ressources
                        if (prix instanceof Soleil)
                            prixSoleil += prix.getQuantite();
                        else if (prix instanceof Lune)
                            prixLune += prix.getQuantite();
                        else//Cela ne devrait jamais arriver
                            throw new DiceForgeException("Coordinateur", "Une carte doit couter soit des lunes soit des soleils !");
                    }
                    if (prixSoleil <= joueur.getSoleil() && prixLune <= joueur.getLune())//Si le joueur peut l'acheter on l'ajoute
                        cartesAbordables.add(carte);
                }
            }
        }
        return cartesAbordables;
    }
    private void retirerJoueurDeSonEmplacement(Joueur joueur){
        for (Joueur j : plateau.getPortail().getJoueurs())//En premier, on retire le joueur s'il est situé dans les portails originels
            if (joueur.getIdentifiant() == j.getIdentifiant()) {//On teste les identifiants, c'est le plus sur
                plateau.getPortail().retirerJoueur(joueur.getIdentifiant());
                break;
            }
        for (Ile ile:plateau.getIles())
            if (ile.getJoueur() != null && joueur.getIdentifiant() == ile.getJoueur().getIdentifiant()) {
                ile.retirerJoueur();//En premier, on retire le joueur de son ile
                break;
            }
    }
    private void secondeAction(Joueur joueur, int numeroManche) {
        if (joueur.choisirActionSupplementaire(numeroManche)) {//S'il peut, et il veut, il re-agit
            joueur.ajouterSoleil(-2);
            action(joueur, numeroManche);
        }
    }

    Plateau getPlateau(){return plateau;}

    int[] getResultatMatch(){
        return resultatMatch;
    }
}

package diceForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Le coordinateur s'occupe de faire tourner le jeu.
 * Il s'occupe du déroulement des de la partie, mais aussi de déplacer les joueurs et gérer leur actions.
 * Le coordinateur est en échange permanent avec les joueurs, a chaque étape nécessitant une prise de décision,
 * il envoit toutes les possibilités aux joueurs,il s'assure lui même de les trier pour les joueurs, par exemple si
 * le joueur veut forger mais n'a que 5 or, le coordinateur ne va lui proposer que les bassins dont le coût est <=5.
 */
class Coordinateur {
    private Plateau plateau;
    private String affichage = "";

    Coordinateur(boolean modeVerbeux, Joueur[] joueurs){
        //Le constructeur est séparé en deux cas: le cas ou l'on veut une seule partie et où l'on la description des actions des bots, et le cas ou l'on veut simuler un grand nombre de partie et voir le résultat avec des statistiques
        int nbrManche = joueurs.length == 3 ? 10 : 9; //le jeu se joue en 9 manches si il y a 3 joueurs, sinon 10
        if (modeVerbeux) {
            for (int i = 0; i < joueurs.length; i++)
                System.out.println(joueurs[i]);
            plateau = new Plateau(true, joueurs);//Le plateau, qui comprend toute la partie physique du jeu
            System.out.println(plateau);
            for (int numManche = 1; numManche <= nbrManche; ++numManche) {//C'est ici que tout le jeu se déroule
                jouerManche(numManche);
            }
            List<Integer> infoJoueurGagnant = infoJoueurGagnant();//On récupère les infos du joueur gagnant
            for (Joueur joueur:plateau.getJoueur())
                affichage += joueur;
            if (infoJoueurGagnant.size() == 2)//Un gagnant
                affichage += "\n\n\n\n\t\t--------------------------------------------------\n\t\t" + "| Le joueur n°" + infoJoueurGagnant.get(1) + " gagne avec " + infoJoueurGagnant.get(0) + " points de gloire ! |\n" + "\t\t--------------------------------------------------\n";
            else {//Egalité
                affichage += "\n\n\n\n\t\t--------------------------------------------------\n\t\t" + "| Egalité entre les joueurs n°";
                for (int i = 1; i != infoJoueurGagnant.size(); ++i)
                    affichage += infoJoueurGagnant.get(i)+", ";
                affichage += "avec " + infoJoueurGagnant.get(0) + " points de gloire ! |\n " + "\t\t--------------------------------------------------\n ";
            }
        }
        else{
            int[] nbrVictoire = new int[joueurs.length];
            int[] PtsGloireCumules = new int[joueurs.length];
            int nbrPartiesJoue = 1000; //nbrPartiesJoue = 1000 parties, comme demandé dans le kata
            for (int i = 0; i != joueurs.length; ++i){
                nbrVictoire[i] = 0;//Initialisation des tableaux, a voir si on peut faire plus simple
                PtsGloireCumules[i] = 0;
            }
            for (int i = 0; i != nbrPartiesJoue; ++i){//On fait autant de partie que l'on veut
                plateau = new Plateau(false, joueurs);
                for (int numManche = 1; numManche <= nbrManche; ++numManche) {//C'est ici que tout le jeu se déroule
                    jouerManche(numManche);
                }
                List<Integer> infoJoueurGagnant = infoJoueurGagnant();
                for (int j = 1; j != infoJoueurGagnant.size(); ++j)
                    nbrVictoire[infoJoueurGagnant.get(j)]++;//Puis on stocke les infos des parties
                for (int j = 0; j != joueurs.length; ++j)
                    PtsGloireCumules[j] += plateau.getJoueur().get(j).getPointDeGloire();
            }
            for (int i = 0; i != joueurs.length; ++i){//Puis on les affiches
                affichage += "Le joueur "+i+" a gagné "+nbrVictoire[i]+" fois avec une moyenne de "+PtsGloireCumules[i]/nbrPartiesJoue+" points de gloire\n";
            }
        }
    }

    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    private void jouerManche(int numeroManche){
        for (Joueur joueur:plateau.getJoueur()){
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
        phaseRenforts(joueur, numeroManche);
        //toDo: phaseJeton: phase durant laquelle le joueur peut utiliser un jeton triton, le jeton cerbère étant utilisable juste après la phase de dés (pour doubler un résultat)
        phaseJetonTriton(joueur, numeroManche);
        if (action(joueur, numeroManche) && joueur.getSoleil()>= 2) {
            phaseJetonTriton(joueur, numeroManche);
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
        if (plateau.estVerbeux())
            affichage += "Phase de lance des des\n";

        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            x.lancerLesDes();
            if (plateau.estVerbeux())
                affichage += x;
        }
        for (Joueur x:plateau.getJoueur())//et gagne les ressources correspondantes
            x.gagnerRessource();

        if (plateau.getJoueur().size() == 2) {
            for (Joueur x:plateau.getJoueur())
                x.lancerLesDes();//S'il n'y a que 2 joueurs, chaque joueur lance les dés une deuxième fois
            for (Joueur x:plateau.getJoueur())
                x.gagnerRessource();
        }

        if (plateau.estVerbeux()) {
            affichage += ("\n--------------------------------------------------------\n" + "Manche: " + numeroManche + "\t||\t" + "Tour du joueur " + joueur.getIdentifiant() + "\t||\t" + "\n--------------------------------------------------------\n"); // annonce de la manche et du tour, les résultats des lancés ne sont pas affichés par souci de concisions
            affichage += ("Ressources disponibles:\n\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() + "\n"); //On affiche les ressources disponibles au joueur, utile pour vérifier par la suite que les ia programmées jouent de manière relativement intelligente
            if (!joueur.getMarteau().isEmpty())
                joueur.getMarteau().forEach(marteau -> affichageMarteau(marteau));
        }
    }

    private void affichageMarteau(Marteau marteau){
            if (marteau.getNiveau()==0 && marteau.getPoints()<10)
                affichage += "\tMarteau Phase I: " + marteau.getPoints() + "/10" + "\n";
            else if (marteau.getNiveau()==0 && marteau.getPoints()>=10)
                affichage += "\tMarteau Phase II: " + (marteau.getPoints() - 10) + "/15" + "\n";
            else if (marteau.getNiveau()==1)
                affichage += "\tMarteau Phase II: " + marteau.getPoints() + "/15" + "\n";
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
            if ((renfort+"").equals("ANCIEN") && (nbrAncientAjoute+1)*3 <= joueur.getOr()){//On ajoute les anciens si le joueur peut
                renfortsUtilisables.add(renfort);
                ++nbrAncientAjoute;
            }
            else if (!(renfort+"").equals("ANCIEN")) {//Et on ajoute les autres
                renfortsUtilisables.add(renfort);
            }
        }
        //On demande au joueur son plan de jeu pour les renforts
        List choixDuJoueur = joueur.choisirRenforts(renfortsUtilisables);
        //On active les renforts selon les choix du joueur
        joueur.appelerRenforts(choixDuJoueur);
        if (plateau.estVerbeux())
            for (Joueur x:plateau.getJoueur())
                affichage += x;
    }

    private void phaseJetonTriton(Joueur joueur, int numeroManche){
        if (!joueur.getJetons().isEmpty())
            for (Joueur.Jeton jeton : joueur.getJetons()){
                if (jeton.equals("TRITON"))
                    joueur.utiliserJetonTriton();
            }
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
                do
                    bassinsAEnlever = forger(joueur, numeroManche, bassinsAEnlever);//On stocke le bassin à enlever pour ne pas qu'il reforge dedans
                while(bassinsAEnlever != null);
                break;
            case EXPLOIT:
                if (cartesAbordables(joueur).isEmpty()) { //Si le bot est suffisament "stupide" pour décider d'acheter un exploit sans avoir les ressources, on affiche plutot qu'il passe son tour au lieu de laisser "le joueur achète un exploit sans rien expliciter derrière
                    if (plateau.estVerbeux())
                        affichage += "\n\t\t-- le joueur " + joueur.getIdentifiant() + " passe son tour --\n";
                    return false;
                }
                exploit(joueur, numeroManche);
                break;
            case PASSER:
                if (plateau.estVerbeux())
                    affichage += "\n\t\t-- le joueur " + joueur.getIdentifiant() + " passe son tour --\n";
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
        List<Bassin> bassinAbordable = BassinAbordable(joueur, bassinsUtilises);
        if (bassinAbordable.isEmpty()) //Si le joueur n'a pas assez d'or pour acheter la moindre face, l'action s'arrête
            return null;
        ChoixJoueurForge choixDuJoueur = joueur.choisirFaceAForgerEtARemplacer(bassinAbordable, numeroManche);//Le joueur choisi
        if (choixDuJoueur.getBassin() != null) {
            joueur.forgerDe(choixDuJoueur.getNumDe(), choixDuJoueur.getBassin().retirerFace(choixDuJoueur.getNumFace()), choixDuJoueur.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
            joueur.ajouterOr(-choixDuJoueur.getBassin().getCout());//On oublie pas de faire payer le joueur
        }
        bassinsUtilises.add(choixDuJoueur.getBassin());//on indique quel bassin a été utilisé, null si n'il y pas eu de craft (signifiant pour le joueur la volonté de s'arrêter)
        if (plateau.estVerbeux())
            for (Joueur joueur1:plateau.getJoueur())
                affichage += joueur1;
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
    private List<Bassin> BassinAbordable(Joueur joueur, List<Bassin> bassinsUtilises) {
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
        if (plateau.estVerbeux())
            for (Joueur joueur1:plateau.getJoueur())
                affichage += joueur1;
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
            if (plateau.estVerbeux()) {
                affichage += "---------- Le joueur " + joueur.getIdentifiant() + " choisi d'effectuer une seconde action ----------\n";
                affichage += ("Ressources disponibles:\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() + "\n");
            }
            action(joueur, numeroManche);
        }
        if (plateau.estVerbeux())
            for (Joueur joueur1:plateau.getJoueur())
                affichage += joueur1;
    }

    /**
     * Permet de connaitre le numéro du joueur gagnant ainsi que son nombre de point de gloire
     * Cette fonction additionne le nombre de point de gloire des cartes des joueurs, il faut donc ne l'appeler qu'une fois
     * @return une List, le premier élement est le nombre de point de gloire maximum, l'/les autre(s) est/sont le(s) numéro(s) du/des joueur(s) gagnant(s)
     */
    List<Integer> infoJoueurGagnant(){
        List<Integer> infoJoueurGagnant = new ArrayList<>();
        infoJoueurGagnant.add(0);
        infoJoueurGagnant.add(-1);
        for (Joueur joueur:plateau.getJoueur()){
            joueur.additionnerPointsCartes();
            if (joueur.getPointDeGloire() > infoJoueurGagnant.get(0)){//Simple recherche d'un maximum
                infoJoueurGagnant.set(0, joueur.getPointDeGloire());//On midifie le nbr de points de gloire maximum
                infoJoueurGagnant.subList(1, infoJoueurGagnant.size()).clear();//On clear la liste sauf le premier élément
                infoJoueurGagnant.add(joueur.getIdentifiant());//On ajoute son identifiant
            }
            else if (joueur.getPointDeGloire() == infoJoueurGagnant.get(0))
                infoJoueurGagnant.add(joueur.getIdentifiant());//On ajoute son identifiant
        }
        if (infoJoueurGagnant.get(1) == -1)
            throw new DiceForgeException("Coordinateur", "Aucun joueur a plus de 0 pt de gloire, problème !");
        return infoJoueurGagnant;
    }
    public String toString(){
        return affichage;
        }
}

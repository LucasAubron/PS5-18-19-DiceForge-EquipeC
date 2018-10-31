package diceForge;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Le coordinateur s'occupe de faire tourner le jeu.
 * Il s'occupe du déroulement des de la partie, mais aussi de déplacer les joueurs et gérer leur actions.
 * Le coordinateur est en échange permanent avec les joueurs, a chaque étape nécessitant une prise de décision,
 * il envoit toutes les possibilités aux joueurs,il s'assure lui même de les trier pour les joueurs, par exemple si
 * le joueur veut forger mais n'a que 5 or, le coordinateur ne va lui proposer que les bassins dont le coût est <=5.
 */
public class Coordinateur {
    Plateau plateau;
    private String affichage = "";

    public Coordinateur(Plateau plateau, int nbrManche){
        this.plateau = plateau;//On garde le plateau en référence
        if (nbrManche < 9 || nbrManche > 10)
            throw new DiceForgeException("Coordinateur","Le nombre de manche est invalide. Min : 9, max : 10, actuel : "+nbrManche);
        for (int numManche = 0; numManche <= nbrManche; ++numManche){//C'est ici que tout le jeu ce déroule
            jouerManche(numManche);
        }
        int[] infoJoueurGagnant = infoJoueurGagnant();//On récupère les infos du joueur gagnant
        if (plateau.estVerbeux())
            affichage += "\n\n\n\n\t\t--------------------------------------------------\n\t\t" + "| Le joueur n°"+infoJoueurGagnant[0]+" gagne avec "+infoJoueurGagnant[1]+" points de gloire ! |\n" + "\t\t--------------------------------------------------\n";
    }

    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    public void jouerManche(int numeroManche){
        for (Joueur joueur:plateau.getJoueur()){
            tour(joueur, numeroManche);
        }
    }

    /**
     * La méthode qui gére la gestion d'un tour, a appeler par manche autant de fois qu'il y a de joueur.
     * @param joueur c'est le joueur actif
     * @param numeroManche pour plus tard, lorsque les bots feront des actions différentes selon les tours
     */
    public void tour(Joueur joueur, int numeroManche){
        phaseLanceDe(joueur, numeroManche);
        phaseRenforts(joueur, numeroManche);
        //toDo: phaseJeton: phase durant laquelle le joueur peut utiliser un jeton (triton et/ou cerbère), a appeler avant chacune des deux actions
        if (action(joueur, numeroManche) && joueur.getSoleil()>= 2)
            secondeAction(joueur, numeroManche);
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
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getJoueur().size() == 2) {
                x.lancerLesDes();//S'il n'y a que 2 joueurs, chaque joueur lance les dés une deuxième fois
            }
            x.lancerLesDes();
        }
        if (plateau.estVerbeux()) {
            affichage += ("--------------------------------------------------------\n" + "Manche: " + numeroManche + "\t||\t" + "Tour du joueur " + joueur.getIdentifiant() + "\t||\t" + "\n--------------------------------------------------------\n"); // annonce de la manche et du tour, les résultats des lancés ne sont pas affichés par souci de concisions
            affichage += ("\n" + "Ressources disponibles:\n\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() + "\n"); //On affiche les ressources disponibles au joueur, utile pour vérifier par la suite que les ia programmées jouent de manière relativement intelligente
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
            if ((renfort+"").equals("ANCIEN") && (nbrAncientAjoute+1)*3 <= joueur.getOr()){//On ajoute les anciens si le joueur peut
                renfortsUtilisables.add(renfort);
                ++nbrAncientAjoute;
            }
            else if (!(renfort+"").equals("ANCIEN"))//Et on ajoute les autres
                renfortsUtilisables.add(renfort);
        }
        //On demande au joueur son plan de jeu pour les renforts
        List choixDuJoueur = joueur.choisirRenforts(renfortsUtilisables);
        //On active les renforts selon les choix du joueur
        joueur.appelerRenforts(choixDuJoueur);
        if (plateau.estVerbeux())
            choixDuJoueur.forEach(x -> affichage += "\nLe joueur " + joueur.getIdentifiant() + " active le renfort " + x + "\n");
    }
    
    /**
     * Demande ce que le bot veut faire et agit en fonction de sa réponse
     * return true si le joueur effectue une action, false s'il passe son tour,
     * utile pour lui demander s'il souhaite réaliser une seconde action.
     */
    public boolean action(Joueur joueur, int numeroManche){
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                if (plateau.estVerbeux())
                    affichage += "\n\t\t-- Le joueur " + joueur.getIdentifiant() + " choisi de forger --\n\n";
                List<Bassin> bassinsAEnlever = new ArrayList<>();
                //Il faut que le joueur puisse s'arreter de forger
                do
                    bassinsAEnlever = forger(joueur, numeroManche, bassinsAEnlever);//On stocke le bassin à enlever pour ne pas qu'il reforge dedans
                while(bassinsAEnlever != null);
                break;
            case EXPLOIT:
                if (cartesAbordables(joueur).isEmpty()) { //Si le bot est suffisament "stupide" pour décider d'acheter un exploit sans avoir les ressources, on affiche plutot qu'il passe son tour au lieu de laisser "le joueur achète un exploit sans rien expliciter derrière
                    if (plateau.estVerbeux())
                        affichage += "\n\t\t-- le joueur " + joueur.getIdentifiant() + " passe son tour --\n\n";
                    return false;
                }
                if (plateau.estVerbeux())
                    affichage += "\n\t\t-- Le joueur " + joueur.getIdentifiant() + " choisi d'accomplir un exploit --\n\n";
                exploit(joueur, numeroManche);
                break;
            case PASSER:
                if (plateau.estVerbeux())
                    affichage += "\n\t\t-- le joueur " + joueur.getIdentifiant() + " passe son tour --\n\n";
                return false;//Si le joueur passe, on averti plus haut
        }
        return true;
    }

    /**
     * Méthode qui demande à un joueur de choisir la face a crafter (dans un des bassins) et la face a éliminer (sur ses dés)
     * et qui effectue l'action chosie par le joueur.
     * @return Une List représentant les bassins que le joueur à déjà utilisés, ou null si le joueur ne peut plus ou ne veut plus forger
     */
    public List<Bassin> forger(Joueur joueur, int numeroManche, List<Bassin> bassinsUtilises) {
        List<Bassin> bassinAbordable = BassinAbordable(joueur, bassinsUtilises);
        if (bassinAbordable.isEmpty()) //Si le joueur n'a pas assez d'or pour acheter la moindre face, l'action s'arrête
            return null;
        ChoixJoueurForge choixDuJoueur = joueur.choisirFaceAForger(bassinAbordable, numeroManche);//Le joueur choisi
        if (choixDuJoueur.getBassin() != null) {
            if (plateau.estVerbeux())
                affichage += "Le joueur " + joueur.getIdentifiant() + " forge une face" + choixDuJoueur.getBassin().getFace(choixDuJoueur.getNumFace()) + " sur le dé n°" + choixDuJoueur.getNumDe() + " et remplace une face" + joueur.getDe(choixDuJoueur.getNumDe()).getFace(choixDuJoueur.getPosFace()) +"\n\n";
            joueur.forgerDe(choixDuJoueur.getNumDe(), choixDuJoueur.getBassin().retirerFace(choixDuJoueur.getNumFace()), choixDuJoueur.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
            joueur.ajouterOr(-choixDuJoueur.getBassin().getCout());//On oublie pas de faire payer le joueur (n'est-ce pas Gabriel ..)
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
    public void exploit(Joueur joueur, int numeroManche) {
        List cartesAbordables = cartesAbordables(joueur); //on a déjà vérifié en amont que le joueur peut acheter au moins une carte donc la liste n'est jamais vide
        Carte carteChoisie = joueur.choisirCarte(cartesAbordables, numeroManche); //On demande au joueur la carte qu'il veut acheter
        retirerJoueurDeSonEmplacement(joueur);//le joueur dont c'est le tour quitte son emplacement actuel
        Joueur joueurChasse = null;
        for (Ile ile : plateau.getIles()) {
            for (List<Carte> paquet : ile.getCartes()) {
                if (!paquet.isEmpty() && paquet.get(0).equals(carteChoisie)) {
                    joueurChasse = ile.prendreCarte(joueur, carteChoisie);//Ici on l'ajoute à l'ile ou il va
                }
            }
        }
        if (plateau.estVerbeux())
            affichage += "Le joueur " + joueur.getIdentifiant() + " achète une carte " + carteChoisie;
        if (joueurChasse != null) {//S'il il y a bien un joueur qui a été chassé, on le renvoi au portails originels
            plateau.getPortail().ajouterJoueur(joueurChasse);
            if (plateau.estVerbeux())
                affichage += " et chasse le joueur " + joueurChasse.getIdentifiant();
        }
        if (plateau.estVerbeux())
            affichage += "\n\n";
    }

    private List cartesAbordables(Joueur joueur) {
        List<Carte> cartesAbordables = new ArrayList<>();//Notre liste qui va contenir les cartes affordables par le joueur
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
                affichage += ("\n" + "Ressources disponibles:\n\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() + "\n");
            }
            action(joueur, numeroManche);
        }
    }

    /**
     * Permet de connaitre le numéro du joueur gagnant ainsi que son nombre de point de gloire
     * Cette fonction additionne le nombre de point de gloire des cartes des joueurs, il faut donc ne l'appeler qu'une fois
     * @return un tableau d'entier de taille 2, son premier éléments est le numéro du joueur gagnant et son second est le nombre de point de victoire du gagnant
     */
    public int[] infoJoueurGagnant(){
        int numJoueurGagnant = 0, maxPointGloire = 0;
        for (Joueur joueur:plateau.getJoueur()){
            joueur.additionnerPointsCartes();
            if (joueur.getPointDeGloire() > maxPointGloire){//Simple recherche d'un maximum
                maxPointGloire = joueur.getPointDeGloire();
                numJoueurGagnant = joueur.getIdentifiant();
            }
        }
        return new int[]{numJoueurGagnant, maxPointGloire};
    }
    public String toString(){
        return affichage;
        }
}

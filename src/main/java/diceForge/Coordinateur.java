package diceForge;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Le coordinateur s'occupe de faire tourner le jeu.
 * Il s'occupe du déroulement des manches, mais aussi de déplacer les joueurs.
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

    /**
     * Cette méthode permet de jouer une manche, elle est a appeler autant de fois qu'il y a de manche
     */
    public void jouerManche(int numeroManche){
        for (Joueur joueur:plateau.getJoueur()){
            tour(joueur, numeroManche);
        }
    }

    /**
     * La méthode qui gére la gestion d'un tour.
     * Il faut absolument faire des méthodes forger() et exploit() !
     * @param joueur c'est le joueur actif
     * @param numeroManche pour plus tard, lorsque les bots feront des actions différentes selon les tours
     */
    public void tour(Joueur joueur, int numeroManche){
        phaseLanceDe(joueur, numeroManche);
        renforts(joueur, numeroManche);
        boolean agit = actionPrincipale(joueur, numeroManche);//le joueur agit, et on regarde s'il passe son tour ou pas
        if (joueur.getSoleil() >= 2 && joueur.choisirActionSupplementaire(numeroManche) && agit) {//S'il peut, et il veut, il re-agit
           joueur.ajouterSoleil(-2);
           if (plateau.estVerbeux()) {
               affichage += "---------- Le joueur " + joueur.getIdentifiant() + " choisi d'effectuer une seconde action ----------\n";
               affichage += ("\n" + "Ressource disponibles:\n\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() + "\n");
           }
           actionPrincipale(joueur, numeroManche);
        }
    }

    /**
     * Demande ce que le bot veut faire et agit en fonction
     * @return true si le joueur effectue une action, false s'il passe son tour
     */
    public boolean actionPrincipale(Joueur joueur, int numeroManche){
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                if (plateau.estVerbeux())
                    affichage += "\n\t\t-- Le joueur " + joueur.getIdentifiant() + " choisi de forger --\n\n";
                List<Bassin> bassinsAEnlever = new ArrayList<>();
                do {//Il faut que le joueur puisse s'arreter de forger
                    bassinsAEnlever = forger(joueur, numeroManche, bassinsAEnlever);//On stocke le bassin à enlever pour ne pas qu'il reforge dedans
                } while(bassinsAEnlever != null);
                break;
            case EXPLOIT:
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
     * Méthode demande à un joueur de forger une face d'un bassin
     * @return Une List représentant les bassins que le joueur à déjà utilisés, ou null si le joueur ne peut plus forger
     */
    public List<Bassin> forger(Joueur joueur, int numeroManche, List<Bassin> bassinsUtilises) {
        List<Bassin> bassinAbordable = new ArrayList<>();//On créé la liste des bassins abordables
        for (Bassin bassin : plateau.getTemple().getSanctuaire()) {
            boolean forge = true;
            for (Bassin x:bassinsUtilises)
                if (x.equals(bassin))//On fait attention de ne pas réutiliser un bassin déjà utilisé
                    forge = false;
            if (!bassin.getFace().isEmpty() && bassin.getCout() <= joueur.getOr() && forge)//Si on peut ajouter ce bassin
                bassinAbordable.add(bassin);//On l'ajoute
        }
        if (!bassinAbordable.isEmpty())
            bassinsUtilises.add(joueur.choisirFaceAForger(bassinAbordable, numeroManche));//Puis on forge, le joueur s'occupe de retirer la face
        else//Si le joueur ne peut plus forger, on averti
            return null;
        if (bassinsUtilises.get(bassinsUtilises.size()-1) == null)
            return null;
        return bassinsUtilises;
    }

    /**
     * Méthode qui demande à un joueur de choisir une carte
     * A raccourcir, refaire ou alors nier son existence
     */
    public void exploit(Joueur joueur, int numeroManche) {
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
                            throw new DiceForgeException("Coordinateur","Une carte doit couter soit des lunes soit des soleils !");
                    }
                    if (prixSoleil <= joueur.getSoleil() && prixLune <= joueur.getLune())//Si le joueur peut l'acheter on l'ajoute
                        cartesAbordables.add(carte);
                }
            }
        }
        if (cartesAbordables.isEmpty())//Si le joueur ne peut acheter aucune carte, on s'arrète la
            return;
        for (Joueur j : plateau.getPortail().getJoueurs())//En premier, on retire le joueur s'il est situé dans les portails originels
            if (joueur.getIdentifiant() == j.getIdentifiant()) {//On teste les identifiants, c'est le plus sur
                plateau.getPortail().retirerJoueur(joueur.getIdentifiant());
                break;
            }
        Carte carteChoisie = joueur.choisirCarte(cartesAbordables, numeroManche);
        Joueur joueurChasse = null;//On gére le joueur chassé et on donne la carte au joueur
        int i = 1;
        for (Ile ile:plateau.getIles())
            if (ile.getJoueur() != null && joueur.getIdentifiant() == ile.getJoueur().getIdentifiant()) {
                ile.retirerJoueur();//En premier, on retire le joueur de son ile
                break;
            }
                for (Ile ile : plateau.getIles()) {
                    for (List<Carte> paquet : ile.getCartes()) {
                        if (!paquet.isEmpty() && paquet.get(0).equals(carteChoisie)) {
                            joueurChasse = ile.prendreCarte(joueur, carteChoisie);//Ici on l'ajoute à l'ile ou il va
                        }
                    }
                    ++i;
                }
                if (joueurChasse != null) {//S'il il y a bien un joueur qui a été chassé, on le renvoi au portails originels
                    plateau.getPortail().ajouterJoueur(joueurChasse);
                }
            }
    private int nombreAncienInactivable(Joueur joueur){
        int nombreAncienActivable = joueur.getNombreAncien();
        if (joueur.getOr()/3 < nombreAncienActivable)
            nombreAncienActivable = joueur.getOr()/3;
        int nombreAncienInactivable = joueur.getNombreAncien() - nombreAncienActivable;
        return nombreAncienInactivable;
    }

    private List enleveAncienInactivable(Joueur joueur, List renforts,int nombreAncienInactivable) {
        for (int compteAnciensEnleves = 0; compteAnciensEnleves < nombreAncienInactivable; compteAnciensEnleves++)
            renforts.remove(0);
        return renforts;
    }

    private void renforts(Joueur joueur, int numeroManche){
        List renfortsUtilisables = new ArrayList();
        int len = joueur.getRenforts().size();
        for (int i=0; i<len; i++)
            renfortsUtilisables.add(joueur.getRenforts().get(i));
        int nombreAncienInactivable = nombreAncienInactivable(joueur);
        renfortsUtilisables = enleveAncienInactivable(joueur, renfortsUtilisables, nombreAncienInactivable);
        List choixDuJoueur = joueur.choisirRenforts(renfortsUtilisables);
        joueur.appelerRenforts(choixDuJoueur);
        choixDuJoueur.forEach(x -> affichage += "\nLe joueur " + joueur.getIdentifiant() + " active le renfort " + x + "\n");
    }

    private void phaseLanceDe(Joueur joueur, int numeroManche){
        if (plateau.estVerbeux())
            affichage += ("--------------------------------------------------------\n"+ "Manche: " + numeroManche + "\t||\t" + "Tour du joueur " + joueur.getIdentifiant() + "\t||\t" + "\n--------------------------------------------------------\n"); // annonce de la manche et du tour, les résultats des lancés ne sont pas affichés par souci de concisions
            affichage += ("\n" + "Ressource disponibles:\n\tOr: " + joueur.getOr() + "\t||\t" + "Soleil: " + joueur.getSoleil() + "\t||\t" + "Lunes: " + joueur.getLune() +"\n"); //On affiche les ressources disponibles au joueur, utile pour vérifier par la suite que les ia programmées jouent de manière relativement intelligente
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getJoueur().size() == 2) {
                x.lancerLesDes();//S'il n'y a que 2 joueurs, chaque joueur lance les dés une deuxième fois
            }
            x.lancerLesDes();
        }
    }
    public String toString(){
        return affichage;
        }
}

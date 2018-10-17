package diceForge;

import java.util.ArrayList;
import java.util.List;

/**
 * Le coordinateur s'occupe de faire tourner le jeu.
 * Il s'occupe du déroulement des manches, mais aussi de déplacer les joueurs.
 * A voir si c'est une bonne solution
 */
public class Coordinateur {
    Plateau plateau;
    private String affichage = "";

    public Coordinateur(Plateau plateau, int nbrManche){
        this.plateau = plateau;
        if (nbrManche < 4 || nbrManche > 10)
            throw new DiceForgeException("Coordinateur","Le nombre de manche est invalide. Min : 4, max : 10, actuel : "+nbrManche);
        for (int i = 1; i <= nbrManche; ++i){
            jouerManche(i);
        }
        int numJoueurGagnant = 0, maxPointGloire = 0;
        for (Joueur joueur:plateau.getJoueur()){
            joueur.additionnerPointsCartes();
            if (joueur.getPointDeGloire() > maxPointGloire){
                maxPointGloire = joueur.getPointDeGloire();
                numJoueurGagnant = joueur.getIdentifiant();
            }
        }
        affichage += "\n\n\n\n\t\t-------------------------------------------------\n\t\t" + "| Le joueur n°"+numJoueurGagnant+" gagne avec "+maxPointGloire+" points de gloire ! |\n" + "\t\t-------------------------------------------------\n";
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
        if (plateau.getModeVerbeux())
            affichage += ("--------------------------------------------------------------\n"+ "Manche: " + numeroManche + "\t||\t" + "Tour du joueur " + joueur.getIdentifiant() + "\t||\t" + "Phase de lancer de dés" + "\n--------------------------------------------------------------\n");
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getJoueur().size() == 2) {
                x.lancerLesDes();
                if (plateau.getModeVerbeux())
                    affichage += x.returnStringRessourcesEtDes(numeroManche);
            }
            x.lancerLesDes();
            if (plateau.getModeVerbeux())
                affichage += x.returnStringRessourcesEtDes(numeroManche);
        }
        actionPrincipale(joueur, numeroManche);
        if (joueur.getSoleil() >= 2 && joueur.choisirActionSupplementaire(numeroManche)) {
            if (plateau.getModeVerbeux())
                affichage += "\n---------- Le joueur " + joueur.getIdentifiant() + " choisi d'effectuer une seconde action ----------\n\n";
            actionPrincipale(joueur, numeroManche);
        }
    }

    /**
     * Demande ce que le bot veut faire et agit en fonction
     */
    public void actionPrincipale(Joueur joueur, int numeroManche){
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                if (plateau.getModeVerbeux())
                    affichage += "\t\t-- Le joueur " + joueur.getIdentifiant() + " choisi de forger --\n";
                List<Bassin> bassinsAEnlever = new ArrayList<>();
                do {
                    bassinsAEnlever = forger(joueur, numeroManche, bassinsAEnlever);
                } while(joueur.choisirContinuerForger() && bassinsAEnlever != null);
                break;
            case EXPLOIT:
                if (plateau.getModeVerbeux())
                    affichage += "\t\t-- Le joueur " + joueur.getIdentifiant() + " choisi d'accomplir un exploit --\n";
                exploit(joueur, numeroManche);
                break;
            case PASSER:
                if (plateau.getModeVerbeux())
                    affichage += "\n\t\t-- le joueur " + joueur.getIdentifiant() + " passe son tour --\n";
                break;
        }
    }

    /**
     * Méthode demande à un joueur de forger une face d'un bassin
     * @return Une List représentant les bassins que le joueur à déjà utilisés, ou null si le joueur ne peut plus forger
     */
    public List<Bassin> forger(Joueur joueur, int numeroManche, List<Bassin> bassinsUtilises) {
        List<Bassin> bassinAffordable = new ArrayList<>();//On créé la liste des bassins affordables
        for (Bassin bassin : plateau.getTemple().getSanctuaire()) {
            boolean forge = true;
            for (Bassin x:bassinsUtilises)
                if (x.equals(bassin))
                    forge = false;
            if (!bassin.getFace().isEmpty() && bassin.getCout() <= joueur.getOr() && forge)
                bassinAffordable.add(bassin);//Puis on la remplie
        }
        if (!bassinAffordable.isEmpty())
            bassinsUtilises.add(joueur.choisirFaceAForger(bassinAffordable, numeroManche));//Puis on forge, le joueur s'occupe de retirer la face
        else
            return null;
        return bassinsUtilises;

    }

    /**
     * Méthode qui demande à un joueur de choisir une carte
     * A raccourcir, refaire ou alors nier son existence
     */
    public void exploit(Joueur joueur, int numeroManche) {
        List<Carte> cartesAffordables = new ArrayList<>();//Notre liste qui va contenir les cartes affordables par le joueur
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
                        cartesAffordables.add(carte);
                }
            }
        }
        if (cartesAffordables.isEmpty())//Si le joueur ne peut acheter aucune carte, on s'arrète la
            return;
        for (Joueur j : plateau.getPortail().getJoueurs())//En premier, on retire le joueur s'il est situé dans les portails originels
            if (j != null && joueur.getIdentifiant() == j.getIdentifiant()) {//On teste les identifiants, c'est le plus sur
                plateau.getPortail().retirerJoueur(joueur.getIdentifiant());
                break;
            }
        Carte carteChoisie = joueur.choisirCarte(cartesAffordables, numeroManche);
        Joueur joueurChasse = null;
        for (Ile ile : plateau.getIles()) {
            for (List<Carte> paquet : ile.getCartes()) {
                if (paquet.get(0).equals(carteChoisie)) {
                    joueurChasse = ile.prendreCarte(joueur, carteChoisie);
                }
            }
        }
        if (joueurChasse != null)
            plateau.getPortail().ajouterJoueur(joueurChasse);
    }
    public String toString(){
        return affichage;
        }
}

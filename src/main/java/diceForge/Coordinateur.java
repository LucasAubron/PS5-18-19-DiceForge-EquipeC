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
    private int nbrManche;

    public Coordinateur(Plateau plateau, int nbrManche){
        this.plateau = plateau;
        if (nbrManche < 4 || nbrManche > 10)
            throw new DiceForgeException("Coordinateur","Le nombre de manche est invalide. Min : 4, max : 10, actuel : "+nbrManche);
        this.nbrManche = nbrManche;
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
        System.out.println("Le joueur n°"+numJoueurGagnant+" gagne avec "+maxPointGloire+" points de gloire !");
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
        System.out.println("-----------------------------------------------------------------------\n"+ "Manche: " + numeroManche + "\t||\t" + "Tour du joueur: " + joueur.getIdentifiant() + "\t||\t" + "Phase de lancer de dés" + "\n-----------------------------------------------------------------------\n");
        for (Joueur x:plateau.getJoueur()){//En premier, tout le monde lance les dés
            if (plateau.getJoueur().size() == 2) {//On passe par le portail pour de l'optimisation
                x.lancerLesDes();
                if (plateau.modeVerbeux)
                    System.out.println(x.printRessourcesEtDes(numeroManche));
            }
            x.lancerLesDes();
            if (plateau.modeVerbeux)
                System.out.println(x.printRessourcesEtDes(numeroManche));
        }
        Joueur.Action actionBot = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
        switch (actionBot){
            case FORGER:
                forger(joueur, numeroManche);
                break;
            case EXPLOIT:
                exploit(joueur, numeroManche);
                break;
        }
        if (joueur.getSoleil() >= 2 && joueur.choisirActionSupplementaire(numeroManche)) {
            Joueur.Action actionBot2 = joueur.choisirAction(numeroManche);//On regarde quelle est l'action du bot
            switch (actionBot2) {
                case FORGER:
                    forger(joueur, numeroManche);
                    break;
                case EXPLOIT:
                    exploit(joueur, numeroManche);
                    break;
            }
        }
    }

    /**
     * Méthode qui permet à un joueur de forger une face d'un bassin
     */
    public  void forger(Joueur joueur, int numeroManche) {
        List<Bassin> bassinAffordable = new ArrayList<>();//On créé la liste des bassins affordables
        for (Bassin bassin : plateau.getTemple().getSanctuaire()) {
            if (!bassin.getFace().isEmpty() && bassin.getCout() <= joueur.getOr())
                bassinAffordable.add(bassin);//Puis on la remplie
        }
        if (!bassinAffordable.isEmpty())
            joueur.choisirFaceAForger(bassinAffordable, numeroManche);//Puis on forge, le joueur s'occupe de retirer la face
    }

    /**
     * Méthode qui permet à un joueur de choisir une carte
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
                            throw new DiceForgeException("Coordinateur","Une carte doit couter soit des lunes soit des soleils !!!!");
                    }
                    if (prixSoleil <= joueur.getSoleil() && prixLune <= joueur.getLune())//Si le joueur peut l'acheter on l'ajoute
                        cartesAffordables.add(carte);
                }
            }
        }
        if (cartesAffordables.isEmpty())
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
}

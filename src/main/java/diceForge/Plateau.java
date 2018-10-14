package diceForge;

import java.util.ArrayList;

/**
 * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
 * A voir si on met les joueurs ici ou dans le Main
 * En fait on va mettre les joueurs dans PortailsOriginels
 */
public class Plateau {

    Joueur j0 = new RandomBot(0);
    Joueur j1 = new RandomBot(1);
    private Temple temple = new Temple();//La classe temple s'occupe de toute la partie forge de dé
    private PortailsOriginels portail = new PortailsOriginels(new Joueur[]{j0,j1});//La ou les joueurs sont de base
    private Ile[] iles;//La ou il y a les cartes

    public void playPlayer0(){
        j0.lancerLesDes();
        System.out.println(j0.printRessourcesEtDes());
    }

    public Plateau(){
        iles = new Ile[]{
                new Ile(
                        new Carte[][]{{
                                new Carte(new Ressource[]{new Soleil(2)}, 2),
                                new Carte(new Ressource[]{new Soleil(2)}, 2)}
                        })};
    }

    /**
     * Si quelqu'un peut le faire plus clairement, qu'il le fasse
     * @return la liste des joueurs présents sur le plateau
     */
    public Joueur[] getJoueur() {
        Joueur[] joueur = new Joueur[portail.getJoueurs().length];
        ArrayList<Joueur> tempJoueur = new ArrayList<>();
        for (Joueur x:portail.getJoueurs())
            if (x != null)
                tempJoueur.add(x);
        for (Ile x:iles)
            if (x.getJoueur() != null)
                tempJoueur.add(x.getJoueur());
        for (int i = 0; i != tempJoueur.size(); ++i)
            joueur[i] = tempJoueur.get(i);
        return joueur;
    }

    public PortailsOriginels getPortail(){return portail;}
}

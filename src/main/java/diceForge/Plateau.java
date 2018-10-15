package diceForge;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
 * A voir si on met les joueurs ici ou dans le Main
 * En fait on va mettre les joueurs dans PortailsOriginels
 */
public class Plateau {

    private boolean modeVerbeux = true;
    Joueur j0 = new RandomBot(0);
    Joueur j1 = new RandomBot(1);
    private Temple temple = new Temple();//La classe temple s'occupe de toute la partie forge de dé
    private PortailsOriginels portail = new PortailsOriginels(new Joueur[]{j0,j1});//La ou les joueurs sont de base
    private Ile[] iles;//La ou il y a les cartes


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
    public List<Joueur> getJoueur() {
        List<Joueur> tempJoueur = new ArrayList<>();
        for (Joueur x:portail.getJoueurs())
            tempJoueur.add(x);
        for (Ile x:iles)
            if (x.getJoueur() != null)
                tempJoueur.add(x.getJoueur());
        List<Joueur> joueur = new ArrayList<>();
        for (int i = 0; i != tempJoueur.size(); ++i){
            if (tempJoueur.get(i).getIdentifiant() == i)
                joueur.add(tempJoueur.get(i));
        }
        return joueur;
    }

    public PortailsOriginels getPortail(){return portail;}

    public Ile[] getIles() {return iles;}

    public Temple getTemple() {
        return temple;
    }

    public boolean getModeVerbeux() {return modeVerbeux; }
}

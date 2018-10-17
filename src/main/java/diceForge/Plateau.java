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
        for (Joueur x:portail.getJoueurs())//On ajoute tous les joueurs des portails originels
            tempJoueur.add(x);
        for (Ile x:iles)//On ajoute tous les joueurs qui sont dans les iles
            if (x.getJoueur() != null)//On fait attention parce qu'une ile ne contient pas forcement un joueur
                tempJoueur.add(x.getJoueur());
        List<Joueur> joueur = new ArrayList<>();//Pour la liste triée
        for (int i = 0; i != tempJoueur.size(); ++i){
            for (Joueur j:tempJoueur)//On tri la liste des joueurs en fonction de leur identifiant, pour que l'ordre des joueurs reste le même
                if (j.getIdentifiant() == i) {//Si on trouve l'indice correspondant, on le met dans la liste
                    joueur.add(j);
                    break;
                }
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

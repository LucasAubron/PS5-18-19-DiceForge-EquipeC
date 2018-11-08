package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
 * A voir si on met les joueurs ici ou dans le Main
 * En fait on va mettre les joueurs dans PortailsOriginels
 */
public class Plateau {
    private boolean modeVerbeux;
    private PortailsOriginels portail;
    private Temple temple;
    private Ile[] iles;//La ou il y a les cartes


    Plateau(boolean modeVerbeux, Joueur[] joueurs){
        portail = new PortailsOriginels(joueurs);//La ou les joueurs sont de base
        temple = new Temple(joueurs.length);//La classe temple s'occupe de toute la partie forge de dé
        this.modeVerbeux = modeVerbeux;
        Random random = new Random();

        Carte ours = new Carte(new Ressource[]{new Lune(2)}, 2, "Ours");
        Carte biche = new Carte(new Ressource[]{new Lune(2)}, 2, "Biche");
        Carte sanglier = new Carte(new Ressource[]{new Lune(3)}, 4, "Sanglier");
        Carte satyres = new Satyres(portail.getJoueurs());

        Carte hibou = new Carte(new Ressource[]{new Soleil(2)}, 4, "Hibou");
        Carte bateauCeleste = new Carte(new Ressource[]{new Soleil(2)}, 4, "Bateau celeste");
        Carte minautore = new Minautore(portail.getJoueurs());
        iles = new Ile[]{new Ile(new Marteau(),
                new Carte(new Ressource[]{new Lune(1)}, 2, "Coffre"), joueurs.length),
        new Ile(new Carte(new Ressource[]{new Soleil(1)}, 0, "Ancien"),
                new Carte(new Ressource[]{new Soleil(1)}, 2, "Herbes folles"), joueurs.length),
        new Ile(random.nextInt(2) == 1 ? ours : biche,
                random.nextInt(2) == 1 ? sanglier : satyres, joueurs.length),
        new Ile(random.nextInt(2) == 1 ? hibou : bateauCeleste,
                minautore, joueurs.length),
        new Ile(new Carte(new Ressource[]{new Lune(4)}, 12, "Passeur"),
                new Carte(new Ressource[]{new Lune(5)}, 4,  "Casque d invisibilite"),
                joueurs.length)};
    }

    /**
     * Si quelqu'un peut le faire plus clairement, qu'il le fasse
     * @return la liste des joueurs présents sur le plateau
     */
    public List<Joueur> getJoueur() {
        List<Joueur> tempJoueur = new ArrayList<>();
        //On ajoute tous les joueurs des portails originels
        tempJoueur.addAll(portail.getJoueurs());
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

    PortailsOriginels getPortail(){return portail;}

    Ile[] getIles() {return iles;}

    Temple getTemple() {
        return temple;
    }

    boolean estVerbeux() {return modeVerbeux; }
}

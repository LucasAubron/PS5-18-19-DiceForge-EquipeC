package diceForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
 * A voir si on met les joueurs ici ou dans le Main
 * En fait on va mettre les joueurs dans PortailsOriginels
 */
class Plateau {
    private boolean modeVerbeux;
    private PortailsOriginels portail;
    private Temple temple;
    private Ile[] iles;//La ou il y a les cartes

    Plateau(Joueur.Bot[] typeJoueurs, Afficheur afficheur) {
        portail = new PortailsOriginels(typeJoueurs, afficheur);//La ou les joueurs sont de base
        temple = new Temple(typeJoueurs.length);//La classe temple s'occupe de toute la partie forge de dé
        this.modeVerbeux = modeVerbeux;
        Random random = new Random();

        Carte ours = new Carte(new Ressource[]{new Lune(2)}, 2, Carte.Noms.Ours);
        Carte biche = new Carte(new Ressource[]{new Lune(2)}, 2, Carte.Noms.Biche);
        Carte sanglier = new CarteSanglier(portail.getJoueurs());
        Carte satyres = new Satyres(portail.getJoueurs());
        Carte cerbere = new Carte(new Ressource[]{new Lune(4)}, 6, Carte.Noms.Cerbere);
        Carte passeur = new Carte(new Ressource[]{new Lune(4)}, 12, Carte.Noms.Passeur);

        Carte hibou = new Carte(new Ressource[]{new Soleil(2)}, 4, Carte.Noms.Hibou);
        Carte bateauCeleste = new CarteBateauCeleste(this);
        Carte minautore = new Minautore(portail.getJoueurs());
        Carte bouclier = new CarteBouclier(this);
        Carte meduse = new Carte(new Ressource[]{new Soleil(4)}, 14, Carte.Noms.Meduse);
        Carte triton = new Carte(new Ressource[]{new Soleil(4)}, 8, Carte.Noms.Triton);

        iles = new Ile[]{new Ile(new Marteau(),
                new Carte(new Ressource[]{new Lune(1)}, 2, Carte.Noms.Coffre), typeJoueurs.length),
                new Ile(new Carte(new Ressource[]{new Soleil(1)}, 0, Carte.Noms.Ancien),
                        new Carte(new Ressource[]{new Soleil(1)}, 2, Carte.Noms.HerbesFolles), typeJoueurs.length),
                new Ile(random.nextInt(2) == 1 ? ours : biche,
                        random.nextInt(2) == 1 ? sanglier : satyres, typeJoueurs.length),
                new Ile(random.nextInt(2) == 1 ? hibou : bateauCeleste,
                        random.nextInt(2) == 1 ? minautore : bouclier, typeJoueurs.length),
                new Ile(random.nextInt(2) == 1 ? cerbere : passeur,
                        new Carte(new Ressource[]{new Lune(5)}, 4, Carte.Noms.CasqueDinvisibilite),
                        typeJoueurs.length),
                new Ile(random.nextInt(2) == 1 ? meduse : triton,
                        new CarteMiroirAbyssal(portail.getJoueurs()),
                        typeJoueurs.length)};
        /*
         * /!\   lorsqu'on ajoutera les cartes qui coûtent 6 Lunes/Soleils ne pas oublier
         *       de s'occuper du jeton Cerbère!.
         * */
    }

    /**
     * Si quelqu'un peut le faire plus clairement, qu'il le fasse
     * @return la liste des joueurs présents sur le plateau
     */
    List<Joueur> getJoueurs() {
        List<Joueur> tempJoueur = new ArrayList<>();
        //On ajoute tous les joueurs des portails originels
        tempJoueur.addAll(portail.getJoueurs());
        for (Ile x:iles)//On ajoute tous les joueurs qui sont dans les iles
            if (x.getJoueur() != null)//On fait attention parce qu'une ile ne contient pas forcement un joueur
                tempJoueur.add(x.getJoueur());
        List<Joueur> joueurs = new ArrayList<>();//Pour la liste triée
        for (int i = 1; i != tempJoueur.size()+1; ++i){
            for (Joueur j:tempJoueur)//On tri la liste des joueurs en fonction de leur identifiant, pour que l'ordre des joueurs reste le même
                if (j.getIdentifiant() == i) {//Si on trouve l'indice correspondant, on le met dans la liste
                    joueurs.add(j);
                    break;
                }
        }
        return joueurs;
    }

    PortailsOriginels getPortail(){return portail;}

    Ile[] getIles() {return iles;}

    Temple getTemple() {
        return temple;
    }
}

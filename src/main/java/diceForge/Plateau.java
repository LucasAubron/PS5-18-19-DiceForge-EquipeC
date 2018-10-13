package diceForge;

public class Plateau {
    /**
     * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
     * A voir si on met les joueurs ici ou dans le Main
     * En fait on va mettre les joueurs dans PortailsOriginels
     */
    private Temple temple = new Temple();//La classe temple s'occupe de toute la partie forge de dé
    private PortailsOriginels portail = new PortailsOriginels(new Joueur[]{new Joueur(3, 1, 1, 0), new Joueur(3, 1, 1, 1)});//La ou les joueurs sont de base
    private Ile[] iles;//La ou il y a les cartes

    public Plateau(){
        iles = new Ile[]{
                new Ile(
                        new Carte[][]{{
                                new Carte(new Ressource[]{new Soleil(2)}, 2),
                                new Carte(new Ressource[]{new Soleil(2)}, 2)}
                        })};
        //XDDDDDDDDDD moi j'adore java parce que c'est vraiment simple d'initialiser des listes il n'y a aucun truc redondant c'est vraiment sympas. Cependant il est vrai qu'avec des ArrayList ça aurait été encore plus long. En plus le java n'a que des avantages par rapport au C++, par exemple on passer en fonction des initialiseur de liste (ah ben non) ou encore on peut mettre des arguments falcutatifs avec une valeur par défault(???). Bon sinon c'est pour dire que tout va bien la famille les amis wesh ça va
    }
}

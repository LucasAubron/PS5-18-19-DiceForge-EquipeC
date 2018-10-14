package diceForge;

/**
 * Cette classe acceuille tous les éléments qui sont sur le plateau de jeu
 * A voir si on met les joueurs ici ou dans le Main
 * En fait on va mettre les joueurs dans PortailsOriginels
 */
public class Plateau {

    Joueur j0 = new Joueur(3, 1, 1, 0);
    Joueur j1 = new Joueur(3, 1, 1, 1);
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

    public Ile[] getIles() {
        return iles;
    }

    public PortailsOriginels getPortail() {
        return portail;
    }

    public Temple getTemple() {
        return temple;
    }
}

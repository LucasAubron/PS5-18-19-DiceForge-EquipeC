package diceForge;

public class FaceSanglier extends Face {
    private Joueur joueurMaitre;
    FaceSanglier(Joueur joueurMaitre){
        super(new Ressource[][]{{new Soleil(1)}, {new Lune(1)}});
        this.joueurMaitre = joueurMaitre;
    }

    @Override
    void effetActif (Joueur joueur){
        joueurMaitre.gagnerRessourceFace(
                new Face(new Ressource[][]{
                        {new Soleil(1)},
                        {new Lune(1)},
                        {new PointDeGloire(3)}}));
        for (int j = 0; j < joueur.getJetons().size() && joueur.getJetons().get(j) == Joueur.Jeton.CERBERE && joueur.utiliserJetonCerbere(); ++j)
            joueur.appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
    }

    @Override
    public String toString(){
        return "sanglier(joueur n°" + joueurMaitre.getIdentifiant() +") ";
    }

    Joueur getJoueurMaitre() {
        return joueurMaitre;
    }
}

package diceForge;

public class FaceSanglier extends Face {
    private Joueur joueurMaitre;
    FaceSanglier(Joueur joueurMaitre){
        super(typeFace.SANGLIER);
        this.joueurMaitre = joueurMaitre;
    }

    @Override
    void effetActif (Joueur joueur){
        joueurMaitre.gagnerRessourceFace(
                new Face(typeFace.CHOIX, new Ressource[]{
                        new Ressource(1, Ressource.type.SOLEIL),
                        new Ressource(1, Ressource.type.LUNE),
                        new Ressource(3, Ressource.type.PDG)}), false);
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

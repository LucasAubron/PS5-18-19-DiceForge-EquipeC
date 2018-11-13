package diceForge;

class FaceSanglier extends Face {
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
    }

    @Override
    public String toString(){
        return "sanglier(joueur n°" + joueurMaitre.getIdentifiant() +") ";
    }

    Joueur getJoueurMaitre() {
        return joueurMaitre;
    }
}

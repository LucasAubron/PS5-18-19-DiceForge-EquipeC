package diceForge;

class FaceSanglier extends Face {
    private Joueur joueurMaitre;
    FaceSanglier(Joueur joueurMaitre){
        super(new Ressource[][]{{new Soleil(1)}, {new Lune(1)}});
        this.joueurMaitre = joueurMaitre;
    }

    @Override
    public String toString(){
        return "Face sanglier";
    }

    Joueur getJoueurMaitre() {
        return joueurMaitre;
    }
}

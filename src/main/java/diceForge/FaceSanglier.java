package diceForge;

public class FaceSanglier extends Face {
    private Joueur joueurMaitre;
    public FaceSanglier(Joueur joueurMaitre){
        super(new Ressource[][]{{new Soleil(1)}, {new Lune(1)}});
        this.joueurMaitre = joueurMaitre;
    }

    public Joueur getJoueurMaitre() {
        return joueurMaitre;
    }
}

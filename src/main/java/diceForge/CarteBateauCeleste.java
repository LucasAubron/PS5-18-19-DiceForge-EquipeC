package diceForge;

public class CarteBateauCeleste extends Carte {
    private Plateau plateau;
    public CarteBateauCeleste(Plateau plateau){
        super(new Ressource[]{new Soleil(2)}, 4, Noms.BateauCeleste);
        this.plateau = plateau;
    }

    @Override
    void effetDirect(Joueur acheteur){
        acheteur.forgerFace(new FaceBateauCeleste(plateau.getTemple()));
    }

    @Override
    public Carte clone(){
        return new CarteBateauCeleste(plateau);
    }
}

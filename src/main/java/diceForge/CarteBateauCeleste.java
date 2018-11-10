package diceForge;

public class CarteBateauCeleste extends Carte {
    private Plateau plateau;
    CarteBateauCeleste(Plateau plateau){
        super(new Ressource[]{new Soleil(2)}, 4, "Bateau celeste");
        this.plateau = plateau;
    }

    @Override
    void effetDirect(Joueur acheteur){
        acheteur.forgerFace(new FaceBateauCeleste(plateau.getTemple()));
    }
}

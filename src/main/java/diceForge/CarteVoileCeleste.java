package diceForge;

public class CarteVoileCeleste extends Carte {
    private Plateau plateau;
    public CarteVoileCeleste(Plateau plateau){
        super(new Ressource[]{new Ressource(2, Ressource.type.SOLEIL)}, 4, Noms.VoileCeleste);
        this.plateau = plateau;
    }

    @Override
    void effetDirect(Joueur acheteur){
        acheteur.forgerFaceSpeciale(new FaceVoileCeleste(plateau.getTemple()));
    }

    @Override
    public Carte clone(){
        return new CarteVoileCeleste(plateau);
    }
}

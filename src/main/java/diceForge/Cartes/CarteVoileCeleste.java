package diceForge.Cartes;


import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.FaceVoileCeleste;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;

public class CarteVoileCeleste extends Carte {
    private Plateau plateau;
    public CarteVoileCeleste(Plateau plateau){
        super(new Ressource[]{new Ressource(2, Ressource.type.SOLEIL)}, 4, Noms.VoileCeleste);
        this.plateau = plateau;
    }

    @Override
    public void effetDirect(Joueur acheteur){
        acheteur.forgerFaceSpeciale(new FaceVoileCeleste(plateau.getTemple()));
    }

    @Override
    public Carte clone(){
        return new CarteVoileCeleste(plateau);
    }
}

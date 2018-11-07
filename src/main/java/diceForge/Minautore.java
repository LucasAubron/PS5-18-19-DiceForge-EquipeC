package diceForge;

import java.util.ArrayList;
import java.util.List;

public class Minautore extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    public Minautore(List<Joueur> joueurs){
        super(new Ressource[]{new Soleil(3)}, 8, "Minautore");
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new Minautore(joueurs);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
}

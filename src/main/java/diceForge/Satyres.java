package diceForge;

import java.util.ArrayList;
import java.util.List;

public class Satyres extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    Satyres(List<Joueur> joueurs){
        super(new Ressource[]{new Lune(3)}, 6, "Satyres");
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("Satyres","Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : "+joueurs.size());
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new Satyres(joueurs);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
}

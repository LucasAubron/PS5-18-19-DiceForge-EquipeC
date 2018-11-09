package diceForge;

import java.util.ArrayList;
import java.util.List;

public class CarteMiroirAbyssal extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    CarteMiroirAbyssal(List<Joueur> joueurs){
        super(new Ressource[]{new Soleil(5)}, 10, "Miroir Abyssal");
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("Miroir Abyssal","Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : "+joueurs.size());
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new CarteMiroirAbyssal(joueurs);
    }

    @Override
    void effetDirect(Joueur acheteur){
        int[] posFace = acheteur.choisirFaceARemplacePourMiroir();
        acheteur.forgerDe(posFace[0], new FaceMiroirAbyssal(acheteur, this.joueurs), posFace[1]);
    }

    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

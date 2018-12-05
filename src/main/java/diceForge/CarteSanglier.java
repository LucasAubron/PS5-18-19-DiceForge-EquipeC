package diceForge;

import java.util.ArrayList;
import java.util.List;

public class CarteSanglier extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();

    public CarteSanglier(List<Joueur> joueurs){
        super(new Ressource[]{new Ressource(3, Ressource.type.LUNE)}, 4, Noms.Sanglier);
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("CarteSanglier", "Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : " + joueurs.size());
        this.joueurs.addAll(joueurs);
    }

    /**
     * on fait forger la face sanglier par le joueur cible qui est choisit dans la même
     * méthode. La face forgée par le joueur cible contient le joueur propriétaire de la carte
     * Sanglier qui a provoqué la forge de cette face sur le joueur cible.
     * @param acheteur
     */
    @Override
    void effetDirect(Joueur acheteur){
        joueurs.get(acheteur.choisirIdJoueurPorteurSanglier(joueurs)-1).forgerFaceSpeciale(new FaceSanglier(acheteur));
    }

    @Override
    public Carte clone(){
        return new CarteSanglier(joueurs);
    }
}

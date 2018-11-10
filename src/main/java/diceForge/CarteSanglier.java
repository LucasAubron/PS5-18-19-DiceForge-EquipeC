package diceForge;

import java.util.ArrayList;
import java.util.List;

public class CarteSanglier extends  Carte {
    private List<Joueur> joueurs = new ArrayList<>();

    CarteSanglier(List<Joueur> joueurs){
        super(new Ressource[]{new Lune(3)}, 4, "Sanglier");
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("Miroir Abyssal", "Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : " + joueurs.size());
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
        this.joueurs
                .get(acheteur.choisirIdJoueurPorteurSanglier(this.joueurs))
                .forgerFace(new FaceSanglier(acheteur));
    }
}

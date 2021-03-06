package diceForge.Cartes;


import diceForge.Faces.FaceSanglier;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.DiceForgeException;

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
    public void effetDirect(Joueur acheteur){
        List<Joueur> joueursEnnemis = new ArrayList<>(); // On s'assure que le joueur acheter ne puisse pas se donner
        for (Joueur joueur: joueurs)                     // à lui même la face sanglier
            if (joueur.getIdentifiant() != acheteur.getIdentifiant())
                joueursEnnemis.add(joueur);
        int idEnnemiChoisi = acheteur.choisirIdJoueurPorteurSanglier(joueursEnnemis);
        for (Joueur joueur: joueursEnnemis)
            if (idEnnemiChoisi == joueur.getIdentifiant())
                joueur.forgerFaceSpeciale(new FaceSanglier(acheteur));
    }

    @Override
    public Carte clone(){
        return new CarteSanglier(joueurs);
    }
}

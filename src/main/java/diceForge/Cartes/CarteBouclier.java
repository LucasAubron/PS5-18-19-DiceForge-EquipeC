package diceForge.Cartes;

import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import java.util.ArrayList;
import java.util.Arrays;

public class CarteBouclier extends Carte {
    private Joueur joueurMaitre;
    private Plateau plateau;
    public CarteBouclier(Plateau plateau){
        super(new Ressource[]{new Ressource(3, Ressource.type.SOLEIL)}, 6, Noms.Bouclier);
        this.plateau = plateau;
    }

    @Override
    public void effetDirect(Joueur acheteur){
        ChoixJoueurForge choix = acheteur.choisirFaceAForgerEtARemplacer(//ici la méthode ne sert qu'à choisir la face bouclier que l'on veut
                new ArrayList(Arrays.asList(plateau.getTemple().getJardin()[0])));
        acheteur.forgerFaceSpeciale(plateau.getTemple().getJardin()[0].retirerFace(choix.getNumFaceDansBassin())); // ce n'est qu'ici qu'on choisit
    }                                                                                                              // ou on veut la forger

    @Override
    public Carte clone(){
        return new CarteBouclier(plateau);
    }
}

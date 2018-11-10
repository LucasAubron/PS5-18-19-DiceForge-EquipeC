package diceForge;

import java.util.ArrayList;
import java.util.Arrays;

public class CarteBouclier extends Carte {
    private Joueur joueurMaitre;
    private Plateau plateau;
    CarteBouclier(Plateau plateau){
        super(new Ressource[]{new Soleil(3)}, 6, "Bouclier");
        this.plateau = plateau;
    }

    @Override
    void effetDirect(Joueur acheteur){
        ChoixJoueurForge choix = acheteur.choisirFaceAForgerEtARemplacer(
                new ArrayList<>(Arrays.asList(plateau.getTemple().getJardin()[0])), 5);
        acheteur.forgerFace(plateau.getTemple().getJardin()[0].retirerFace(choix.getNumFace()));

    }
}

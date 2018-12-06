package diceForge.Cartes;

import diceForge.Faces.Face;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import java.util.ArrayList;
import java.util.List;

public class Minautore extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    public Minautore(List<Joueur> joueurs){
        super(new Ressource[]{new Ressource(3, Ressource.type.SOLEIL)}, 8, Noms.Minautore);
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new Minautore(joueurs);
    }

    @Override // a refaire
    public void effetDirect(Joueur acheteur){
            for (Joueur joueur:joueurs){//Pour tous les joueurs
                if (joueur.getIdentifiant() != acheteur.getIdentifiant()){//Si ce n'est pas le joueur actuel
                    for (De de:joueur.getDes()){//Pour tous les dés
                        Face face = de.lancerLeDe();//On le lance
                        if (face.faitGagnerUneRessource()) {
                            joueur.gagnerRessourceFace(face, true);
                        }
                    }
                }
            }
    }
    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

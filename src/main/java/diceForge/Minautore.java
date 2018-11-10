package diceForge;

import java.util.ArrayList;
import java.util.List;

class Minautore extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    Minautore(List<Joueur> joueurs){
        super(new Ressource[]{new Soleil(3)}, 8, "Minautore");
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new Minautore(joueurs);
    }

    @Override
    void effetDirect(Joueur acheteur){
            for (Joueur joueur:joueurs){//Pour tous les joueurs
                if (joueur.getIdentifiant() != acheteur.getIdentifiant()){//Si ce n'est pas le joueur actuel
                    for (De de:joueur.getDes()){//Pour tous les dés
                        Face face = de.lancerLeDe();//On le lance
                        if (face.getRessource().length > 0) {
                            int x = 0;
                            if (face.getRessource().length != 1)
                                x = joueur.choisirRessourceAPerdre(face);//On gere le cas du choix
                            for (Ressource ressource : face.getRessource()[x]) {//on parcours les ressources de la face
                                if (ressource instanceof Or)//On retire les ressources
                                    joueur.ajouterOr(-ressource.getQuantite());
                                else if (ressource instanceof Soleil)
                                    joueur.ajouterSoleil(-ressource.getQuantite());
                                else if (ressource instanceof Lune)
                                    joueur.ajouterLune(-ressource.getQuantite());
                                else if (ressource instanceof PointDeGloire)
                                    joueur.ajouterPointDeGloire(-ressource.getQuantite());
                            }
                        }
                    }
                }
            }
    }

    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

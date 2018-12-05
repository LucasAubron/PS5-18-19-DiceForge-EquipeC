package diceForge;

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
    void effetDirect(Joueur acheteur){
            for (Joueur joueur:joueurs){//Pour tous les joueurs
                if (joueur.getIdentifiant() != acheteur.getIdentifiant()){//Si ce n'est pas le joueur actuel
                    for (De de:joueur.getDes()){//Pour tous les dés
                        Face face = de.lancerLeDe();//On le lance
                        if (face.faitGagnerUneRessource()) {
                            if (face.estFaceAChoix() || face.getTypeFace() == Face.typeFace.MIROIR)
                                joueur.choisirRessourceAPerdre(face);//On gere le cas du choix
                            else if (face.getTypeFace() == Face.typeFace.ADDITION){
                                for (Ressource ressource : face.getRessources()) {//on parcours les ressources de la face
                                    if (ressource.estDuType(Ressource.type.OR))//On retire les ressources
                                        joueur.ajouterOr(-ressource.getQuantite());
                                    else if (ressource.estDuType(Ressource.type.SOLEIL))
                                        joueur.ajouterSoleil(-ressource.getQuantite());
                                    else if (ressource.estDuType(Ressource.type.LUNE))
                                        joueur.ajouterLune(-ressource.getQuantite());
                                    else if (ressource.estDuType(Ressource.type.PDG))
                                        joueur.ajouterPointDeGloire(-ressource.getQuantite());
                                }
                            }
                            else if (face.getTypeFace() == Face.typeFace.SIMPLE){
                                if (face.getRessource().estDuType(Ressource.type.OR))//On retire les ressources
                                    joueur.ajouterOr(-face.getRessource().getQuantite());
                                else if (face.getRessource().estDuType(Ressource.type.SOLEIL))
                                    joueur.ajouterSoleil(-face.getRessource().getQuantite());
                                else if (face.getRessource().estDuType(Ressource.type.LUNE))
                                    joueur.ajouterLune(-face.getRessource().getQuantite());
                                else if (face.getRessource().estDuType(Ressource.type.PDG))
                                    joueur.ajouterPointDeGloire(-face.getRessource().getQuantite());
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

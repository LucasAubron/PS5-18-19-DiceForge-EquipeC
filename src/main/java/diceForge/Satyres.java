package diceForge;

import java.util.ArrayList;
import java.util.List;

public class Satyres extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    public Satyres(List<Joueur> joueurs){
        super(new Ressource[]{new Ressource(3, Ressource.type.LUNE) {
            @Override
            public int getQuantite() {
                return super.getQuantite();
            }
        }}, 6, Noms.Satyres);
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("Satyres","Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : "+joueurs.size());
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new Satyres(joueurs);
    }

    @Override
    void effetDirect(Joueur acheteur){
            List<Face> facesEnnemies = new ArrayList<>();//La liste qui va contenir tout les résultats des dé des autres joueurs
            for (Joueur joueur:joueurs)
                if (joueur.getIdentifiant() != acheteur.getIdentifiant())
                    for (De de : joueur.getDes())//Pour tous les de des autres joueurs
                        facesEnnemies.add(de.lancerLeDe());//On prend le résultat d'un nouveau lancer de dé

            acheteur.getDe(0).setFaceActive(acheteur.choisirFaceACopier(facesEnnemies)); //le joueur copie une face ennemie sur la face active de son dé
            acheteur.gagnerRessourceFace(acheteur.getDe(0).getFaceActive(), false); //dans le but de la gagner juste après
            facesEnnemies.remove(acheteur.getDe(0).getFaceActive());//L'acheteur ne peut choisir deux fois la même face, donc on la retire des choix
            acheteur.getDe(1).setFaceActive(acheteur.choisirFaceACopier(facesEnnemies));
            acheteur.gagnerRessourceFace(acheteur.getDe(1).getFaceActive(), false);
    }

    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

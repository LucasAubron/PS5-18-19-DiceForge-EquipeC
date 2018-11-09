package diceForge;

import java.util.ArrayList;
import java.util.List;

class Satyres extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    Satyres(List<Joueur> joueurs){
        super(new Ressource[]{new Lune(3)}, 6, "Satyres");
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
            List<Face> faces = new ArrayList<>();//La liste qui va contenir tout les résultats des dé des autres joueurs
            for (Joueur joueur:joueurs)
                if (joueur.getIdentifiant() != acheteur.getIdentifiant())
                    for (De de : joueur.getDes())//Pour tous les de des autres joueurs
                        faces.add(de.lancerLeDe());//On prend le résultat d'un lancer de dé
            int x = acheteur.choisirFacePourGagnerRessource(faces);//Ce joueur choisi une face
            acheteur.gagnerRessourceFace(faces.get(x));//il gagne ce qu'il y a sur cette face
            faces.remove(x);//Puis on l'enlève de la liste
            acheteur.gagnerRessourceFace(faces.get(acheteur.choisirFacePourGagnerRessource(faces)));//Et on demande pour la deuxième face et on lui fait gagné
    }

    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

package diceForge;

import java.util.ArrayList;
import java.util.List;

public class CarteMiroirAbyssal extends Carte {
    private List<Joueur> joueurs = new ArrayList<>();
    CarteMiroirAbyssal(List<Joueur> joueurs){
        super(new Ressource[]{new Soleil(5)}, 10, "Miroir Abyssal");
        if (joueurs.size() < 2 || joueurs.size() > 4)
            throw new DiceForgeException("Miroir Abyssal","Le nombre de joueurs est invalide. Min : 2, max : 4, actuel : "+joueurs.size());
        this.joueurs.addAll(joueurs);
    }

    @Override
    public Carte clone(){
        return new CarteMiroirAbyssal(joueurs);
    }

    @Override
    void effetDirect(Joueur acheteur){
        List<Face> faces = new ArrayList<>();//La liste qui va contenir tout les résultats des dé des autres joueurs

        //Face uneFace = acheteur.choisirFace();//Ce joueur choisi une face
        int numDe = acheteur.getChoisirDe();
        int numFace = acheteur.getChoisirFace();
        acheteur.forgerDe(numDe, new FaceMiroirAbyssal(acheteur, this.joueurs), numFace);
    }

    List<Joueur> getJoueurs() {
        return joueurs;
    }
}

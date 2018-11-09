package diceForge;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FaceMiroirAbyssal extends Face {
    private Joueur joueurMaitre;
    private List<Joueur> listeJoueurs;

    FaceMiroirAbyssal(Joueur joueurMaitre, List<Joueur> listeJoueurs){
        super(new Ressource[][]{{new Lune(1)}});
        this.joueurMaitre = joueurMaitre;
        this.listeJoueurs = listeJoueurs;
    }

    /**
     *
     * @return la liste des faces des joueurs adverses
     */
    Face[] executerMiroir() {
        Face tabFace[] = new Face[(listeJoueurs.size()-1)*2];
        int i = 0;
        for (Joueur j : this.listeJoueurs)
            if (j.getIdentifiant() != joueurMaitre.getIdentifiant()){
                for (int k = 0; k < 2; k++) {
                    tabFace[i] = j.getDesFaceCourante()[k];
                    i++;
                }
            }
        return tabFace;
    }

    @Override
    public String toString(){
        return "Miroir Abyssal";
    }

    Joueur getJoueurMaitre() {
        return joueurMaitre;
    }
}

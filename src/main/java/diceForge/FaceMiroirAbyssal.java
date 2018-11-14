package diceForge;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static diceForge.Joueur.Jeton.CERBERE;

public class FaceMiroirAbyssal extends Face {
    private List<Joueur> listeJoueurs;
    private int choix = -1;

    FaceMiroirAbyssal(Joueur joueurMaitre, List<Joueur> listeJoueurs){
        super(new Ressource[][]{});
        listeJoueurs.removeIf(x -> x.getIdentifiant() == joueurMaitre.getIdentifiant());
        this.listeJoueurs = listeJoueurs;
    }

    /**
     *
     * @return la liste des faces des joueurs adverses
     */
    List<Face> obtenirFacesAdversaires() {
        List<Face> faces = new ArrayList<>();
        for (Joueur joueur:listeJoueurs)
            for(Face face:joueur.getDesFaceCourante())
                if(!(face instanceof FaceMiroirAbyssal))
                    faces.add(face);
        return faces;
    }

    void setChoix(int choix){
        this.choix = choix;
    }

    @Override
    void effetActif(Joueur joueur){
        List<Face> faceAdversaires = obtenirFacesAdversaires();
        if (choix == -1)
            choix = joueur.choisirFacePourGagnerRessource(faceAdversaires);
        joueur.gagnerRessourceFace(faceAdversaires.get(choix));
        for (int j = 0; j < joueur.getJetons().size() && joueur.getJetons().get(j) == CERBERE && joueur.utiliserJetonCerbere(); ++j)
            joueur.appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
        choix = -1;
    }

    @Override
    public String toString(){
        return "Miroir Abyssal ";
    }
}

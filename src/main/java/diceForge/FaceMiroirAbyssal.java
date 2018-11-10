package diceForge;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FaceMiroirAbyssal extends Face {
    private List<Joueur> listeJoueurs;

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

    @Override
    void effetActif(Joueur joueur){
        List<Face> faceAdversaires = obtenirFacesAdversaires();
        joueur.gagnerRessourceFace(faceAdversaires.get(joueur.choisirFacePourGagnerRessource(faceAdversaires)));
    }

    @Override
    public String toString(){
        return "Miroir Abyssal";
    }
}

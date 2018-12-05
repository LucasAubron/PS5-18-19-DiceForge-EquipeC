package diceForge;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static diceForge.Joueur.Jeton.CERBERE;

public class FaceMiroirAbyssal extends Face {
    private List<Joueur> listeJoueursEnnemis;

    FaceMiroirAbyssal(Joueur joueurMaitre, List<Joueur> listeJoueurs){
        super(typeFace.MIROIR);
        listeJoueurs.removeIf(x -> x.getIdentifiant() == joueurMaitre.getIdentifiant());
        this.listeJoueursEnnemis = listeJoueurs;
    }

    /**
     *
     * @return la liste des faces des joueurs adverses
     */
    List<Face> obtenirFacesAdversaires() {
        List<Face> faces = new ArrayList<>();
        for (Joueur joueur:listeJoueursEnnemis)
            for(Face face:joueur.getDesFaceCourante())
                if(!(face instanceof FaceMiroirAbyssal))
                    faces.add(face);
        return faces;
    }

    Face copierFaceSelonChoixDuJoueur(Joueur joueur){
        List<Face> faceAdversaires = obtenirFacesAdversaires();
        return joueur.choisirFaceACopier(faceAdversaires);
    }

    @Override
    void effetActif(Joueur joueur){
        Face faceACopier = copierFaceSelonChoixDuJoueur(joueur);
        joueur.gagnerRessourceFace(faceACopier);
    }

    @Override
    public String toString(){
        return "Miroir Abyssal ";
    }
}

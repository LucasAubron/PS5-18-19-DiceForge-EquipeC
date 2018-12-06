package diceForge.Faces;

import diceForge.OutilJoueur.Joueur;
import java.util.ArrayList;
import java.util.List;


public class FaceMiroirAbyssal extends Face {
    private List<Joueur> listeJoueursEnnemis;

    public FaceMiroirAbyssal(Joueur joueurMaitre, List<Joueur> listeJoueurs){
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

    public Face copierFaceSelonChoixDuJoueur(Joueur joueur){
        List<Face> faceAdversaires = obtenirFacesAdversaires();
        return joueur.choisirFaceACopier(faceAdversaires);
    }

    @Override
    public void effetActif(Joueur joueur){
        Face faceACopier = copierFaceSelonChoixDuJoueur(joueur);
        joueur.gagnerRessourceFace(faceACopier, false);
    }

    @Override
    public String toString(){
        return "Miroir Abyssal ";
    }
}

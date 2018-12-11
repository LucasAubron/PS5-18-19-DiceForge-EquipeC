package diceForge.ElementPlateau;

import diceForge.Faces.Face;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

public class Bassin {

    //Attributs ----------------------------------------------------------------------------------------------------------------------
    public enum typeBassin{Cout2FaceOr, Cout2FaceLune, Cout3FaceOr, Cout3FaceSoleil, Cout4, Cout5, Cout6, Cout8FaceSoleil, Cout8FacePdg, Cout12, Bouclier};
    private int cout;
    private List<Face> faces;
    private typeBassin type;

    //Constructeurs -------------------------------------------------------------------------------------------------------------------
    /**
     * Constructeur dans le cas ou il y a des faces différentes dans le même bassin
     * et qu'il est plus simple de transmettre une liste
     */
    public Bassin(int cout, List<Face> faces, typeBassin type) {
        if (cout < 0 || cout > 12)
            throw new DiceForgeException("Bassin","Le cout du bassin n'est pas bon. Min 0, max 12, actuel : "+cout);
        this.cout = cout;
        if (faces.size() < 2 || faces.size() > 4)
            throw new DiceForgeException("Bassin","Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+faces.size());
        this.faces = faces;
        this.type = type;
    }

    /**
     * Constructeur qui sert lorsque le bassin ne comporte que la même faces
     */
    public Bassin(int cout, Face facesUnique, int nbrFace, typeBassin type){
        if (cout < 0 || cout > 12)
            throw new DiceForgeException("Bassin","Le cout du bassin n'est pas bon. Min 0, max 12, actuel : "+cout);
        this.cout = cout;
        if (nbrFace < 2 || nbrFace > 4)
            throw new DiceForgeException("Bassin","Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+nbrFace);
        faces = new ArrayList<>();
        for (int i = 0; i != nbrFace; ++i)
            faces.add(facesUnique);
        this.type = type;
    }

    // Méthodes --------------------------------------------------------------------------------------------------------------------
    public int getCout() {
        return cout;
    }

   /**
     * A utiliser pour savoir les faces présentes, PAS pour en prendre une
     * @return la liste des faces.
    * Aucune, certaines ou toutes peuvent être nulles
     */
    public List<Face> getFaces() {
        return faces;
    }

    public Face getFace(int num) {
        return faces.get(num);
    }

    /**
     * Cette méthode doit être utilisé pour retirer une face du bassin pour ensuite la graver sur un dé.
     * Il ne faut pas utiliser la méthode getFace() pour cela !
     */
    public Face retirerFace(int numFace){
        return faces.remove(numFace);
    }

    public boolean estLeBassin(typeBassin type){
        return type == this.type;
    }

    @Override
    public String toString(){
        return "" + faces;
    }
}
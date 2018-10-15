package diceForge;

import java.util.ArrayList;

public class Bassin {
    private int cout;
    private Face[] faces;

    /**
     * Constructeur dans le cas ou il y a des faces différentes dans le même bassin
     * et qu'il est plus simple de transmettre une liste
     */
    public Bassin(int cout, Face[] faces) {
        if (cout < 1 || cout > 12)
            throw new DiceForgeException("Bassin : Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (faces.length < 2 || faces.length > 4)
            throw new DiceForgeException("Bassin : Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+faces.length);
        this.faces = faces;
    }

    /**
     * Constructeur qui sert lorsque le bassin ne comporte que la même faces
     */
    public Bassin(int cout, Face facesUnique, int nbrFace){
        if (cout < 1 || cout > 12)
            throw new DiceForgeException("Bassin : Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (nbrFace < 2 || nbrFace > 4)
            throw new DiceForgeException("Bassin : Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+nbrFace);
        faces = new Face[nbrFace];
        for (int i = 0; i != nbrFace; ++i)
            faces[i] = new Face(facesUnique.getRessource());
    }

    public int getCout() {
        return cout;
    }

    public int nbrFaceRestante(){
        int i = 0;
        for (Face face:faces)
            if (face != null)
                ++i;
        return i;
    }

    public ArrayList<Integer> numFacesRestante(){
        if (nbrFaceRestante() == 0)
            return null;
        ArrayList<Integer> nums = new ArrayList<Integer>();
        for (int i = 0; i != faces.length; ++i)
            if (faces[i] != null)
                nums.add(i);
        return nums;
    }

    /**
     * A utiliser pour savoir les faces présentent, PAS pour en prendre une
     * @return la liste des faces. Aucune, certaines ou toutes peuvent être null
     */
    public Face[] getFace() {
        return faces;
    }

    /**
     * Cette méthode doit être utilisé pour retirer une face du bassin pour ensuite la graver sur un dé.
     * Il ne faut pas utiliser la méthode getFace() pour cela !
     */
    public Face retirerFace(int numFace){
        if (faces[numFace] == null)
            throw new DiceForgeException("Bassin : La face demandée à déjà été retirée");
        Face x = faces[numFace];
        faces[numFace] = null;
        return x;
    }
}

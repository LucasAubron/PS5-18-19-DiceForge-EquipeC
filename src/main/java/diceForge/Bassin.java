package diceForge;

public class Bassin {
    private int cout;
    private Face[] face;

    public Bassin(int cout, Face[] face) {//S'il n'y a pas une seule face pour tout le bassin
        if (cout < 1 || cout > 12)
            throw new RuntimeException("Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (face.length < 2 || face.length > 4)
            throw new RuntimeException("Nombre de face dans un bassin invalide. Min 2, max 4, actuel : "+face.length);
        this.face = face;
    }

    public Bassin(int cout, Face faceUnique, int nbrFace){//Un autre constructeur au cas ou toute les faces soit la même
        if (cout < 1 || cout > 12)
            throw new RuntimeException("Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (nbrFace < 2 || nbrFace > 4)
            throw new RuntimeException("Nombre de face dans un bassin invalide. Min 2, max 4, actuel : "+nbrFace);
        face = new Face[nbrFace];
        for (int i = 0; i != nbrFace; ++i)
            face[i] = new Face(faceUnique.getRessource());
    }

    public int getCout() {
        return cout;
    }

    public Face[] getFace() {//A utiliser pour savoir les faces présentent, PAS pour en prendre une
        return face;
    }

    public Face retirerFace(int numFace){
        if (face[numFace] == null)
            throw new RuntimeException("La face demandée à déjà été retirée");
        Face x = face[numFace];
        face[numFace] = null;
        return x;
    }
}

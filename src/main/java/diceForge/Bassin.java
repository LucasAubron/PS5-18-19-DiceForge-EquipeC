package diceForge;

public class Bassin {
    private int cout;
    private Face[] face;

    public Bassin(int cout, Face[] face) {
        /**
         * Constructeur dans le cas ou il y a des faces différentes dans le même bassin
         * et qu'il est plus simple de transmettre une liste
         */
        if (cout < 1 || cout > 12)
            throw new RuntimeException("Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (face.length < 2 || face.length > 4)
            throw new RuntimeException("Nombre de face dans un bassin invalide. Min 2, max 4, actuel : "+face.length);
        this.face = face;
    }

    public Bassin(int cout, Face faceUnique, int nbrFace){
        /**
         * Constructeur qui sert lorsque le bassin ne comporte que la même face
         */
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
    }//Utiliser uniquement pour savoir qu'elle face il y a dans le bassin

    public Face retirerFace(int numFace){
        /**
         * Cette méthode doit être utilisé pour retirer une face du bassin pour ensuite la graver sur un dé.
         * Il ne faut pas utiliser la méthode getFace() pour cela !
         */
        if (face[numFace] == null)
            throw new RuntimeException("La face demandée à déjà été retirée");
        Face x = face[numFace];
        face[numFace] = null;
        return x;
    }
}

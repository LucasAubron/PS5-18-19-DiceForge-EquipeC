package diceForge;

import java.util.Random;

/**
 * La classe représentant un dé (oui De c'est pas très intuitif, si quelqu'un a mieux il le refactor)
 */
class De {
    private Face[] faces;
    private int nbrFaceForge = 0;//Pour savoir combien de face le joueur à forgé

    Face[] getFaces() {
        return faces;
    }

    Face getFace(int num) { return faces[num]; }

    De(Face[] faces){
        if (faces.length != 6)//Pour la version minimale, le dé à 3 faces
            throw new DiceForgeException("Dé","Le nombre de face est invalide. Attendu : 6, actuel : "+faces.length);
        this.faces = faces;
    }

    Face lancerLeDe(){
        Random aleatoire = new Random();//Permet d'acceder au fonction de Random
        int face = aleatoire.nextInt(faces.length);//Nombre entre 0 et faces.length-1
        return faces[face];
    }

    void forger(Face faceAForger, int numFace){
        if (numFace < 0 || numFace > faces.length-1)
            throw new DiceForgeException("Dé","Le numéro de la face est invalide. Min : 1, max : "+(faces.length-1)+", actuel : "+numFace);
        faces[numFace] = faceAForger;
        ++nbrFaceForge;
    }

    /*int posFace1Or(){
        for (int i = 0; i != faces.length; ++i)
            if ()
    }*/

    int getNbrFaceForge() {
        return nbrFaceForge;
    }

    @Override
    public String toString(){
        return "" + faces[0] + " | " + faces[1] + " | " + faces[2] + " | " + faces[3] + " | " + faces[4] + " | " + faces[5];
    }
}

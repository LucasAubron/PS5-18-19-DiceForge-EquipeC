package diceForge;

import java.util.Random;

/**
 * La classe représentant un dé (oui De c'est pas très intuitif, si quelqu'un a mieux il le refactor)
 */
public class De {
    private Face[] faces;
    private int nbrFaceForge = 0;//Pour savoir combien de face le joueur à forgé

    public Face[] getFaces() {
        return faces;
    }

    public De(Face[] faces){
        if (faces.length != 3)//Pour la version minimale, le dé à 3 faces
            throw new RuntimeException("Le nombre de face est invalide. Attendu : 3, actuel : "+faces.length);
        this.faces = faces;
    }

    public Face lancerLeDe(){
        Random aleatoire = new Random();//Permet d'acceder au fonction de Random
        int face = aleatoire.nextInt(faces.length);//Nombre entre 0 et faces.length-1
        return faces[face];
    }

    public void forger(Face faceAForger, int numFace){
        if (numFace < 0 || numFace > faces.length-1)
            throw new RuntimeException("Le numéro de la face est invalide. Min : 1, max : "+(faces.length-1)+", actuel : "+numFace);
        faces[numFace] = faceAForger;
        ++nbrFaceForge;
    }
}

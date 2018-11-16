package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La classe représentant un dé (oui De c'est pas très intuitif, si quelqu'un a mieux il le refactor)
 */
public class De {
    private Face[] faces;
    private int nbrFaceForge = 0;//Pour savoir combien de face le joueur à forgé (pour la carte typhon)
    private Face derniereFace;
    private Afficheur afficheur;//sert uniquement pour l'afficheur
    private Joueur proprietaire;//idem
    private int id;//idem

    public Face[] getFaces() {
        return faces;
    }

    public Face getFace(int num) { return faces[num]; }

    public Face derniereFace(){return derniereFace;}

    De(Face[] faces, Afficheur afficheur, Joueur joueur, int id){
        this.proprietaire = joueur;
        this.afficheur = afficheur;
        this.id = id;
        if (faces.length != 6)
            throw new DiceForgeException("Dé","Le nombre de face est invalide. Attendu : 6, actuel : "+faces.length);
        this.faces = faces;
    }

    Face lancerLeDe(){
        Random aleatoire = new Random();//Permet d'acceder au fonction de Random
        derniereFace = faces[aleatoire.nextInt(faces.length)];//Nombre entre 0 et faces.length-1
        afficheur.resultatDe(proprietaire, id);
        return derniereFace;
    }

    void forger(Face faceAForger, int numFace){
        if (numFace < 0 || numFace > faces.length-1)
            throw new DiceForgeException("Dé","Le numéro de la face est invalide. Min : 0, max : "+(faces.length-1)+", actuel : "+numFace);
        faces[numFace] = faceAForger;
        ++nbrFaceForge;
    }

    public int getNbrFaceForge() {
        return nbrFaceForge;
    }

    @Override
    public String toString(){
        return "" + faces[0] + " | " + faces[1] + " | " + faces[2] + " | " + faces[3] + " | " + faces[4] + " | " + faces[5];
    }
}

package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La classe représentant un dé (oui De c'est pas très intuitif, si quelqu'un a mieux il le refactor)
 */
public class De {

    // Attributs ------------------------------------------------------------------------------------------------

    private Face[] faces;
    private int nbrFaceForge = 0;//Pour savoir combien de faces le joueur à forgé (pour la carte typhon)
    private Face faceActive;
    private Afficheur afficheur;//sert uniquement pour l'afficheur
    private Joueur proprietaire;//idem
    private int id;//idem

    // Constructeur ------------------------------------------------------------------------------------------------

    De(Face[] faces, Afficheur afficheur, Joueur joueur, int id){//les 6 faces en tableau, l'afficheur, le joueur
        this.proprietaire = joueur;                              //propriétaire du dé, et le numéro du dé (==son id)
        this.afficheur = afficheur;
        this.id = id;
        if (faces.length != 6)
            throw new DiceForgeException("Dé","Le nombre de face est invalide. Attendu : 6, actuel : "+faces.length);
        this.faces = faces;
    }

    // Méthodes ----------------------------------------------------------------------------------------------------


    public Face[] getFaces() {
        return faces;
    }

    public Face getFace(int num) { return faces[num]; }

    public Face getFaceActive(){return faceActive;}

    public void setFaceActive(Face faceCopiee){faceActive = faceCopiee;} // /!\ ATTENTION, sert uniquement pour la face miroir
                                                                        // ne change pas réellement le dé mais sa dernière face active
    public Face lancerLeDe(){
        Random aleatoire = new Random();//Permet d'acceder au fonction de Random
        faceActive = faces[aleatoire.nextInt(faces.length)];//Nombre entre 0 et faces.length-1
        afficheur.resultatDe(proprietaire, id);
        return faceActive;
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

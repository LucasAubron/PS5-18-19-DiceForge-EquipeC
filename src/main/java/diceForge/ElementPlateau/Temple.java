package diceForge.ElementPlateau;

import diceForge.Faces.Face;
import diceForge.Faces.FaceBouclier;
import diceForge.OutilJoueur.Ressource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe regroupant tout ce qui est dans le temple, c'est à dire :
 * le sanctuaire comportant les bassins accessibles par la forge
 * et les jardins comportant les bassins accessibles par les cartes.
 */
public class Temple {
    private Bassin[] sanctuaire = new Bassin[10];//10 bassins dans la version finale du jeu
    private Bassin[] jardin = new Bassin[1];//On ne met que la face Bouclier dans le jardin car c'est la seule des faces du jardin a avoir différentes formes

    public Temple(int nbrJoueur){
        int nbrFaceParBassin = (nbrJoueur == 2 ? 2 : 4);//S'il n'y a que 2 joueurs, il y a 2 faces par bassin, sinon 4
        Random random = new Random();

        sanctuaire[0] = new Bassin(2, new Face(new Ressource(3, Ressource.type.OR)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceOr);
        sanctuaire[1] = new Bassin(2, new Face(new Ressource(1, Ressource.type.LUNE)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceLune);

        sanctuaire[2] = new Bassin(3, new Face(new Ressource(4, Ressource.type.OR)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceOr);
        sanctuaire[3] = new Bassin(3, new Face(new Ressource(1, Ressource.type.SOLEIL)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceSoleil);

        List<Face> faceBassin4 = new ArrayList<>();//Quand il n'y a pas que des mêmes faces dans un bassin, ça devient compliqué
        faceBassin4.add(new Face(Face.typeFace.ADDITION, new Ressource[]{new Ressource(1, Ressource.type.SOLEIL), new Ressource(1, Ressource.type.PDG)}));//On ajoute chaque face manuellement
        faceBassin4.add(new Face(Face.typeFace.CHOIX, new Ressource[]{new Ressource(1, Ressource.type.LUNE), new Ressource(1, Ressource.type.SOLEIL), new Ressource(1, Ressource.type.OR)}));
        faceBassin4.add(new Face(Face.typeFace.ADDITION, new Ressource[]{new Ressource(2, Ressource.type.OR),new Ressource(1, Ressource.type.LUNE)}));
        faceBassin4.add(new Face(new Ressource(6, Ressource.type.OR)));
        if (nbrFaceParBassin == 2){//S'il n'y a que 2 joueurs, on retire 2 faces aléatoirement
            faceBassin4.remove(random.nextInt(faceBassin4.size()));
            faceBassin4.remove(random.nextInt(faceBassin4.size()));
        }
        sanctuaire[4] = new Bassin(4, faceBassin4, Bassin.typeBassin.Cout4);//Et on construit le bassin avec l'autre constructeur

        sanctuaire[5] = new Bassin(5, new Face(Face.typeFace.CHOIX, new Ressource[]{new Ressource(2, Ressource.type.PDG), new Ressource(3, Ressource.type.OR)}), nbrFaceParBassin, Bassin.typeBassin.Cout5);

        sanctuaire[6] = new Bassin(6, new Face(new Ressource(2, Ressource.type.LUNE)), nbrFaceParBassin, Bassin.typeBassin.Cout6);

        sanctuaire[7] = new Bassin(8, new Face(new Ressource(2, Ressource.type.SOLEIL)), nbrFaceParBassin, Bassin.typeBassin.Cout8FaceSoleil);

        sanctuaire[8] = new Bassin(8, new Face(new Ressource(3, Ressource.type.PDG)), nbrFaceParBassin, Bassin.typeBassin.Cout8FacePdg);

        List<Face> faceBassin9 = new ArrayList<>();//Même chose que la face 4
        faceBassin9.add(new Face(new Ressource(4, Ressource.type.PDG)));
        faceBassin9.add(new Face(Face.typeFace.ADDITION, new Ressource[]{new Ressource(1, Ressource.type.PDG), new Ressource(1, Ressource.type.LUNE), new Ressource(1, Ressource.type.OR), new Ressource(1, Ressource.type.SOLEIL)}));
        faceBassin9.add(new Face(Face.typeFace.CHOIX, new Ressource[]{new Ressource(2, Ressource.type.LUNE), new Ressource(2, Ressource.type.OR), new Ressource(2, Ressource.type.SOLEIL)}));
        faceBassin9.add(new Face(Face.typeFace.ADDITION, new Ressource[]{new Ressource(2, Ressource.type.LUNE), new Ressource(2, Ressource.type.PDG)}));
        if (nbrFaceParBassin == 2){
            faceBassin9.remove(random.nextInt(faceBassin9.size()));
            faceBassin9.remove(random.nextInt(faceBassin9.size()));
        }

        sanctuaire[9] = new Bassin(12, faceBassin9, Bassin.typeBassin.Cout12);

        List<Face> faceJardin0 = new ArrayList<>();
        faceJardin0.add(new FaceBouclier(new Ressource(2, Ressource.type.SOLEIL)));
        faceJardin0.add(new FaceBouclier(new Ressource(2, Ressource.type.LUNE)));
        faceJardin0.add(new FaceBouclier(new Ressource(3, Ressource.type.OR)));
        faceJardin0.add(new FaceBouclier(new Ressource(3, Ressource.type.PDG)));

        jardin[0] = new Bassin(0, faceJardin0, Bassin.typeBassin.Cout2FaceLune.Bouclier);
    }

    public Bassin[] getSanctuaire() {return sanctuaire;}

    public Bassin[] getJardin() {
        return jardin;
    }
}

package diceForge;

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
    private Bassin[] jardin = new Bassin[1];//Pour l'instant qu'1 bassin dans le jardin

    public Temple(int nbrJoueur){
        int nbrFaceParBassin = (nbrJoueur == 2 ? 2 : 4);//S'il n'y a que 2 joueurs, il y a 2 faces par bassin, sinon 4
        Random random = new Random();

        sanctuaire[0] = new Bassin(2, new Face(new Ressource[][]{{new Or(3)}}), nbrFaceParBassin);
        sanctuaire[1] = new Bassin(2, new Face(new Ressource[][]{{new Lune(1)}}), nbrFaceParBassin);
        sanctuaire[2] = new Bassin(3, new Face(new Ressource[][]{{new Or(4)}}), nbrFaceParBassin);
        sanctuaire[3] = new Bassin(3, new Face(new Ressource[][]{{new Soleil(1)}}), nbrFaceParBassin);

        List<Face> faceBassin4 = new ArrayList<>();//Quand il n'y a pas que des mêmes faces dans un bassin, ça devient compliqué
        faceBassin4.add(new Face(new Ressource[][]{{new Soleil(1), new PointDeGloire(1)}}));//On ajoute chaque face manuellement
        faceBassin4.add(new Face(new Ressource[][]{{new Lune(1)}, {new Soleil(1)}, {new Or(1)}}));
        faceBassin4.add(new Face(new Ressource[][]{{new Lune(1), new Or(2)}}));
        faceBassin4.add(new Face(new Ressource[][]{{new Or(6)}}));
        if (nbrFaceParBassin == 2){//S'il n'y a que 2 joueurs, on retire 2 faces aléatoirement
            faceBassin4.remove(random.nextInt(faceBassin4.size()));
            faceBassin4.remove(random.nextInt(faceBassin4.size()));
        }
        sanctuaire[4] = new Bassin(4, faceBassin4);//Et on construit le bassin avec l'autre constructeur

        sanctuaire[5] = new Bassin(5, new Face(new Ressource[][]{{new PointDeGloire(2)}, {new Or(3)}}), nbrFaceParBassin);
        sanctuaire[6] = new Bassin(6, new Face(new Ressource[][]{{new Lune(2)}}), nbrFaceParBassin);
        sanctuaire[7] = new Bassin(8, new Face(new Ressource[][]{{new Soleil(2)}}), nbrFaceParBassin);
        sanctuaire[8] = new Bassin(8, new Face(new Ressource[][]{{new PointDeGloire(3)}}), nbrFaceParBassin);

        List<Face> faceBassin9 = new ArrayList<>();//Même chose que la face 4
        faceBassin9.add(new Face(new Ressource[][]{{new PointDeGloire(1), new Lune(1), new Or(1), new Soleil(1)}}));
        faceBassin9.add(new Face(new Ressource[][]{{new PointDeGloire(4)}}));
        faceBassin9.add(new Face(new Ressource[][]{{new Lune(2)}, {new Or(2)}, {new Soleil(2)}}));
        faceBassin9.add(new Face(new Ressource[][]{{new Lune(2), new PointDeGloire(2)}}));
        if (nbrFaceParBassin == 2){
            faceBassin9.remove(random.nextInt(faceBassin9.size()));
            faceBassin9.remove(random.nextInt(faceBassin9.size()));
        }
        sanctuaire[9] = new Bassin(12, faceBassin9);

        List<Face> faceJardin0 = new ArrayList<>();
        faceJardin0.add(new FaceBouclier(new Ressource[]{new Soleil(2)}));
        faceJardin0.add(new FaceBouclier(new Ressource[]{new Lune(2)}));
        faceJardin0.add(new FaceBouclier(new Ressource[]{new Or(3)}));
        faceJardin0.add(new FaceBouclier(new Ressource[]{new PointDeGloire(3)}));
        jardin[0] = new Bassin(0, faceJardin0);
    }

    public Bassin[] getSanctuaire() {return sanctuaire;}

    public Bassin[] getJardin() {
        return jardin;
    }
}

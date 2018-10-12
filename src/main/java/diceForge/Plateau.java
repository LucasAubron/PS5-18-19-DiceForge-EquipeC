package diceForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {
    private List<Bassin> piscine = new ArrayList<>();//Ce système m'a l'air compliqué, il faut voir si on le garde
    public Plateau(){//C'est ici qu'on défini ce qu'il va y avoir dans le plateau
        piscine.add(new Bassin(2, new ArrayList<>()));//On ajoute un nouveau bassin
        piscine.get(0).ajouterFace(new Face(new ArrayList<>()));//On lui ajoute une nouvelle face
        piscine.get(0).getFace().get(0).ajouterRessource(new Soleil(), 0);//On ajoute à la face un soleil

        piscine.get(0).ajouterFace(new Face(new ArrayList<>()));//On lui ajoute une nouvelle face
        piscine.get(0).getFace().get(0).ajouterRessource(new Soleil(), 0);//On ajoute à la face un soleil

        piscine.add(new Bassin(2, new ArrayList<>()));//On lui ajoute une nouvelle face
        piscine.get(1).getFace().get(0).ajouterRessource(new Or(), 0, 2);//On ajoute à la face 2 soleil

        piscine.add(new Bassin(2, new ArrayList<>()));//On lui ajoute une nouvelle face
        piscine.get(1).getFace().get(0).ajouterRessource(new Or(), 0, 2);//On ajoute à la face 2 soleil
    }
}

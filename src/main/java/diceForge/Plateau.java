package diceForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {
    private Bassin[] piscine;
    public Plateau(){//C'est ici qu'on défini ce qu'il va y avoir dans le plateau
        piscine = new Bassin[2];//Pour la version minimale, il n'y a que 2 bassins
        Face or = new Face(new Ressource[][]{{new Or(), new Or()}});//On initialise la face or
        Face soleil = new Face(new Ressource[][]{{ new Soleil()}});//Et la face soleil
        piscine[0] = new Bassin(2, or, 2);//Et on créé chaque bassins avec leur face
        piscine[1] = new Bassin(2, soleil, 1);
    }
}

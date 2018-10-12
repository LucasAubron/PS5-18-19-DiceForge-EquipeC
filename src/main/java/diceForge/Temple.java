package diceForge;

public class Temple {
    private Bassin[] sanctuaire = new Bassin[2];//Pour la version minimale, il n'y a que 2 bassins
    public Temple(){
        Face or = new Face(new Ressource[][]{{new Or(), new Or()}});//On initialise la face or
        Face soleil = new Face(new Ressource[][]{{ new Soleil()}});//Et la face soleil
        sanctuaire[0] = new Bassin(2, or, 2);//Et on créé chaque bassins avec leur face
        sanctuaire[1] = new Bassin(2, soleil, 1);
    }
}

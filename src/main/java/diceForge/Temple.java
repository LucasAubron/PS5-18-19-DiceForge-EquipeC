package diceForge;

/**
 * Classe regroupant tout ce qui est dans le temple, c'est à dire :
 * le sanctuaire comportant les bassins accessibles par la forge
 * et les jardins comportant les bassins accessibles par les cartes.
 */
public class Temple {
    private Bassin[] sanctuaire = new Bassin[2];//Pour la version minimale, il n'y a que 2 bassins
    public Temple(){
        Face or = new Face(new Ressource[][]{{new Or(2)}});//On initialise la face or
        Face soleil = new Face(new Ressource[][]{{ new Soleil(1)}});//Et la face soleil
        sanctuaire[0] = new Bassin(2, or, 2);//Et on créé chaque bassins avec leur face
        sanctuaire[1] = new Bassin(2, soleil, 2);
    }

    public Bassin[] getSanctuaire() {return sanctuaire;}
}

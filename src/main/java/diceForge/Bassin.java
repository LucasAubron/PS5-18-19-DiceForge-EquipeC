package diceForge;

import java.util.ArrayList;
import java.util.List;

public class Bassin {
    private int cout;
    private List<Face> faces;

    /**
     * Constructeur dans le cas ou il y a des faces différentes dans le même bassin
     * et qu'il est plus simple de transmettre une liste
     */
    public Bassin(int cout, List<Face> faces) {
        if (cout < 1 || cout > 12)
            throw new DiceForgeException("Bassin","Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (faces.size() < 2 || faces.size() > 4)
            throw new DiceForgeException("Bassin","Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+faces.size());
        this.faces = faces;
    }

    /**
     * Constructeur qui sert lorsque le bassin ne comporte que la même faces
     */
    public Bassin(int cout, Face facesUnique, int nbrFace){
        if (cout < 1 || cout > 12)
            throw new DiceForgeException("Bassin","Le cout du bassin n'est pas bon. Min 1, max 12, actuel : "+cout);
        this.cout = cout;
        if (nbrFace < 2 || nbrFace > 4)
            throw new DiceForgeException("Bassin","Nombre de faces dans un bassin invalide. Min 2, max 4, actuel : "+nbrFace);
        faces = new ArrayList<>();
        for (int i = 0; i != nbrFace; ++i)
            faces.add(new Face(facesUnique.getRessource()));
    }

    public int getCout() {
        return cout;
    }

   /**
     * A utiliser pour savoir les faces présentent, PAS pour en prendre une
     * @return la liste des faces. Aucune, certaines ou toutes peuvent être null
     */
    public List<Face> getFaces() {
        return faces;
    }

    public Face getFace(int num) {
        return faces.get(num);
    }
    /**
     * Cette méthode doit être utilisé pour retirer une face du bassin pour ensuite la graver sur un dé.
     * Il ne faut pas utiliser la méthode getFace() pour cela !
     */
    public Face retirerFace(int numFace){
        return faces.remove(numFace);
    }

    public boolean equals(Bassin bassin){
        if (faces.isEmpty() && bassin.getFaces().isEmpty() && cout == bassin.getCout())
            return true;
        if (cout == bassin.getCout() && faces.size() == bassin.getFaces().size() && faces.get(0).toString().equals(bassin.getFaces().get(0).toString()))
            return true;
        return false;
    }

    @Override
    public String toString(){
        return "" + faces;
    }
}
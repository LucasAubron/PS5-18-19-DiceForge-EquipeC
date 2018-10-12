package diceForge;

import java.util.List;

public class Bassin {
    private int cout;
    private List<Face> face;

    public Bassin(int cout, List<Face> face) {
        this.cout = cout;
        this.face = face;
    }

    public void ajouterFace(Face face){
        this.face.add(face);
    }

    public int getCout() {
        return cout;
    }

    public List<Face> getFace() {
        return face;
    }
}

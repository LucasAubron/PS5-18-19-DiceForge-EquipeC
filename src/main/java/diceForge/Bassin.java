package diceForge;

import java.util.List;

public class Bassin {
    private int cout;
    private List<List<Face>> face;

    public Bassin(int cout, List<List<Face>> face) {
        this.cout = cout;
        this.face = face;
    }

    public int getCout() {
        return cout;
    }

    public List<List<Face>> getFace() {
        return face;
    }
}

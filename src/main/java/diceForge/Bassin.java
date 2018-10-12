package diceForge;

import java.util.ArrayList;
import java.util.List;

public class Bassin {
    private int cout;
    private Face[] face;

    public Bassin(int cout, Face[] face) {
        this.cout = cout;
        this.face = face;
    }

    public int getCout() {
        return cout;
    }

    public Face getFace(int nbrFace) {
        return face[nbrFace];
    }
}

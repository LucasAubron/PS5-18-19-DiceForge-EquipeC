package diceForge;

import java.util.List;

public class Face {
    private Ressource[][] ressource;
    public Face(Ressource[][] ressource) {
        this.ressource = ressource;
    }

    public Ressource[][] getRessource() {
        return ressource;
    }
}

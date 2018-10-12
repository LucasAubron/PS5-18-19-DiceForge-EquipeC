package diceForge;

import java.util.List;

public class Face {
    private List<List<Ressource>> ressource;
    public Face(List<List<Ressource>> ressource) {
        this.ressource = ressource;
    }

    public void ajouterRessource(Ressource ressource, int numList){
        this.ressource.get(numList).add(ressource);
    }

    public void ajouterRessource(Ressource ressource, int numList, int nbr){
        for (int i = 0; i != nbr; ++i){
            this.ressource.get(numList).add(ressource);
        }
    }

    public List<List<Ressource>> getRessource() {
        return ressource;
    }
}

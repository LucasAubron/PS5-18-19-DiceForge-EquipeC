package diceForge.Faces;

import diceForge.OutilJoueur.Ressource;

public class FaceBouclier extends Face {
    Ressource ressource;
    public FaceBouclier(Ressource ressource){
        super(typeFace.BOUCLIER);
        this.ressource = ressource;
    }

    @Override
    public String toString(){
        return "bouclier de type" + ressource.toString();
    }
}

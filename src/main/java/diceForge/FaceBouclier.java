package diceForge;

public class FaceBouclier extends Face {
    Ressource ressource;
    FaceBouclier(Ressource ressource){
        super(typeFace.BOUCLIER);
        this.ressource = ressource;
    }

    @Override
    public String toString(){
        return "bouclier de type" + ressource.toString();
    }
}

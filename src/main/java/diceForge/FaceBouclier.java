package diceForge;

public class FaceBouclier extends Face {
    Ressource ressource;
    FaceBouclier(Ressource[] ressources){
        super(new Ressource[][]{ressources});
        this.ressource= ressources[0];
    }

    @Override
    public String toString(){
        return "bouclier de type" + ressource.toString();
    }
}

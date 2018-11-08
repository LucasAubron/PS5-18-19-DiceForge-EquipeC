package diceForge;

public class FaceBouclier extends Face {
    FaceBouclier(Ressource[] ressources){
        super(new Ressource[][]{ressources});
    }

    @Override
    public String toString(){
        return "Face bouclier";
    }
}

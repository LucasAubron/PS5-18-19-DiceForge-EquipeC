package diceForge;

public class FaceBateauCeleste extends Face {
    private Temple temple;
    public FaceBateauCeleste(Temple temple){
        super(new Ressource[][]{{}});
        this.temple = temple;
    }

    public Temple getTemple() {
        return temple;
    }
}

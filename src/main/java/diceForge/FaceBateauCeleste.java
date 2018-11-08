package diceForge;

class FaceBateauCeleste extends Face {
    private Temple temple;
    FaceBateauCeleste(Temple temple){
        super(new Ressource[][]{{}});
        this.temple = temple;
    }

    Temple getTemple() {
        return temple;
    }
}

package diceForge;

class Afficheur {

    private Plateau plateau;
    private String info = "Hello World";

    Afficheur(Plateau plateau){
        this.plateau = plateau;
    }

    @Override
    public String toString(){
        return info;
    }
}

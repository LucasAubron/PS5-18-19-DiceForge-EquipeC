package diceForge;

class Afficheur {

    private Plateau plateau;
    private String info = "Hello World";
    private boolean modeVerbeux;

    Afficheur(boolean modeVerbeux, Plateau plateau){
        this.plateau = plateau;
        this.modeVerbeux = modeVerbeux;
    }

    @Override
    public String toString(){
        return info;
    }
}

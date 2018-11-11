package diceForge;

class Afficheur {

    private Plateau plateau;
    private String info = "Hello World";
    private boolean modeVerbeux;
    private Coordinateur coordinateur;

    Afficheur(boolean modeVerbeux,Coordinateur coordinateur, Plateau plateau){
        this.plateau = plateau;
        this.modeVerbeux = modeVerbeux;
        this.coordinateur = coordinateur;
    }

    @Override
    public String toString(){
        return info;
    }
}

package diceForge;

public class Coordinateur {
    Plateau plateau;
    private int nbrManche;
    private int mancheActuelle = 1;

    public Coordinateur(Plateau plateau, int nbrManche){
        this.plateau = plateau;
        this.nbrManche = nbrManche;
    }
}

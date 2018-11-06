package diceForge;

public class Sanglier extends Carte {
    int idJoueurMaitre;
    public Sanglier(int idJoueurMaitre){
        super (new Ressource[]{new Lune(3)}, 4, "Sanglier");
        this.idJoueurMaitre = idJoueurMaitre;
    }
}

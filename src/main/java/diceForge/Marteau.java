package diceForge;

public class Marteau extends Carte {
    private int point = 0;
    private int niveau = 0;

    public Marteau(){
        super (new Ressource[]{new Lune(1)}, 0, "Marteau");
    }

    /**
     * Permet d'ajouter des points à son marteau
     * @param nbrPoints le nombre de points à ajouter
     * @return Le nombre de point en trop si le marteau est complété, 0 sinon
     */
    public int ajouterPoints(int nbrPoints){
        point += nbrPoints;
        if (nbrPoints >= 15){
            ++niveau;
            point -= 15;
            if (niveau > 2)
                return point;
        }
        return 0;
    }

    @Override
    public int getNbrPointGloire(){
        if (niveau == 1)
            return 10;
        if (niveau == 2)
            return 25;
        return 0;
    }
}
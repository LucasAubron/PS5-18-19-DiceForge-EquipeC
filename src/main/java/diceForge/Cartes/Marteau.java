package diceForge.Cartes;


import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.DiceForgeException;

public class Marteau extends Carte {
    private int point = 0;
    private int niveau = 0;

    public Marteau(){
        super (new Ressource[]{new Ressource(1, Ressource.type.LUNE)}, 0, Noms.Marteau);
    }

    @Override
    public Carte clone(){
        return new Marteau();
    }

    /**
     * Permet d'ajouter des points à son marteau
     * @param nbrPoints le nombre de points à ajouter
     * @return Le nombre de point en trop si le marteau est complété, 0 sinon
     */
    public int ajouterPoints(int nbrPoints){
        if (niveau > 1) return nbrPoints;
        point += nbrPoints;
        if (point >= 15){
            ++niveau;
            point -= 15;
            if (niveau > 1)
                return point;
        }
        return 0;
    }

    int getNiveau(){return niveau;}

    int getPoints(){return point;}

    @Override
    public int getNbrPointGloire(){
        if (niveau == 1)
            return 10;
        if (niveau == 2)
            return 25;
        return 0;
    }

    @Override
    public String toString(){
        if (niveau == 0)
            return "Marteau phase I: " + point + "/15";
        if (niveau == 1)
            return "Marteau phase II: " + point + "/15";
        if (niveau == 2)
            return "Marteau II: 15/15";
        else
            throw new DiceForgeException("Marteau", "Le marteau est a un niveau incorect; accepté min 0 max 2, actuel:" + niveau);
    }
}

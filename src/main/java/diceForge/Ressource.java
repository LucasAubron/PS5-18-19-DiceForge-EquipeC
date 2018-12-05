package diceForge;

/**
 * Parce qu'il va y avoir des faces de dé complexes,
 * et aussi pour avoir un unique tableau dans Face,
 * on créé une classe Ressource que l'on dérive en chaque ressource de base
 */
public class Ressource {
    private int quantite;
    public enum type{OR, SOLEIL, LUNE, PDG;}//PDG == point de gloire
    private type typeRessource;

    Ressource(int quantite, type typeR){//version a garder après refactor
        if (quantite < 0)
            throw new DiceForgeException("Ressource","La quantité donnée est invalide. Min 0, actuelle : "+quantite);
        this.quantite = quantite;
        this.typeRessource = typeR;
    }

    public int getQuantite(){return quantite;}

    public type getType(){return typeRessource;}

    public boolean estDuType(Enum typeR){
        if (typeRessource == typeR)
            return true;
        return false;
    }

    @Override
    public String toString(){
        if (typeRessource == type.OR)
            return " Or";
        if (typeRessource == type.SOLEIL)
            return " Soleil";
        if (typeRessource == type.LUNE)
            return " Lune";
        if (typeRessource == type.PDG)
            return " Point de gloire";
        throw new DiceForgeException("Ressource", "Le type de ressource n'est pas reconnu: " + typeRessource);
    }
}

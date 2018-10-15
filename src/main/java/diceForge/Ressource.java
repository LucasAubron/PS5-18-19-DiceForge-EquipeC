package diceForge;

/**
 * Parce qu'il va y avoir des faces de dé complexes,
 * et aussi pour avoir un unique tableau dans Face,
 * on créé une classe Ressource que l'on dérive en chaque ressource de base,
 * mais aussi (plus tard) en face de dé complexe
 */
public abstract class Ressource {
    private int quantite;
    public int getQuantite(){return quantite;}
    Ressource(int quantite){
        if (quantite < 0)
            throw new DiceForgeException("La quantité donnée est invalide. Min 0, actuelle : "+quantite);
        this.quantite = quantite;
    }
}

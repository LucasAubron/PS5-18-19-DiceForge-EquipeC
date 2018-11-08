package diceForge;

/**
 * Parce qu'il va y avoir des faces de dé complexes,
 * et aussi pour avoir un unique tableau dans Face,
 * on créé une classe Ressource que l'on dérive en chaque ressource de base
 */
public abstract class Ressource {
    private int quantite;
    int getQuantite(){return quantite;}
    Ressource(int quantite){
        if (quantite < 0)
            throw new DiceForgeException("Ressource","La quantité donnée est invalide. Min 0, actuelle : "+quantite);
        this.quantite = quantite;
    }
}

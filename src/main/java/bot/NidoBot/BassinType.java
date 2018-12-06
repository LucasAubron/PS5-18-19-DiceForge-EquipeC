package bot.NidoBot;

import diceForge.OutilJoueur.Ressource;

public class BassinType {
    private int num;
    private Ressource.type nom;
    public BassinType(int num, Ressource.type nom){
        this.num = num;
        this.nom = nom;
    }
    public int getNum(){return this.num;}
    public Ressource.type getNom(){return this.nom;}
}

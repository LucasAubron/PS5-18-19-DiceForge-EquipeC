package bot.NidoBot;

import diceForge.Bassin;

public class BassinType {
    private int num;
    private String nom;
    public BassinType(int num, String nom){
        this.num = num;
        this.nom = nom;
    }
    public int getNum(){return this.num;}
    public String getNom(){return this.nom;}
}

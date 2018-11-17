package bot.NidoBot;

public class Stats {
    private int nbLunes;
    private int nbSoleils;
    public Stats(){
        this.nbLunes = 0;
        this.nbSoleils = 0;
    }
    public void incrementNbLunes(){this.nbLunes++;}
    public void incrementNbSoleils(){this.nbSoleils++;}
    public int getNbLunes(){return this.nbLunes;}
    public int getNbSoleils(){return this.nbSoleils;}
}
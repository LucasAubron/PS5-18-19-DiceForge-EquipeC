package bot.mlgBot;


import diceForge.Structure.Main;

public class Run {
    public static void main(String[] args) {
        long temps = System.currentTimeMillis();
        for (int i = 0; i != 5000; ++i) {
            Main.main(null);
            long x = (System.currentTimeMillis()-temps)/1000;
            System.out.println("Temps écoulé: "+x/3600+"h "+(x%3600)/60+"min "+x%60+"sec");
        }

    }
}

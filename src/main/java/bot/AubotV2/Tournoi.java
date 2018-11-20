package bot.AubotV2;

public class Tournoi {
    private int nombreDeJoueurParPartie;
    private int nombreDeMatchParConfrontation;
    private int[] victoireCumulesParJoueurs = new int[10];
    private String[] pathFiles1V1 = new String[]{"src/main/java/bot/AubotV2/1V1/Bot", "src/main/java/bot/AubotV2/1V1/Bot (1)", "src/main/java/bot/AubotV2/1V1/Bot (2)",  "src/main/java/bot/AubotV2/1V1/Bot (3)",  "src/main/java/bot/AubotV2/1V1/Bot (4)",  "src/main/java/bot/AubotV2/1V1/Bot (5)",  "src/main/java/bot/AubotV2/1V1/Bot (6)",  "src/main/java/bot/AubotV2/1V1/Bot (7)",  "src/main/java/bot/AubotV2/1V1/Bot (8)",  "src/main/java/bot/AubotV2/1V1/Bot (9)",   "src/main/java/bot/AubotV2/1V1/Bot (10)"};
    private String[] pathFiles1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1/Bot", "src/main/java/bot/AubotV2/1V1V1/Bot (1)", "src/main/java/bot/AubotV2/1V1V1/Bot (2)",  "src/main/java/bot/AubotV2/1V1V1/Bot (3)",  "src/main/java/bot/AubotV2/1V1V1/Bot (4)",  "src/main/java/bot/AubotV2/1V1V1/Bot (5)",  "src/main/java/bot/AubotV2/1V1V1/Bot (6)",  "src/main/java/bot/AubotV2/1V1V1/Bot (7)",  "src/main/java/bot/AubotV2/1V1V1/Bot (8)",  "src/main/java/bot/AubotV2/1V1V1/Bot (9)",   "src/main/java/bot/AubotV2/1V1/Bot (10)"};
    private String[] pathFiles1V1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1V1/Bot", "src/main/java/bot/AubotV2/1V1V1V1/Bot (1)", "src/main/java/bot/AubotV2/1V1V1V1/Bot (2)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (3)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (4)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (5)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (6)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (7)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (8)",  "src/main/java/bot/AubotV2/1V1V1V1/Bot (9)",   "src/main/java/bot/AubotV2/1V1/Bot (10)"};
    Tournoi(int n, int m){
        this.nombreDeJoueurParPartie = n;
        this.nombreDeMatchParConfrontation = m;
        initTournoi();
    }

    void initTournoi(){
        switch (nombreDeJoueurParPartie){
            case 2:
                for (int j1 = 0; j1<10; j1++)
                    for (int j2 = 0; j2 <10; j2++)
                        if (j1 != j2)
                            for (int indicePartie = 0; indicePartie < nombreDeMatchParConfrontation; indicePartie++)
                                new MatchTournoi(new String[]{pathFiles1V1[j1], pathFiles1V1[j2]});
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }
}


package bot.AubotV2;

public class Tournoi {
    private int nombreDeJoueurParPartie;
    private int nombreDeMatchParConfrontation;
    private int[] victoireCumulesParJoueurs = new int[10];
    private String[] pathFiles1V1 = new String[]{"src/main/java/bot/AubotV2/1V1/Bot1", "src/main/java/bot/AubotV2/1V1/Bot2",  "src/main/java/bot/AubotV2/1V1/Bot3",  "src/main/java/bot/AubotV2/1V1/Bot4",  "src/main/java/bot/AubotV2/1V1/Bot5",  "src/main/java/bot/AubotV2/1V1/Bot6",  "src/main/java/bot/AubotV2/1V1/Bot7",  "src/main/java/bot/AubotV2/1V1/Bot8",  "src/main/java/bot/AubotV2/1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] pathFiles1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] pathFiles1V1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    Tournoi(int n, int m){
        this.nombreDeJoueurParPartie = n;
        this.nombreDeMatchParConfrontation = m;
        initTournoi();
    }

    void initTournoi(){
        MatchTournoi match = null;
        switch (nombreDeJoueurParPartie){
            case 2:
                for (int j1 = 0; j1<10; j1++)
                    for (int j2 = 0; j2 <10; j2++)
                        if (j1 != j2){
                            match = new MatchTournoi(new String[]{pathFiles1V1[j1], pathFiles1V1[j2]}, nombreDeMatchParConfrontation/nombreDeJoueurParPartie);
                            victoireCumulesParJoueurs[j1] += match.getResultatMatch()[0];
                            victoireCumulesParJoueurs[j2] += match.getResultatMatch()[1];
                        }
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    int[] getResultats(){
        return victoireCumulesParJoueurs;
    }
}


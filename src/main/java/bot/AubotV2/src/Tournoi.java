package bot.AubotV2.src;


import diceForge.OutilJoueur.Joueur;

public class Tournoi {
    private int nombreDeJoueurParPartie;
    private int nombreDeMatchParConfrontation;
    private int[] victoireCumulesParJoueurs = new int[10];
    private int[] pointCumulesParJoueurs = new int[10];
    private Joueur.Bot[] Enum2J = new Joueur.Bot[]{Joueur.Bot.A1,Joueur.Bot.A2,Joueur.Bot.A3,Joueur.Bot.A4,Joueur.Bot.A5,Joueur.Bot.A6,Joueur.Bot.A7,Joueur.Bot.A8,Joueur.Bot.A9,Joueur.Bot.A10};
    private Joueur.Bot[] Enum3J = new Joueur.Bot[]{};
    private Joueur.Bot[] Enum4J = new Joueur.Bot[]{};

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
                    for (int j2 = j1 +1; j2 <10; j2++){
                        match = new MatchTournoi(new Joueur.Bot[]{Enum2J[j1], Enum2J[j2]}, nombreDeMatchParConfrontation);
                        victoireCumulesParJoueurs[j1] += match.getResultatMatch()[0];
                        victoireCumulesParJoueurs[j2] += match.getResultatMatch()[1];
                        pointCumulesParJoueurs[j1] += match.getPointMatch()[0];
                        pointCumulesParJoueurs[j2] += match.getPointMatch()[1];
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

    int[] getPoints(){ return pointCumulesParJoueurs; }
}


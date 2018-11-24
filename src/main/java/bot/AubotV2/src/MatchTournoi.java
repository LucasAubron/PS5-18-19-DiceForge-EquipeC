package bot.AubotV2;

import diceForge.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static diceForge.Joueur.Jeton.CERBERE;
import static diceForge.Joueur.Jeton.TRITON;

public class MatchTournoi{
    private int nombreDePartieParMatch;
    private int nombreDeJoueur;
    private Joueur.Bot[] enumBot;
    private int[] resultatMatch;
    private int[] pointMatch;

    MatchTournoi(Joueur.Bot[] enumBot, int n) {
        this.nombreDePartieParMatch = n;
        this.nombreDeJoueur = enumBot.length;
        this.enumBot = enumBot;
        initMatch();
    }

    private void initMatch() {
        switch (nombreDeJoueur) {
            case 2:
                resultatMatch = new int[]{0,0};
                pointMatch = new int[]{0,0};
                for (int partie = 0; partie < nombreDePartieParMatch/1000; partie++) { //On divise par 1000 parce que le coordinateur fait déjà 1000 partie
                    Coordinateur coordinateur = new Coordinateur(false, new Joueur.Bot[]{enumBot[0],enumBot[1]});
                    for (int i = 0; i<2; i++) {
                        resultatMatch[i] += coordinateur.getNbrVictoire()[i];
                        pointMatch[i] += coordinateur.getPtsGloireCumules()[i];
                    }
                }
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }


    int[] getResultatMatch() {
        return resultatMatch;
    }

    int[] getPointMatch() {
        return pointMatch;
    }
}
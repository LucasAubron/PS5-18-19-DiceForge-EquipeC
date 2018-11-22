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
    private String[] filePath;
    private int[] resultatMatch;
    private int nbrManche;
    private Afficheur afficheur;

    MatchTournoi(String[] filePath, int n) {
        this.afficheur = new Afficheur(false);
        this.nombreDePartieParMatch = n;
        this.nombreDeJoueur = filePath.length;
        this.filePath = filePath;
        this.resultatMatch = new int[nombreDeJoueur];
        this.nbrManche = (nombreDeJoueur == 3) ? 10 : 9;
        initMatch();
    }

    private void initMatch() {
        int maxPoint = -1; //permet de déterminer le ou les gagnants
        List<Integer> idGagnant = new ArrayList(Arrays.asList(-1)); //idem
        for (int partie = 0; partie < nombreDePartieParMatch; partie++) {
        }
    }


    int[] getResultatMatch() {
        return resultatMatch;
    }
}
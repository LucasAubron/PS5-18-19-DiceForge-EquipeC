package bot.AubotV2;

import java.util.Arrays;

public class Evolution {
    int nombreDeJoueurs;
    int nombreDePartieParMatch;
    int nombreDePopulationMax;
    String[] eliteFile = new String[5];
    String[] maillonFaibleFile = new String[5];

    Evolution(int n, int m, int p){
        this.nombreDeJoueurs = n;
        this.nombreDePartieParMatch = m;
        this.nombreDePopulationMax = p;
        initEvolution();
    }

    private void initEvolution(){
        for (int population = 1; population <= nombreDePopulationMax; population++){
            System.out.println("Population n°" + population +":");
            selection();
            croisement();
            mutation();
        }
    }

    private void selection(){
        Tournoi tournoi = new Tournoi(nombreDeJoueurs, nombreDePartieParMatch);
        int[] res = tournoi.getResultats();
        int[] eliteId = new int[5];
        int[] eliteRes = new int[5];
        int minVictoireElite = nombreDePartieParMatch + 1;
        for (int i=0; i <10; i++) {
            System.out.println("Joueur n°" + (i + 1) + ": " + res[i] + "/" + (9*nombreDePartieParMatch) + " parties gagnées");

            }
        /*
        Arrays.sort(eliteId);
        System.out.println("\n\nLes joueurs n°" + (eliteId[0]+1) + ", " + (eliteId[1]+1) + ", " + (eliteId[2]+1) + ", " + (eliteId[3]+1) + " et " + (eliteId[4]+1) + " survivent\n\n-----------------------------------------------------------------------\n");
        */
    }

    private void croisement(){
    }

    private void mutation(){
    }

}

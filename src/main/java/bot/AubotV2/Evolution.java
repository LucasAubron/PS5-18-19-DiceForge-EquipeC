package bot.AubotV2;

public class Evolution {
    int nombreDeJoueurs;
    int nombreDePartieParMatch;
    int nombreDePopulationMax;
    String[] elite = new String[5];
    String[] maillonFaible = new String[5];

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
        for (int i=0; i <10; i++)
            System.out.println("Joueur n°" + (i+1) + ": " + res[i] + "Points de gloire");
    }

    private void croisement(){
    }

    private void mutation(){
    }

}

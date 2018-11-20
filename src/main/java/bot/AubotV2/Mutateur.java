package bot.AubotV2;

import diceForge.Afficheur;
import diceForge.Coordinateur;
import diceForge.Joueur;

public class Mutateur {
    int nombreDeJoueurs;
    int matchEntreDeuxJoueursParTournoi;
    int nombreDePopulationMax;
    Afficheur afficheur = new Afficheur(false);

    Mutateur(int n, int m, int p){
        this.nombreDeJoueurs = n;
        this.matchEntreDeuxJoueursParTournoi = m;
        this.nombreDePopulationMax = p;
        initDemo();
    }

    private void initDemo(){
        for (int population = 1; population <= nombreDePopulationMax; population++){
            int[] winRateCumulesJoueur = new int[10];
            for (int i = 0; i < 10; i++)
                for (int j = 0; i < 10; j++)
                    if (i!=j){
                        break;
                    }

        }
    }
}

package bot.AubotV2;

import diceForge.Coordinateur;

public class MatchTournoi extends Coordinateur {
    private int nombreDePartieParMatch;
    private int nombreDeJoueur;

    MatchTournoi(String[] filePath){
        this.nombreDePartieParMatch = filePath.length;
        this.nombreDeJoueur = filePath.length;
        initMatch();
    }

    private void initMatch(){
        for (int partie = 0; partie < nombreDePartieParMatch; partie++){
            //PlateauTournoi plateau = new PlateauTournoi();
        }
    }
}

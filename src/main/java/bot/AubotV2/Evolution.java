package bot.AubotV2;

import diceForge.Joueur;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class Evolution {
    private Random random = new Random();
    private int nombreDeJoueurs;
    private int nombreDePartieParMatch;
    private int nombreDePopulationMax;
    private int uneChanceSurDeMuter;
    String[] stringFichierNouveauJoueurs = new String[5];
    private FileReader[][] fr = new FileReader[5][5];
    private BufferedReader[][] br = new BufferedReader[5][5];
    private BufferedWriter[] bw = new BufferedWriter[5];
    private int[] alphaId = new int[5];
    private int[] betaId = new int[5];
    private String[] alphaFile = new String[5];
    private String[] betaFile = new String[5];
    private String[] path2J = new String[]{"src/main/java/bot/AubotV2/1V1/Bot1", "src/main/java/bot/AubotV2/1V1/Bot2",  "src/main/java/bot/AubotV2/1V1/Bot3",  "src/main/java/bot/AubotV2/1V1/Bot4",  "src/main/java/bot/AubotV2/1V1/Bot5",  "src/main/java/bot/AubotV2/1V1/Bot6",  "src/main/java/bot/AubotV2/1V1/Bot7",  "src/main/java/bot/AubotV2/1V1/Bot8",  "src/main/java/bot/AubotV2/1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] path3J = new String[]{"src/main/java/bot/AubotV2/1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] path4J = new String[]{"src/main/java/bot/AubotV2/1V1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};

    Evolution(int n, int m, int p, int mu){
        this.nombreDeJoueurs = n;
        this.nombreDePartieParMatch = m;
        this.nombreDePopulationMax = p;
        this.uneChanceSurDeMuter = mu;
        initEvolution();
    }

    private void initEvolution(){
        for (int population = 1; population <= nombreDePopulationMax; population++){
            System.out.println("Population n°" + population +":");
            selection();
            croisement();
        }
    }

    private void selection(){
        Tournoi tournoi = new Tournoi(nombreDeJoueurs, nombreDePartieParMatch);
        int[] res = tournoi.getResultats();
        int[] resId = new int[]{0,1,2,3,4,5,6,7,8,9};
        if (nombreDeJoueurs == 2) {
            for (int i = 0; i < 10; i++) {
                System.out.println("Joueur n°" + (i + 1) + ": " + (float) res[i] / (9 * nombreDePartieParMatch) * 100 + "% de victoire");
            }
        }
        int minId;
        int stockScore;
        int stockId;
        for (int i = 0; i < 10; i++) {//tri par sélection en fonction du score du joueur, répliqué sur le tableau des id pour avoir les 5 joueurs les plus faibles en premier dans les deux tableaux (celui qui nous intéresse vraiment est celui des id..)
            minId = i;
            for (int j = i + 1; j < 10; j++) {
                if (res[j] < res[minId]) {
                    minId = j;
                }
            }
            if (minId != i){
                stockScore = res[i]; stockId = resId[i];
                res[i] = res[minId]; resId[i] = resId[minId];
                res[minId] = stockScore; resId[minId] = stockId;
            }
        }
        for (int i = 0; i < 5; i++) {
            betaId[i] = resId[i];
        }
        for (int i = 5; i < 10; i++) {
            this.alphaId[i-5] = resId[i];
        }
        Arrays.sort(alphaId);
        System.out.println("\n\nLes joueurs n°" + (alphaId[0] + 1) + ", " + (alphaId[1] + 1) + ", " + (alphaId[2] + 1) + ", " + (alphaId[3] + 1) + " et " + (alphaId[4] + 1) + " survivent\n\n-----------------------------------------------------------------------\n");
    }

    private void croisement() {
        alphaFile = new String[5];
        betaFile = new String[5];
        switch (nombreDeJoueurs) {//on récupère les fichiers des joueurs survivants et vaincus
            case 2:
                for (int i = 0; i < 5; i++) {
                    alphaFile[i] = path2J[alphaId[i]];
                    betaFile[i] = path2J[betaId[i]];
                }
                break;
            case 3:
                for (int i = 0; i < 5; i++) {
                    alphaFile[i] = path3J[alphaId[i]];
                    betaFile[i] = path3J[betaId[i]];
                }
                break;
            case 4:
                for (int i = 0; i < 5; i++) {
                    alphaFile[i] = path4J[alphaId[i]];
                    betaFile[i] = path4J[betaId[i]];
                }
                break;
        }
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {// on attribue à chaque fichier un lecteur
                try {
                    this.fr[j][i] = new FileReader(alphaFile[i]);
                } catch (IOException e) {
                    System.out.println("Le fichier n'a pas été trouvé");
                }
                this.br[j][i] = new BufferedReader(fr[j][i]);
            }
        }
        stringFichierNouveauJoueurs = new String[]{"", "", "", "", ""};
        String ligne = "";
        int compteLigne = 1;
        for (int i = 0; i < 5; i++) {
            compteLigne = 1;
            for (int j = 0; j < 20; j++) {
                int r = random.nextInt(5);
                try {
                    ligne = br[i][r].readLine();
                } catch (IOException e) {
                    System.out.println("Erreur lors de la lecture du fichier: " + e);
                }
                int mutation = random.nextInt(uneChanceSurDeMuter);
                if (mutation!=0)
                    stringFichierNouveauJoueurs[i] += ligne + "\n";
                else{
                    stringFichierNouveauJoueurs[i] += mutation(compteLigne) + "\n";
                }
                for (int k = 0; k < 5; k++)
                    if (k != r)
                        try {
                            br[i][k].readLine();
                        } catch (IOException e) {
                            System.out.println("Erreur lors de la lecture du fichier! " + e);
                        }
                compteLigne++;
            }
            for (int l = 0; l < 5; l++) {
                try {
                    br[i][l].close();
                    fr[i][l].close();
                } catch (IOException e) {
                    System.out.println("Erreur lors de la fermeture du fichier: " + e);
                }
            }
        }
        for (int i=0; i<5; i++){
            File f = new File(betaFile[i]);
            if (f.exists())
            {
                f.delete();
            }
            try {
                FileWriter out = new FileWriter(f);
                out.write(stringFichierNouveauJoueurs[i]);
                out.close();
            } catch (IOException e){
                System.out.println("Erreur: " + e);
            }
        }
    }

    private String mutation(int ligne){
        if (ligne ==1){
            return "" + (random.nextInt(6)+1) + (random.nextInt(6) + 2) + (random.nextInt(8)+2) + (random.nextInt(8)+2) + (random.nextInt(8)+2) + (random.nextInt(8)+2) + (random.nextInt(8)+2);
        }
        if (ligne == 2 || ligne == 3){
            return "" + (random.nextInt(2) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
        }
        if (ligne >=4 && ligne <= 9){
            String res = "";
            int[] coutBassins = new int[]{2,3,4,5,6,8};
            int[] unTableauParmiCinq = new int[3];
            for (int i=0; i<5; i++) {
                unTableauParmiCinq[0] = coutBassins[random.nextInt(6)];
                unTableauParmiCinq[1] = random.nextInt(2);
                int r = random.nextInt(2);
                switch (unTableauParmiCinq[0]){
                    case 2:
                        unTableauParmiCinq[2] = (r == 0) ? 0 : 2;
                        break;
                    case 3:
                        unTableauParmiCinq[2] = (r == 0) ? 0 : 1;
                        break;
                    case 4:
                        unTableauParmiCinq[2] = random.nextInt(3);
                        break;
                    case 5:
                        unTableauParmiCinq[2] = 0;
                        break;
                    case 6:
                        unTableauParmiCinq[2] = 2;
                        break;
                    case 8:
                        unTableauParmiCinq[2] = 1;
                        break;
                }
                res += "" + unTableauParmiCinq[0] + unTableauParmiCinq[1] + unTableauParmiCinq[2];
            }
            return res;
        }
        if (ligne >=10 && ligne <=19){
            String[] alphabet = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x"};
            return "" + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]) + (alphabet[random.nextInt(24)]);
        }
        if (ligne == 20){
            String res = "";
            for (int i = 0; i<16; i++)
                res += random.nextInt(3);
            return res;
        }
        return "| PROBLEME |";
    }

}

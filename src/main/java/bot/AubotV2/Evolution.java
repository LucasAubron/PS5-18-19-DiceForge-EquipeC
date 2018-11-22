package bot.AubotV2;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class Evolution {
    private Random random = new Random();
    int nombreDeJoueurs;
    int nombreDePartieParMatch;
    int nombreDePopulationMax;
    String[] stringFichierNouveauJoueurs = new String[5];
    private FileReader[][] fr = new FileReader[5][5];
    private BufferedReader[][] br = new BufferedReader[5][5];
    private BufferedWriter[] bw = new BufferedWriter[5];
    private int[] alphaId = new int[5];
    private int[] betaId = new int[5];
    private String[] alphaFile = new String[5];
    private String[] betaFile = new String[5];
    private String[] pathFiles1V1 = new String[]{"src/main/java/bot/AubotV2/1V1/Bot1", "src/main/java/bot/AubotV2/1V1/Bot2",  "src/main/java/bot/AubotV2/1V1/Bot3",  "src/main/java/bot/AubotV2/1V1/Bot4",  "src/main/java/bot/AubotV2/1V1/Bot5",  "src/main/java/bot/AubotV2/1V1/Bot6",  "src/main/java/bot/AubotV2/1V1/Bot7",  "src/main/java/bot/AubotV2/1V1/Bot8",  "src/main/java/bot/AubotV2/1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] pathFiles1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] pathFiles1V1V1V1 = new String[]{"src/main/java/bot/AubotV2/1V1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1V1/Bot2",  "src/main/java/bot/AubotV2/1V1V1V1/Bot3",  "src/main/java/bot/AubotV2/1V1V1V1/Bot4",  "src/main/java/bot/AubotV2/1V1V1V1/Bot5",  "src/main/java/bot/AubotV2/1V1V1V1/Bot6",  "src/main/java/bot/AubotV2/1V1V1V1/Bot7",  "src/main/java/bot/AubotV2/1V1V1V1/Bot8",  "src/main/java/bot/AubotV2/1V1V1V1/Bot9",   "src/main/java/bot/AubotV2/1V1/Bot10"};

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
        int[] resId = new int[]{0,1,2,3,4,5,6,7,8,9};
        if (nombreDeJoueurs == 2) {
            for (int i = 0; i < 10; i++) {
                System.out.println("Joueur n°" + (i + 1) + ": " + (float) res[i] / (9 * nombreDePartieParMatch) * 100 + "% de victoire");
            }
        }
        int min = -1;
        int stock = -1;
        for (int i = 0; i < 10; i++) {//tri par sélection en fonction du score du joueur, répliqué sur le tableau des id pour avoir les 5 joueurs les plus faibles en premier dans les deux tableaux (celui qui nous intéresse vraiment est celui des id..)
            min = i;
            for (int j = i + 1; j < 10; j++) {
                if (res[j] < res[i])
                    min = j;
            }
            if (min != i) {//pas pratique le java pour échanger deux valeurs d'un tableau ... en python c'est une ligne par échange soit trois foins moins qu'ici :D
                stock = res[i];
                res[i] = res[min];
                res[min] = stock;
                stock = resId[i];
                resId[i] = resId[min];
                resId[min] = stock;
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
                    alphaFile[i] = pathFiles1V1[alphaId[i]];
                    betaFile[i] = pathFiles1V1[betaId[i]];
                }
                break;
            case 3:
                for (int i = 0; i < 5; i++) {
                    alphaFile[i] = pathFiles1V1V1[alphaId[i]];
                    betaFile[i] = pathFiles1V1V1[betaId[i]];
                }
                break;
            case 4:
                for (int i = 0; i < 5; i++) {
                    alphaFile[i] = pathFiles1V1V1V1[alphaId[i]];
                    betaFile[i] = pathFiles1V1V1V1[betaId[i]];
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
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 20; j++) {
                int r = random.nextInt(5);
                try {
                    ligne = br[i][r].readLine();
                } catch (IOException e) {
                    System.out.println("Erreur lors de la lecture du fichier: " + e);
                }
                stringFichierNouveauJoueurs[i] += ligne + "\n";
                for (int k = 0; k < 5; k++)
                    if (k != r)
                        try {
                            br[i][k].readLine();
                        } catch (IOException e) {
                            System.out.println("Erreur lors de la lecture du fichier! " + e);
                        }
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

    private void mutation(){
    }

}

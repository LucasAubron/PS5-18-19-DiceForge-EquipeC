package bot.AubotV2.src;

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
    private int uneChanceSurDeMuterLigne;
    String[] stringFichierNouveauJoueurs = new String[5];
    private FileReader[][] fr = new FileReader[5][5];
    private BufferedReader[][] br = new BufferedReader[5][5];
    private BufferedWriter[] bw = new BufferedWriter[5];
    private int[] alphaId = new int[5];
    private int[] betaId = new int[5];
    private String[] alphaFile = new String[5];
    private String[] betaFile = new String[5];
    private String[] path2J = new String[]{"src/main/java/bot/AubotV2/1V1/Bot1", "src/main/java/bot/AubotV2/1V1/Bot2", "src/main/java/bot/AubotV2/1V1/Bot3", "src/main/java/bot/AubotV2/1V1/Bot4", "src/main/java/bot/AubotV2/1V1/Bot5", "src/main/java/bot/AubotV2/1V1/Bot6", "src/main/java/bot/AubotV2/1V1/Bot7", "src/main/java/bot/AubotV2/1V1/Bot8", "src/main/java/bot/AubotV2/1V1/Bot9", "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] path3J = new String[]{"src/main/java/bot/AubotV2/1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1/Bot2", "src/main/java/bot/AubotV2/1V1V1/Bot3", "src/main/java/bot/AubotV2/1V1V1/Bot4", "src/main/java/bot/AubotV2/1V1V1/Bot5", "src/main/java/bot/AubotV2/1V1V1/Bot6", "src/main/java/bot/AubotV2/1V1V1/Bot7", "src/main/java/bot/AubotV2/1V1V1/Bot8", "src/main/java/bot/AubotV2/1V1V1/Bot9", "src/main/java/bot/AubotV2/1V1/Bot10"};
    private String[] path4J = new String[]{"src/main/java/bot/AubotV2/1V1V1V1/Bot1", "src/main/java/bot/AubotV2/1V1V1V1/Bot2", "src/main/java/bot/AubotV2/1V1V1V1/Bot3", "src/main/java/bot/AubotV2/1V1V1V1/Bot4", "src/main/java/bot/AubotV2/1V1V1V1/Bot5", "src/main/java/bot/AubotV2/1V1V1V1/Bot6", "src/main/java/bot/AubotV2/1V1V1V1/Bot7", "src/main/java/bot/AubotV2/1V1V1V1/Bot8", "src/main/java/bot/AubotV2/1V1V1V1/Bot9", "src/main/java/bot/AubotV2/1V1/Bot10"};

    Evolution(int n, int m, int p, int mu, int mul) {
        this.nombreDeJoueurs = n;
        this.nombreDePartieParMatch = m;
        this.nombreDePopulationMax = p;
        this.uneChanceSurDeMuter = mu;
        this.uneChanceSurDeMuterLigne = mul;
        initEvolution();
    }

    private void initEvolution() {
        for (int population = 1; population <= nombreDePopulationMax; population++) {
            System.out.println("Population n°" + population + ":");
            selection();
            croisement();
        }
    }

    private void selection() {
        Tournoi tournoi = new Tournoi(nombreDeJoueurs, nombreDePartieParMatch);
        int[] res = tournoi.getResultats();
        int[] point = tournoi.getPoints();
        int[] resId = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        switch (nombreDeJoueurs){
            case 2:
                for (int i = 0; i < 10; i++) {
                    System.out.println("Joueur n°" + (i + 1) + ": " +  ((res[i] * 100)/9)/(float)(nombreDePartieParMatch)  + "% de victoire avec en moyenne " + point[i] / (9 * nombreDePartieParMatch) + " points de gloire");
                }
            case 3:
                break;
            case 4:
                break;
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
            if (minId != i) {
                stockScore = res[i];
                stockId = resId[i];
                res[i] = res[minId];
                resId[i] = resId[minId];
                res[minId] = stockScore;
                resId[minId] = stockId;
            }
        }
        for (int i = 0; i < 5; i++) {
            betaId[i] = resId[i];
        }
        for (int i = 5; i < 10; i++) {
            this.alphaId[i - 5] = resId[i];
        }
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
                if (mutation != 0)
                    stringFichierNouveauJoueurs[i] += ligne + "\n";
                else {
                    stringFichierNouveauJoueurs[i] += (random.nextInt(uneChanceSurDeMuterLigne) == 0) ? mutationLigne(ligne, compteLigne) + "\n" : mutationChar(ligne, compteLigne) + "\n"; //deux types de mutation, une grossière qui créé de gros changements, et une moins impactante
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
        for (int i = 0; i < 5; i++) {
            File f = new File(betaFile[i]);
            if (f.exists()) {
                f.delete();
            }
            try {
                FileWriter out = new FileWriter(f);
                out.write(stringFichierNouveauJoueurs[i]);
                out.close();
            } catch (IOException e) {
                System.out.println("Erreur: " + e);
            }
        }
    }

    private String mutationChar(String ligne, int compteLigne) {
        int longueur = ligne.length();
        char[] tabLigne = ligne.toCharArray();
        int randomPos = random.nextInt(longueur);
        switch (longueur) {
            case 7:
                if (randomPos == 0) {
                    int r = random.nextInt(6) + 1;
                    char a = (char) (r + 48);
                    tabLigne[randomPos] = a;
                } else {
                    int r = random.nextInt(8) + 2;
                    char a = (char) (r + 48);
                    tabLigne[randomPos] = a;
                }
                break;
            case 10: {
                int r = random.nextInt(3) + 1;
                char a = (char) (r + 48);
                tabLigne[randomPos] = a;
                break;
            }
            case 15: {
                int[] coutBassins = new int[]{2, 3, 4, 5, 6, 8};
                if (randomPos % 3 == 1) {
                    randomPos--;
                } else if (randomPos % 3 == 2) {
                    randomPos -= 2;
                }
                int r1 = coutBassins[random.nextInt(5)];
                int r2 = random.nextInt(2);
                int n;
                switch (r1) {
                    case 2:
                        n = (random.nextInt() == 0) ? 0 : 2;
                        break;
                    case 3:
                        n = (random.nextInt(2) == 0) ? 0 : 1;
                        break;
                    case 4:
                        n = random.nextInt(3);
                        break;
                    case 5:
                        n = 0;
                        break;
                    case 6:
                        n = 2;
                        break;
                    case 8:
                        n = 1;
                        break;
                    default:
                        n = 1000; //probleme
                }
                char a = (char) (r1 + 48);
                char b = (char) (r2 + 48);
                char c = (char) (n + 48);
                tabLigne[randomPos] = a;
                tabLigne[randomPos + 1] = b;
                tabLigne[randomPos + 2] = c;
                break;
            }
            case 18: {
                String alphabet = "abcdefghijklmnopqrstuvwx";
                char[] alphabetChar = alphabet.toCharArray();
                int r = random.nextInt(24);
                char a = alphabetChar[r];
                tabLigne[randomPos] = a;
                break;
            }
            case 16:
                int r = random.nextInt(nombreDeJoueurs+1);
                char a = (char) (r + 48);
                tabLigne[randomPos] = a;
        }
        String res = "";
        for (int i = 0; i < longueur; i++) {
            res += tabLigne[i];
        }
        return res;
    }

    private String mutationLigne(String ligne, int compteLigne) {
        if (compteLigne == 1) {
            return "" + (random.nextInt(6) + 1) + (random.nextInt(6) + 2) + (random.nextInt(8) + 2) + (random.nextInt(8) + 2) + (random.nextInt(8) + 2) + (random.nextInt(8) + 2) + (random.nextInt(8) + 2);
        }
        if (compteLigne == 2 || compteLigne == 3) {
            return "" + "1" + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + (random.nextInt(3) + 1) + "0";
        }
        if (compteLigne >= 4 && compteLigne <= 9) {
            String res = "";
            int[] coutBassins = new int[]{2, 3, 4, 5, 6, 8};
            int[] unTableauParmiDix = new int[3];
            for (int i = 0; i < 10; i++) {
                unTableauParmiDix[0] = coutBassins[random.nextInt(6)];
                unTableauParmiDix[1] = random.nextInt(2);
                int r = random.nextInt(2);
                switch (unTableauParmiDix[0]) {
                    case 2:
                        unTableauParmiDix[2] = (r == 0) ? 0 : 2;
                        break;
                    case 3:
                        unTableauParmiDix[2] = (r == 0) ? 0 : 1;
                        break;
                    case 4:
                        unTableauParmiDix[2] = random.nextInt(3);
                        break;
                    case 5:
                        unTableauParmiDix[2] = 0;
                        break;
                    case 6:
                        unTableauParmiDix[2] = 2;
                        break;
                    case 8:
                        unTableauParmiDix[2] = 1;
                        break;
                }
                res += "" + unTableauParmiDix[0] + unTableauParmiDix[1] + unTableauParmiDix[2];
            }
            return res;
        }
        if (compteLigne >= 10 && compteLigne <= 19) {
            String[] alphabet = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x"};
            String res = "";
            for (int i = 0; i<18; i++)
                res += (alphabet[random.nextInt(24)]);
            return res;
        }
        if (compteLigne == 20) {
            String res = "";
            for (int i = 0; i < 16; i++)
                res += random.nextInt(nombreDeJoueurs+1);
            return res;
        }
        return "| PROBLEME |";
    }

}

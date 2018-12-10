package bot.AubotV2.src;



import java.io.*;
import java.util.*;

/**
 * Ma première expérience avec les buffer en java, c'est pour cela que ce n'est pas très compréhensible, voire pas du tout
 * Pour résumer: les 5 meilleurs bot du tournoi précedent (ceux qui ont le meileur winrate ...)
 * survivent, les 5 pires sont éliminés et remplacés.
 * Pour recréer 5 nouveaux joueurs, on va faire croiser les joueurs survivants.
 * Pour cela, on réécrit ligne à ligne les nouveaux joueurs, pour chaque ligne, on recopie une ligne
 * d'un joueur survivant pris au hasard.
 * S'ajoute à cela la mutation, certaines fois une ligne ne sera pas recopiée d'un joueur
 * mais sera réécrite de façon aléatoire. Il existe les mutation totales comme expliqué
 * précédemment, et les mutations minimes ou la on ne changera qu'un ou deux caractère
 * d'une ligne prise d'un joueur survivant.
 */

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
    private String[] path2J = new String[]{"src/main/java/bot/AubotV2/Bot1/1V1", "src/main/java/bot/AubotV2/Bot2/1V1", "src/main/java/bot/AubotV2/Bot3/1V1", "src/main/java/bot/AubotV2/Bot4/1V1", "src/main/java/bot/AubotV2/Bot5/1V1", "src/main/java/bot/AubotV2/Bot6/1V1", "src/main/java/bot/AubotV2/Bot7/1V1", "src/main/java/bot/AubotV2/Bot8/1V1", "src/main/java/bot/AubotV2/Bot9/1V1", "src/main/java/bot/AubotV2/Bot10/1V1"};
    private String[] path3J = new String[]{"src/main/java/bot/AubotV2/Bot1/1V1V1", "src/main/java/bot/AubotV2/Bot2/1V1V1", "src/main/java/bot/AubotV2/Bot3/1V1V1", "src/main/java/bot/AubotV2/Bot4/1V1V1", "src/main/java/bot/AubotV2/Bot5/1V1V1", "src/main/java/bot/AubotV2/Bot6/1V1V1", "src/main/java/bot/AubotV2/Bot7/1V1V1", "src/main/java/bot/AubotV2/Bot8/1V1V1", "src/main/java/bot/AubotV2/Bot9/1V1V1", "src/main/java/bot/AubotV2/Bot10/1V1V1"};
    private String[] path4J = new String[]{"src/main/java/bot/AubotV2/Bot1/1V1V1V1", "src/main/java/bot/AubotV2/Bot2/1V1V1V1", "src/main/java/bot/AubotV2/Bot3/1V1V1V1", "src/main/java/bot/AubotV2/Bot4/1V1V1V1", "src/main/java/bot/AubotV2/Bot5/1V1V1V1", "src/main/java/bot/AubotV2/Bot6/1V1V1V1", "src/main/java/bot/AubotV2/Bot7/1V1V1V1", "src/main/java/bot/AubotV2/Bot8/1V1V1V1", "src/main/java/bot/AubotV2/Bot9/1V1V1V1", "src/main/java/bot/AubotV2/Bot10/1V1V1V1"};

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
            for (int j = 0; j < 8; j++) {
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
                    stringFichierNouveauJoueurs[i] += (random.nextInt(uneChanceSurDeMuterLigne) == 0) ? mutationLigne(ligne, compteLigne) + "\n" : mutationMinime(ligne, compteLigne) + "\n"; //deux types de mutation, une grossière qui créé de gros changements, et une moins impactante
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

    private String mutationMinime(String ligne, int compteLigne) {
        int longueur = ligne.length();
        char[] tabLigne = ligne.toCharArray();
        int randomPos = random.nextInt(longueur); //Pour choisir le seul élément qui va être modifié quand il n'y a qu'un seul élement à modifier ...
        int randomPos2 = random.nextInt(longueur);//pour les permutations (a utiliser avec randomPos)
        if (compteLigne == 1)
                if (randomPos == 0) {
                    int r = random.nextInt(6) + 1;
                    char a = (char) (r + 48);
                    tabLigne[randomPos] = a;
                } else {
                    int r = random.nextInt(8) + 2;
                    char a = (char) (r + 48);
                    tabLigne[randomPos] = a;
                }
        else if (compteLigne == 2 || compteLigne == 3){
                int r = random.nextInt(6) + 1;
                char a = (char) (r + 48);
                tabLigne[randomPos] = a;
        }
        else if (compteLigne >= 4 && compteLigne <= 6){
            char save = tabLigne[randomPos2];
            tabLigne[randomPos2] = tabLigne[randomPos];
            tabLigne[randomPos] = save;
        }
        else if (compteLigne == 7){
            char save = tabLigne[randomPos2];
            tabLigne[randomPos2] = tabLigne[randomPos];
            tabLigne[randomPos] = save;
        }
        else if (compteLigne == 8){
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
            String res = "";
            res += random.nextInt(6) + 1;
            for (int i = 0; i<6; i++)
                res+= random.nextInt(8) + 2;
            return res;
        }
        if (compteLigne == 2 || compteLigne == 3) {
            String res = "";
            for (int i = 0; i<6; i++)
                res += random.nextInt(6) + 1;
            return res;
        }
        if (compteLigne >= 4 && compteLigne <= 6) {
            String res = "";
            List<Integer> resListe = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
            Collections.shuffle(resListe);
            for (int n: resListe)
                res += n;
            return res;
        }
        if (compteLigne == 7) {
            //String resTab = "abcdefghijklmnopqrstuvwx";
            //resTab.toCharArray();
            String res = "";
            List resListe = new ArrayList(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x'));
            Collections.shuffle(resListe);
            for (Object a: resListe)
                res+=a;
            return res;
        }
        if (compteLigne == 8) {
            String res = "";
            for (int i = 0; i < 24; i++)
                res += random.nextInt(nombreDeJoueurs+1);
            return res;
        }
        return "| PROBLEME |";
    }

}

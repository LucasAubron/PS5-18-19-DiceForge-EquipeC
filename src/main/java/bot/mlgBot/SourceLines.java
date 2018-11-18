package bot.mlgBot;

import diceForge.DiceForgeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class SourceLines {
    private List<Byte> lignes;
    private byte[] choixAction;
    private byte[] choixSecondeAction;
    private List<List<List<Byte>>> ordreBassin = new ArrayList<>();//Manche(Or(numBassin))
    private List<List<List<List<Byte>>>> ordreCarte = new ArrayList<>();//Manche(Soleil/Lune(Quantite(Cartes)))

    public SourceLines(List<StatLine> statLines){
        lignes = combinerStatLines(statLines);
    }

    public SourceLines(List<StatLine> statLines, String nomFichier, int nbrJoueur) {
        this(nomFichier, nbrJoueur);
        lignes = combinerStatLines(statLines);
    }

    public SourceLines(String nomFichier, int nbrJoueur){
        try{
            RandomAccessFile file = new RandomAccessFile(nomFichier, "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int)file.length());
            int x = channel.read(buf);
            if (x != file.length())
                throw new DiceForgeException("SourceLine", "Le buffeur n'a pas lu tout le fichier");
            lignes = new ArrayList<>();
            for (int i = 0; i != x; ++i)
                lignes.add(buf.get(i));
            file.close();

        } catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        Random notLuckButSkill = new Random();
        choixAction = new byte[nbrJoueur == 3 ? 10 : 9];
        choixSecondeAction = new byte[nbrJoueur == 3 ? 10 : 9];
        ordreBassin = new ArrayList<>();
        ordreCarte = new ArrayList<>();
        ordreBassin.add(new ArrayList<>());
        ordreCarte.add(new ArrayList<>());
        for (int i = 0; i != 2; ++i)
            ordreCarte.get(0).add(new ArrayList<>());
        int j = 0, partie = 1;
        int soleil = 0;
        for (int i = 0; i != lignes.size(); ++i) {
            if (lignes.get(i) == ";".getBytes()[0]) {
                j = 0;
                ++partie;
                ++i;
            }
            int pourcentRandom = 30;
            switch (partie) {
                case 1:
                    choixAction[j] = (notLuckButSkill.nextInt(pourcentRandom) == 0 ? (byte) (notLuckButSkill.nextInt(2) + 1) : lignes.get(i));
                    if (choixAction[j] == 0)
                        throw new DiceForgeException("SourceLigne", "Choixaction pas bon");
                    break;
                case 2:
                    choixSecondeAction[j] = (notLuckButSkill.nextInt(pourcentRandom) == 0 ? (byte) (notLuckButSkill.nextInt(2) + 1) : lignes.get(i));
                    break;
                case 3:
                    if (lignes.get(i + 1) == ":".getBytes()[0])
                        ordreBassin.get(ordreBassin.size() - 1).add(new ArrayList<>());
                    else if (lignes.get(i) == ",".getBytes()[0])
                        ordreBassin.add(new ArrayList<>());
                    else if (lignes.get(i) != ":".getBytes()[0] && notLuckButSkill.nextInt(pourcentRandom) != 0)
                        ordreBassin.get(ordreBassin.size() - 1).get(ordreBassin.get(ordreBassin.size() - 1).size() - 1).add(lignes.get(i));
                    break;
                case 4:
                    if (lignes.get(i) == "?".getBytes()[0])
                        soleil = 1;
                    else if (i + 1 < lignes.size() && lignes.get(i + 1) == ":".getBytes()[0])
                        ordreCarte.get(ordreCarte.size() - 1).get(soleil).add(new ArrayList<>());
                    else if (lignes.get(i) == ",".getBytes()[0]) {
                        ordreCarte.add(new ArrayList<>());
                        for (int k = 0; k != 2; ++k)
                            ordreCarte.get(ordreCarte.size() - 1).add(new ArrayList<>());
                        soleil = 0;
                    } else if (lignes.get(i) != ":".getBytes()[0] && notLuckButSkill.nextInt(pourcentRandom) != 0)
                        ordreCarte.get(ordreCarte.size() - 1).get(soleil).get(ordreCarte.get(ordreCarte.size() - 1).get(soleil).size() - 1).add(lignes.get(i));
                    break;

            }
            ++j;
        }
    }

    private List<Byte> combinerStatLines(List<StatLine> statLines){
        List<Byte> ligne = new ArrayList<>();
        for(int i = 0; i != statLines.get(0).getChoixAction().length*2+1; ++i){
            if (i != statLines.get(0).getChoixAction().length){
                float somme = 0;
                for (int j = 0; j != statLines.size(); ++j) {
                    if (i < statLines.get(0).getChoixAction().length)
                        somme += statLines.get(j).getChoixAction()[i];
                    else {
                        somme += statLines.get(j).getChoixSecondeAction()[i - statLines.get(0).getChoixAction().length - 1];
                        if (statLines.get(j).getChoixSecondeAction()[i - statLines.get(0).getChoixAction().length - 1] == 0)
                            somme += 1.5;
                    }
                }
                ligne.add((byte) Math.round(somme / (float) statLines.size()));
                if (ligne.get(ligne.size() - 1) < 0 || ligne.get(ligne.size() - 1) > 3)
                    throw new DiceForgeException("SourceLines", "Erreur lors de la création des actions. Min: 0, max: 3, actuel: " + ligne.get(ligne.size() - 1));
            } else
                ligne.add(";".getBytes()[0]);
        }
        ligne.add(";".getBytes()[0]);//---------------------------------------BASSIN------------------------------------
        List<List<List<Byte>>> bassins = new ArrayList<>();//Manche(Or(numBassin))
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            bassins.add(new ArrayList<>());
        int approxOr = 1;
        for (StatLine statLine:statLines){
            for (int j = 0; j != statLine.getChoixBassin().length; ++j){
                while(statLine.getChoixBassin()[j][2]/approxOr >= bassins.get(statLine.getChoixBassin()[j][1]).size())
                    bassins.get(statLine.getChoixBassin()[j][1]).add(new ArrayList<>());
                bassins.get(statLine.getChoixBassin()[j][1]).get(statLine.getChoixBassin()[j][2]/approxOr).add(statLine.getChoixBassin()[j][0]);
            }
        }
        for(int i = 0; i != bassins.size(); ++i){
            if (i != 0)
                ligne.add(",".getBytes()[0]);
            int plusGrand = bassins.get(i).size();
            if (ordreBassin.size() > 0 && ordreBassin.get(i).size() > plusGrand)
                plusGrand = ordreBassin.get(i).size();
            for (int j = 0; j != plusGrand; ++j){
                ligne.add((byte)(j*approxOr));
                ligne.add(":".getBytes()[0]);
                Set<Byte> antiDoublon = new HashSet<>();
                if (bassins.get(i).size() > j)
                    antiDoublon.addAll(bassins.get(i).get(j));
                if (ordreBassin.size() > 0 && ordreBassin.get(i).size() > j)
                    antiDoublon.addAll(ordreBassin.get(i).get(j));
                ligne.addAll(antiDoublon);
            }
        }
        ligne.add(";".getBytes()[0]);//--------------------------------------CARTE--------------------------------------
        List<List<List<List<Byte>>>> cartes = new ArrayList<>();//or/lune(manche(cout(carte)))
        int approxRessource = 1;
        for (int i = 0; i != 2; ++i) {
            cartes.add(new ArrayList<>());
            for (int j = 0; j != statLines.get(0).getChoixAction().length; ++j)
                cartes.get(i).add(new ArrayList<>());
        }
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i) {
            for (StatLine statLine : statLines) {
                if (statLine.getChoixCarte()[i][0] != 0){
                    int numRessource = (statLine.getChoixCarte()[i][0] <= 13 ? 0 : 1);
                    while (statLine.getChoixCarte()[i][numRessource+1]/approxRessource >= cartes.get(numRessource).get(i).size())
                        cartes.get(numRessource).get(i).add(new ArrayList<>());
                    cartes.get(numRessource).get(i).get(statLine.getChoixCarte()[i][numRessource+1]/approxRessource).add(statLine.getChoixCarte()[i][0]);
                }
            }
        }
        for (int k = 0; k != statLines.get(0).getChoixAction().length; ++k) {
            if (k != 0)
                ligne.add(",".getBytes()[0]);
            for (int i = 0; i != 2; ++i) {
                if (i != 0)
                    ligne.add("?".getBytes()[0]);
                int plusGrand = cartes.get(i).get(k).size();
                if (ordreCarte.size() > 0 && ordreCarte.get(k).get(i).size() > plusGrand)
                    plusGrand = ordreCarte.get(k).get(i).size();
                for (int j = 0; j != plusGrand; ++j) {
                    ligne.add((byte) (j * approxRessource));
                    ligne.add(":".getBytes()[0]);
                    Set<Byte> antiDoublon = new HashSet<>();
                    if (cartes.get(i).get(k).size() > j)
                        antiDoublon.addAll(cartes.get(i).get(k).get(j));
                    if (ordreCarte.size() > 0 && ordreCarte.get(k).get(i).size() > j)
                        antiDoublon.addAll(ordreCarte.get(k).get(i).get(j));
                    ligne.addAll(antiDoublon);
                }
            }
        }
        return ligne;
    }

    public List<Byte> getLigne() {
        return lignes;
    }

    public byte[] getChoixAction(){
        return choixAction;
    }

    public byte[] getChoixSecondeAction(){
        return choixSecondeAction;
    }

    public List<List<List<Byte>>> getOrdreBassin(){
        return ordreBassin;
    }

    public List<List<List<List<Byte>>>> getOrdreCarte(){
        return ordreCarte;
    }
}

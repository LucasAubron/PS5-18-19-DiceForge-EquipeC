package bot.mlgBot;

import diceForge.DiceForgeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceLines {
    private List<Byte> lignes;

    public SourceLines(List<StatLine> statLines){
        lignes = combinerStatLines(statLines);
    }

    public SourceLines(String nomFichier){
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
        List<List<List<Byte>>> bassinSelonOrEtManche = new ArrayList<>();//Manche(Or(numBassin))
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            bassinSelonOrEtManche.add(new ArrayList<>());
        int approxOr = 3;
        for (StatLine statLine:statLines){
            for (int j = 0; j != statLine.getChoixBassin().length; ++j){
                while(statLine.getChoixBassin()[j][2]/approxOr >= bassinSelonOrEtManche.get(statLine.getChoixBassin()[j][1]).size())
                    bassinSelonOrEtManche.get(statLine.getChoixBassin()[j][1]).add(new ArrayList<>());
                bassinSelonOrEtManche.get(statLine.getChoixBassin()[j][1]).get(statLine.getChoixBassin()[j][2]/approxOr).add(statLine.getChoixBassin()[j][0]);
            }
        }
        for(int i = 0; i != bassinSelonOrEtManche.size(); ++i){
            if (i != 0)
                ligne.add(",".getBytes()[0]);
            for (int j = 0; j != bassinSelonOrEtManche.get(i).size(); ++j){
                ligne.add((byte)(j*approxOr));
                ligne.add(":".getBytes()[0]);
                Set<Byte> antiDoublon = new HashSet<>();
                antiDoublon.addAll(bassinSelonOrEtManche.get(i).get(j));
                ligne.addAll(antiDoublon);
            }
        }
        ligne.add(";".getBytes()[0]);//--------------------------------------CARTE--------------------------------------
        int approxRessource = 2;
        List<List<List<List<Byte>>>> cartes = new ArrayList<>();//2 listes or/lune(manche(cout(carte)))
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
                for (int j = 0; j != cartes.get(i).get(k).size(); ++j) {
                    ligne.add((byte) (j * approxRessource));
                    ligne.add(":".getBytes()[0]);
                    Set<Byte> antiDoublon = new HashSet<>();
                    antiDoublon.addAll(cartes.get(i).get(k).get(j));
                    ligne.addAll(antiDoublon);
                }
            }
        }
        return ligne;
    }

    public List<Byte> getLigne() {
        return lignes;
    }
}

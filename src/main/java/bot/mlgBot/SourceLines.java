package bot.mlgBot;

import diceForge.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

public class SourceLines {
    private List<List<Byte>> lignes;
    public SourceLines(List<StatLine> statLines){
        List<List<StatLine>> statLinesTrie = new ArrayList<>();
        /*

        */
    }

    private List<Byte> combinerStatLines(List<StatLine> statLines){
        List<Byte> ligne = new ArrayList<>();
        for(int i = 0; i != statLines.get(0).getChoixAction().length*2+1; ++i){
            if (i != statLines.get(0).getChoixAction().length){
                int somme = 0;
                for (int j = 0; j != statLines.size(); ++j) {
                    if (i < statLines.get(0).getChoixAction().length)
                        somme += statLines.get(j).getChoixAction()[i];
                    else
                        somme += statLines.get(j).getChoixSecondeAction()[i];
                }
                ligne.add((byte) Math.round(somme / (float) statLines.size()));
                if (ligne.get(ligne.size() - 1) < 0 || ligne.get(ligne.size() - 1) > 3)
                    throw new DiceForgeException("SourceLines", "Erreur lors de la création des actions. Min: 0, max: 3, actuel: " + ligne.get(ligne.size() - 1));
            } else
                ligne.add(";".getBytes()[0]);
        }
        ligne.add(";".getBytes()[0]);
        List<List<List<Integer>>> bassinSelonOrEtManche = new ArrayList<>();
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            bassinSelonOrEtManche.add(new ArrayList<>());
        for (int i = 0; i != statLines.size(); ++i){
            for (int j = 0; j != statLines.get(i).getChoixBassin().length; ++j){
                while(statLines.get(i).getChoixBassin()[j][2] <= bassinSelonOrEtManche.get(statLines.get(i).getChoixBassin()[j][1]).size()/3)
                    bassinSelonOrEtManche.get(statLines.get(i).getChoixBassin()[j][1]).add(new ArrayList<>());
            }
        }
        return ligne;
    }

    public byte[][] getLigne() {
        byte[][] bytes = new byte[lignes.size()][lignes.get(0).size()];
        for (int i = 0; i != lignes.size(); ++i)
            for (int j = 0;  j != lignes.get(0).size(); ++j)
                bytes[i][j] = lignes.get(i).get(j);
        return bytes;
    }
}

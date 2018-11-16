package bot.mlgBot;

import diceForge.DiceForgeException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceLines {
    private List<List<Byte>> lignes;

    public SourceLines(List<StatLine> statLines){
        lignes = new ArrayList<>();
        lignes.add(combinerStatLines(statLines));
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
        for (int i = 0; i != statLines.size(); ++i){
            for (int j = 0; j != statLines.get(i).getChoixBassin().length; ++j){
                while(statLines.get(i).getChoixBassin()[j][2]/approxOr >= bassinSelonOrEtManche.get(statLines.get(i).getChoixBassin()[j][1]).size())
                    bassinSelonOrEtManche.get(statLines.get(i).getChoixBassin()[j][1]).add(new ArrayList<>());
                bassinSelonOrEtManche.get(statLines.get(i).getChoixBassin()[j][1]).get(statLines.get(i).getChoixBassin()[j][2]/approxOr).add(statLines.get(i).getChoixBassin()[j][0]);
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
        List<List<List<List<Byte>>>> carte = new ArrayList<>();//Manche(Soleil/2(Lune/2(numBassin))
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            carte.add(new ArrayList<>());
        int approxRessource = 2;

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

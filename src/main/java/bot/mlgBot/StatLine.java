package bot.mlgBot;

import diceForge.DiceForgeException;

import java.io.DataInput;
import java.util.List;

public class StatLine {
    private byte[] choixAction;
    private byte[] choixSecondeAction;
    private byte[] choixBassin;
    private byte[] choixCarte;

    public StatLine(List<Byte> choixAction, List<Byte> choixSecondeAction, List<Byte> choixBassin, List<Byte> choixCarte) {
        if (choixAction.size() < 9 || choixAction.size() > 10)
            throw new DiceForgeException("StatLine.java","Le nombre de choix d'action est invalide. Min : 9, max : 10, actuel : "+choixAction.size());
        if (choixSecondeAction.size() < 9 || choixSecondeAction.size() > 10)
            throw new DiceForgeException("StatLine.java","Le nombre de choix de seconde action est invalide. Min : 9, max : 10, actuel : "+choixAction.size());
        this.choixAction = new byte[choixAction.size()];
        for (int i = 0; i != choixAction.size(); ++i)
            this.choixAction[i] = choixAction.get(i);
        this.choixSecondeAction = new byte[choixSecondeAction.size()];
        for (int i = 0; i != choixSecondeAction.size(); ++i)
            this.choixSecondeAction[i] = choixSecondeAction.get(i);
        this.choixBassin = new byte[choixBassin.size()];
        for (int i = 0; i != choixBassin.size(); ++i)
            this.choixBassin[i] = choixBassin.get(i);
        this.choixCarte = new byte[choixCarte.size()];
        for (int i = 0; i != choixCarte.size(); ++i)
            this.choixCarte[i] = choixCarte.get(i);
    }

    public byte[] getChoixAction(){
        return choixAction;
    }

    public byte[] getChoixSecondeAction(){
        return choixSecondeAction;
    }

    public byte[][] getChoixBassin(){
        byte[][] newChoixBassin = new byte[choixBassin.length/3][3];
        for (int i = 0; i != newChoixBassin.length; ++i)
            for (int j = 0; j != 3; ++j)
                newChoixBassin[i][j] = choixBassin[i*3+j];
        return newChoixBassin;
    }

    public byte[] getChoixCarte(){
        return choixCarte;
    }
}

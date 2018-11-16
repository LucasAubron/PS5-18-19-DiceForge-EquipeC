package bot.mlgBot;

import diceForge.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

public class StatLine {
    private byte[] choixAction;
    private byte[] choixSecondeAction;
    private byte[] choixBassin;
    private byte[] choixCarte;

    public StatLine(byte[] bytes){
        List<Byte> actionLigne = new ArrayList<>();
        List<Byte> secActionLigne = new ArrayList<>();
        List<Byte> bassinLigne = new ArrayList<>();
        List<Byte> carteLigne = new ArrayList<>();
        int curseur = 0;
        for (; bytes[curseur] != ";".getBytes()[0]; ++curseur)
            actionLigne.add(bytes[curseur]);
        for (++curseur; bytes[curseur] != ";".getBytes()[0]; ++curseur)
            secActionLigne.add(bytes[curseur]);
        for (++curseur; bytes[curseur] != ";".getBytes()[0]; curseur += 3){
            bassinLigne.add(bytes[curseur]);
            bassinLigne.add(bytes[curseur+1]);
            bassinLigne.add(bytes[curseur+2]);
        }
        for (++curseur; bytes[curseur] != ";".getBytes()[0]; ++curseur)
            carteLigne.add(bytes[curseur]);

        if (actionLigne.size() < 9 || actionLigne.size() > 10)
            throw new DiceForgeException("StatLine.java","Le nombre de choix d'action est invalide. Min : 9, max : 10, actuel : "+actionLigne.size());
        if (secActionLigne.size() < 9 || secActionLigne.size() > 10)
            throw new DiceForgeException("StatLine.java","Le nombre de choix de seconde action est invalide. Min : 9, max : 10, actuel : "+secActionLigne.size());
        this.choixAction = new byte[actionLigne.size()];
        for (int i = 0; i != actionLigne.size(); ++i)
            this.choixAction[i] = actionLigne.get(i);
        this.choixSecondeAction = new byte[secActionLigne.size()];
        for (int i = 0; i != secActionLigne.size(); ++i)
            this.choixSecondeAction[i] = secActionLigne.get(i);
        this.choixBassin = new byte[bassinLigne.size()];
        for (int i = 0; i != bassinLigne.size(); ++i)
            this.choixBassin[i] = bassinLigne.get(i);
        this.choixCarte = new byte[carteLigne.size()];
        for (int i = 0; i != carteLigne.size(); ++i)
            this.choixCarte[i] = carteLigne.get(i);
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

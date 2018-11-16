package bot.mlgBot;

import diceForge.Carte;
import diceForge.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

public class StatLine {
    private byte[] choixAction;
    private byte[] choixSecondeAction;
    private byte[][] choixBassin;
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
        for (++curseur; bytes[curseur] != ";".getBytes()[0]; ++curseur)
            bassinLigne.add(bytes[curseur]);
        for (++curseur; bytes[curseur] != ";".getBytes()[0]; ++curseur)
            carteLigne.add(bytes[curseur]);

        if (actionLigne.size() < 9 || actionLigne.size() > 10)
            throw new DiceForgeException("StatLine","Le nombre de choix d'action est invalide. Min : 9, max : 10, actuel : "+actionLigne.size());
        if (secActionLigne.size() < 9 || secActionLigne.size() > 10)
            throw new DiceForgeException("StatLine","Le nombre de choix de seconde action est invalide. Min : 9, max : 10, actuel : "+secActionLigne.size());
        this.choixAction = new byte[actionLigne.size()];
        for (int i = 0; i != actionLigne.size(); ++i) {
            this.choixAction[i] = actionLigne.get(i);
            if (this.choixAction[i] < 0 || this.choixAction[i] > 3)
                throw new DiceForgeException("StatLine","Le numéro d'action est invalide. Min: 0, max: 3, actuel: "+this.choixAction[i]+", pos: "+i);
        }
        this.choixSecondeAction = new byte[secActionLigne.size()];
        for (int i = 0; i != secActionLigne.size(); ++i) {
            this.choixSecondeAction[i] = secActionLigne.get(i);
            if (this.choixSecondeAction[i] < 0 || this.choixSecondeAction[i] > 3)
                throw new DiceForgeException("StatLine", "Le numéro de seconde action est invalide. Min: 0, max: 3, actuel: " + this.choixSecondeAction[i]);
        }
        this.choixBassin = new byte[bassinLigne.size()/3][3];
        for (int i = 0; i != choixBassin.length; ++i)
            for (int j = 0; j != 3; ++j)
                choixBassin[i][j] = bassinLigne.get(i*3+j);
        this.choixCarte = new byte[carteLigne.size()];
        for (int i = 0; i != carteLigne.size(); ++i) {
            this.choixCarte[i] = carteLigne.get(i);
            if ((choixCarte[i] < 0 || choixCarte[i] > Carte.Noms.values().length) && choixCarte[i] != ",".getBytes()[0])
                throw new DiceForgeException("StatLine", "Le numéro de la carte est invalide. Min: 0, max: "+Carte.Noms.values().length+", actuel: " + choixCarte[i]);
        }
    }

    public byte[] getChoixAction(){
        return choixAction;
    }

    public byte[] getChoixSecondeAction(){
        return choixSecondeAction;
    }

    public byte[][] getChoixBassin(){
        return choixBassin;
    }

    public byte[] getChoixCarte(){
        return choixCarte;
    }
}

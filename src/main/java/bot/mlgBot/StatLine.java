package bot.mlgBot;

import diceForge.Carte;
import diceForge.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

public class StatLine {
    private byte[] choixAction;
    private byte[] choixSecondeAction;
    private byte[][] choixBassin;
    private byte[][] choixCarte;
    private int pdg = 0;

    public int getPdg(){
        return pdg;
    }

    public StatLine(byte[] bytes, int pdg){
        this.pdg = pdg;
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
        choixAction = new byte[actionLigne.size()];
        for (int i = 0; i != actionLigne.size(); ++i) {
            choixAction[i] = actionLigne.get(i);
            if (choixAction[i] < 0 || choixAction[i] > 3)
                throw new DiceForgeException("StatLine","Le numéro d'action est invalide. Min: 0, max: 3, actuel: "+choixAction[i]+", pos: "+i);
        }
        choixSecondeAction = new byte[secActionLigne.size()];
        for (int i = 0; i != secActionLigne.size(); ++i) {
            choixSecondeAction[i] = secActionLigne.get(i);
            if (choixSecondeAction[i] < 0 || choixSecondeAction[i] > 3)
                throw new DiceForgeException("StatLine", "Le numéro de seconde action est invalide. Min: 0, max: 3, actuel: " + choixSecondeAction[i]);
        }
        choixBassin = new byte[bassinLigne.size()/3][3];
        for (int i = 0; i != choixBassin.length; ++i)
            for (int j = 0; j != 3; ++j)
                choixBassin[i][j] = bassinLigne.get(i*3+j);
        choixCarte = new byte[choixAction.length][3];
        int pos = 0, j = 0;
        for (int i = 0; i != carteLigne.size(); ++i) {
            if (carteLigne.get(i) == ",".getBytes()[0]) {
                ++pos;
                j = 0;
            }
            else{
                choixCarte[pos][j] = carteLigne.get(i);
                if ((choixCarte[pos][j] < 0 || choixCarte[pos][j] > Carte.Noms.values().length) && choixCarte[pos][j] != ",".getBytes()[0])
                    throw new DiceForgeException("StatLine", "Le numéro de la carte est invalide. Min: 0, max: "+Carte.Noms.values().length+", actuel: " + choixCarte[pos][j]);
                ++j;
            }
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

    public byte[][] getChoixCarte(){
        return choixCarte;
    }
}

package bot.mlgBot;

import diceForge.DiceForgeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class SourceLines {
    Random random = new Random();
    private List<Byte> lignes;
    private List<List<List<Byte>>> choixAction = new ArrayList<>();//Manche(Or(Action))
    private List<List<List<Byte>>> ordreBassin = new ArrayList<>();//Manche(Or(numBassin))
    private List<List<List<List<Byte>>>> ordreCarte = new ArrayList<>();//Manche(Soleil/Lune(Quantite(Cartes)))

    private int pourcentRandom = 30;

    private boolean desactiverMutation = false;

    public SourceLines(List<StatLine> statLines){
        lignes = combinerStatLines(statLines);
    }

    public SourceLines(List<StatLine> statLines, String nomFichier, int nbrJoueur) {
        this(nomFichier, nbrJoueur, true);
        lignes = combinerStatLines(statLines);
    }

    public SourceLines(String nomFichier, int nbrJoueur, boolean desactiverMutation){
        this.desactiverMutation = desactiverMutation;
        if (nbrJoueur == 11) desactiverMutation = true;
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
        choixAction = new ArrayList<>();
        choixAction.add(new ArrayList<>());
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
            switch (partie) {
                case 1:
                    if (lignes.get(i) == ",".getBytes()[0])
                        choixAction.add(new ArrayList<>());
                    else if (lignes.get(i) == ":".getBytes()[0])
                        choixAction.get(choixAction.size()-1).add(new ArrayList<>());
                    else if(random.nextInt(pourcentRandom) == 0 && !desactiverMutation)
                        choixAction.get(choixAction.size() - 1).get(choixAction.get(choixAction.size() - 1).size() - 1).add((byte)(random.nextInt(2)+1));
                    else
                        choixAction.get(choixAction.size() - 1).get(choixAction.get(choixAction.size() - 1).size() - 1).add((byte)(lignes.get(i)-"0".getBytes()[0]));
                    break;
                case 2:
                    if (lignes.get(i) == ":".getBytes()[0])
                        ordreBassin.get(ordreBassin.size() - 1).add(new ArrayList<>());
                    else if (lignes.get(i) == ",".getBytes()[0])
                        ordreBassin.add(new ArrayList<>());
                    else if (lignes.get(i) != ":".getBytes()[0] && (random.nextInt(pourcentRandom) != 0 || desactiverMutation))
                        ordreBassin.get(ordreBassin.size() - 1).get(ordreBassin.get(ordreBassin.size() - 1).size() - 1).add(lignes.get(i));
                    break;
                case 3:
                    if (lignes.get(i) == "?".getBytes()[0])
                        soleil = 1;
                    else if (lignes.get(i) == ":".getBytes()[0])
                        ordreCarte.get(ordreCarte.size() - 1).get(soleil).add(new ArrayList<>());
                    else if (lignes.get(i) == ",".getBytes()[0]) {
                        ordreCarte.add(new ArrayList<>());
                        for (int k = 0; k != 2; ++k)
                            ordreCarte.get(ordreCarte.size() - 1).add(new ArrayList<>());
                        soleil = 0;
                    } else if (lignes.get(i) != ":".getBytes()[0] && (random.nextInt(pourcentRandom) != 0 || desactiverMutation))
                        ordreCarte.get(ordreCarte.size() - 1).get(soleil).get(ordreCarte.get(ordreCarte.size() - 1).get(soleil).size() - 1).add(lignes.get(i));
                    break;

            }
            ++j;
        }
    }

    private List<Byte> combinerStatLines(List<StatLine> statLines){
        int approxRessource = 1;
        int approxOr = 1;
        List<Byte> ligne = new ArrayList<>();
        List<List<List<Byte>>> action = new ArrayList<>();
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            action.add(new ArrayList<>());
        for (StatLine statLine:statLines) {
            for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i) {
                while (action.get(i).size() <= statLine.getChoixAction()[i][1]/approxOr)
                    action.get(i).add(new ArrayList<>());
                action.get(i).get(statLine.getChoixAction()[i][1]/approxOr).add(statLine.getChoixAction()[i][0]);
            }
        }
        for (int i = 0; i != action.size(); ++i){
            if (i != 0)
                ligne.add(",".getBytes()[0]);
            int plusGrand = action.get(i).size();
            if (choixAction.size() > 0 && choixAction.get(i).size() > plusGrand)
                plusGrand = choixAction.get(i).size();
            for (int j = 0; j != plusGrand; ++j) {
                ligne.add(":".getBytes()[0]);
                byte ajout = 0;
                if (choixAction.size() > 0 && choixAction.get(i).size() > j && !choixAction.get(i).get(j).isEmpty())
                    ajout = choixAction.get(i).get(j).get(0);
                if (action.get(i).size() > j && !action.get(i).get(j).isEmpty())
                    ajout = action.get(i).get(j).get(0);
                if (ajout != (byte) 0)
                    ligne.add((byte) (ajout + "0".getBytes()[0]));
            }
        }
        ligne.add(";".getBytes()[0]);//---------------------------------------BASSIN------------------------------------
        List<List<List<Byte>>> bassins = new ArrayList<>();//Manche(Or(numBassin))
        for (int i = 0; i != statLines.get(0).getChoixAction().length; ++i)
            bassins.add(new ArrayList<>());
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
            for (int j = 0; j != plusGrand; ++j) {
                ligne.add(":".getBytes()[0]);
                Set<Byte> antiDoublon = new LinkedHashSet<>();
                if (bassins.get(i).size() > j)
                    antiDoublon.addAll(bassins.get(i).get(j));
                List<Byte> temp = new ArrayList<>();
                if (ordreBassin.size() > 0 && ordreBassin.get(i).size() > j)
                    temp.addAll(ordreBassin.get(i).get(j));
                /*for (int l = 0; l < temp.size() - 1; ++l) {
                    if (random.nextInt(pourcentRandom * 4) == 0) {
                        byte bInf = temp.get(l);
                        byte bSup = temp.get(l + 1);
                        temp.set(l, bSup);
                        temp.set(l + 1, bInf);
                    }
                }*/
                antiDoublon.addAll(temp);
                ligne.addAll(antiDoublon);
            }
        }
        ligne.add(";".getBytes()[0]);//--------------------------------------CARTE--------------------------------------
        List<List<List<List<Byte>>>> cartes = new ArrayList<>();//or/lune(manche(cout(carte)))
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
                    ligne.add(":".getBytes()[0]);
                    Set<Byte> antiDoublon = new LinkedHashSet<>();
                    if (cartes.get(i).get(k).size() > j)
                        antiDoublon.addAll(cartes.get(i).get(k).get(j));
                    List<Byte> temp = new ArrayList<>();
                    if (ordreCarte.size() > 0 && ordreCarte.get(k).get(i).size() > j)
                        temp.addAll(ordreCarte.get(k).get(i).get(j));
                    /*for (int l = 0; l < temp.size()-1; ++l){
                        if (random.nextInt(pourcentRandom * 4) == 0) {
                            byte bInf = temp.get(l);
                            byte bSup = temp.get(l+1);
                            temp.set(l, bSup);
                            temp.set(l+1, bInf);
                        }
                    }*/
                    antiDoublon.addAll(temp);
                    ligne.addAll(antiDoublon);
                }
            }
        }
        return ligne;
    }

    public List<Byte> getLigne() {
        return lignes;
    }

    public List<List<List<Byte>>> getChoixAction(){
        return choixAction;
    }

    public List<List<List<Byte>>> getOrdreBassin(){
        return ordreBassin;
    }

    public List<List<List<Byte>>> getOrdreBassinAvecMutation(){
        for (int i = 0; i != ordreBassin.size(); ++i){
            for (int j = 0; j < ordreBassin.get(i).size(); ++j){
                if (random.nextInt(pourcentRandom) == 0)
                    ordreBassin.get(i).remove(j);
            }
        }
        return ordreBassin;
    }

    public List<List<List<List<Byte>>>> getOrdreCarte(){
        return ordreCarte;
    }

    public List<List<List<List<Byte>>>> getOrdreCarteAvecMutation(){
        for (int i = 0; i != ordreCarte.size(); ++i)
            for (int j = 0; j != ordreCarte.get(i).size(); ++j)
                for (int k = 0; k < ordreCarte.get(i).get(j).size(); ++k)
                    if (random.nextInt(pourcentRandom) == 0)
                        ordreCarte.get(i).get(j).remove(k);
        return ordreCarte;
    }
}

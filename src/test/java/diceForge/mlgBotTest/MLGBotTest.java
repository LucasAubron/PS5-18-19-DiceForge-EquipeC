package diceForge.mlgBotTest;

import bot.mlgBot.SourceLines;
import bot.mlgBot.StatLine;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MLGBotTest {
    @Test
    public void lectureDeFichier() {
        String cible = "src\\test\\java\\diceForge\\mlgBotTest\\fichierTestEcriture";
        File creerFichierTest = new File(cible);
        if (!creerFichierTest.exists()) {
            try {
                creerFichierTest.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        try {
            RandomAccessFile file = new RandomAccessFile(cible, "rw");
            FileChannel channel = file.getChannel();

            // ; = 59 || , = 44 || : = 58 || 0 = 48 || ? = 63

            byte[] bytes = new byte[]{58, 58, 49, 44, 44, 44, 44, 44, 44, 44, 58, 50, 58, 49, 44, 59,
                    58, 8, 58, 4, 7, 58, 8, 58, 5, 44, 44, 44, 44, 58, 9, 58, 7, 58, 3, 44, 44, 44, 44, 59,
                    58, 23, 58, 12, 58, 8, 63, 58, 21, 58, 18, 44, 44, 44, 44, 44, 44, 44, 44};
            ByteBuffer buf = ByteBuffer.allocate(bytes.length);
            buf.clear();
            buf.put(bytes);
            buf.flip();
            channel.write(buf, 0);
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        SourceLines sl = new SourceLines(cible, 11, true);

        List<List<List<Byte>>> actions = new ArrayList<>();
        List<List<List<Byte>>> bassins = new ArrayList<>();
        List<List<List<List<Byte>>>> cartes = new ArrayList<>();
        for (int i = 0; i != 9; ++i){
            actions.add(new ArrayList<>());
            bassins.add(new ArrayList<>());
            cartes.add(new ArrayList<>());
            for (int j = 0; j != 2; ++j)
                cartes.get(i).add(new ArrayList<>());
        }
        actions.get(0).add(new ArrayList<>());
        actions.get(0).add(new ArrayList<>());
        actions.get(0).get(1).add((byte)1);
        actions.get(7).add(new ArrayList<>());
        actions.get(7).get(0).add((byte)2);
        actions.get(7).add(new ArrayList<>());
        actions.get(7).get(1).add((byte)1);

        assertEquals(actions, sl.getChoixAction());

        bassins.get(0).add(new ArrayList<>());
        bassins.get(0).get(0).add((byte)8);
        bassins.get(0).add(new ArrayList<>());
        bassins.get(0).get(1).add((byte)4);
        bassins.get(0).get(1).add((byte)7);
        bassins.get(0).add(new ArrayList<>());
        bassins.get(0).get(2).add((byte)8);
        bassins.get(0).add(new ArrayList<>());
        bassins.get(0).get(3).add((byte)5);
        bassins.get(4).add(new ArrayList<>());
        bassins.get(4).get(0).add((byte)9);
        bassins.get(4).add(new ArrayList<>());
        bassins.get(4).get(1).add((byte)7);
        bassins.get(4).add(new ArrayList<>());
        bassins.get(4).get(2).add((byte)3);

        assertEquals(bassins, sl.getOrdreBassin());


        cartes.get(0).get(0).add(new ArrayList<>());
        cartes.get(0).get(0).get(0).add((byte)23);
        cartes.get(0).get(0).add(new ArrayList<>());
        cartes.get(0).get(0).get(1).add((byte)12);
        cartes.get(0).get(0).add(new ArrayList<>());
        cartes.get(0).get(0).get(2).add((byte)8);
        cartes.get(0).get(1).add(new ArrayList<>());
        cartes.get(0).get(1).get(0).add((byte)21);
        cartes.get(0).get(1).add(new ArrayList<>());
        cartes.get(0).get(1).get(1).add((byte)18);

        assertEquals(cartes, sl.getOrdreCarte());

        creerFichierTest.delete();
    }

    @Test
    public void ecritureDeFichier() {

        // ; = 59 || , = 44 || : = 58 || 0 = 48 || ? = 63

        byte[] bytes = new byte[]{1, 4, 1, 7, 2, 6, 2, 11, 2, 3, 1, 1, 2, 14, 2, 2, 2, 16, 59,
                3, 0, 7, 2, 0, 4, 1, 0, 1, 7, 7, 7, 10, 7, 0, 5, 8, 12, 4, 8, 7, 1, 8, 1, 59,
                44, 44, 23, 4, 5, 44, 12, 6, 6, 44, 11, 9, 9, 44, 44, 22, 1, 5, 44, 44, 59};

        StatLine statLine = new StatLine(bytes, 101);

        byte[][] choixAction = new byte[][]{{1, 4}, {1, 7}, {2, 6}, {2, 11}, {2, 3}, {1, 1}, {2, 14}, {2, 2}, {2, 16}};
        assertArrayEquals(choixAction, statLine.getChoixAction());

        byte[][] choixBassin = new byte[][]{{3, 0, 7}, {2, 0, 4}, {1, 0, 1}, {7, 7, 7}, {10, 7, 0}, {5, 8, 12}, {4, 8, 7}, {1, 8, 1}};
        assertArrayEquals(choixBassin, statLine.getChoixBassin());

        byte[][] choixCarte = new byte[9][3];
        choixCarte[2][0] = 23;
        choixCarte[2][1] = 4;
        choixCarte[2][2] = 5;
        choixCarte[3][0] = 12;
        choixCarte[3][1] = 6;
        choixCarte[3][2] = 6;
        choixCarte[4][0] = 11;
        choixCarte[4][1] = 9;
        choixCarte[4][2] = 9;
        choixCarte[6][0] = 22;
        choixCarte[6][1] = 1;
        choixCarte[6][2] = 5;
        assertArrayEquals(choixCarte, statLine.getChoixCarte());

        SourceLines sourceLines = new SourceLines(new ArrayList<>(Arrays.asList(statLine)));

        List<Byte> b = new ArrayList<Byte>();
        byte[] bb = new byte[]{58, 58, 58, 58, 58, 49, 44, 58, 58, 58, 58, 58, 58, 58, 58, 49, 44,
                58, 58, 58, 58, 58, 58, 58, 50, 44, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 50, 44, 58, 58, 58, 58, 50, 44,
                58, 58, 49, 44, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 50, 44, 58, 58, 58, 50, 44,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 50, 59,
                58, 58, 1, 58, 58, 58, 2, 58, 58, 58, 3, 44, 44, 44, 44, 44, 44, 44, 58, 10, 58, 58, 58, 58, 58, 58, 58, 7, 44,
                58, 58, 1, 58, 58, 58, 58, 58, 58, 4, 58, 58, 58, 58, 58, 5, 59,
                63, 44, 63, 44, 63, 58, 58, 58, 58, 58, 58, 23, 44, 58, 58, 58, 58, 58, 58, 58, 12, 63, 44,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 11, 63, 44, 63, 44, 63, 58, 58, 58, 58, 58, 58, 22, 44, 63, 44, 63};

        for (int i = 0; i != bb.length; ++i)
            b.add(bb[i]);

        assertEquals(b, sourceLines.getLigne());

    }
}

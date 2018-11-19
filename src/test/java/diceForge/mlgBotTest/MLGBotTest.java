package diceForge.mlgBotTest;

import bot.mlgBot.SourceLines;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MLGBotTest {
    @Test
    public void lectureDeFichier() {
        String cible = "src\\test\\java\\diceForge\\mlgBotTest\\fichierTest";
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
        SourceLines sl = new SourceLines(cible, 11);

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
}

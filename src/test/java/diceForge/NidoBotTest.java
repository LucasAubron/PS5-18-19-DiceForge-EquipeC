package diceForge;

import bot.NidoBot.NidoBot;
import org.junit.Test;
import static org.junit.Assert.*;

public class NidoBotTest {

    @Test
    public void dummyTest(){
        Soleil s = new Soleil(2);
        Lune l = new Lune(3);
        System.out.println(l.getClass().getName());
    }

    @Test
    public void TestgetNbFaces(){
        NidoBot nb = new NidoBot(1, null, null);
        De[] des = new De[]{
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new PointDeGloire(2)}}),
                        new Face(new Ressource[][]{{new Soleil(2)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}})},
                        null, nb, 0),
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}}),
                        new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}})}, null, nb, 1)};
        assertEquals(nb.getNbFaces(0, des, new Soleil(1)).getNbSoleils(), 3);
        assertEquals(nb.getNbFaces(0, des, new Lune(1)).getNbLunes(), 1);

        assertEquals(nb.getNbFaces(1, des, new Soleil(1)).getNbSoleils(), 1);
        assertEquals(nb.getNbFaces(1, des, new Lune(1)).getNbLunes(), 3);
    }
}

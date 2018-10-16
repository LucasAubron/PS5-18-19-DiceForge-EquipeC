package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class BassinTest {
    private Bassin b0 = new Bassin(3, new Face(new Ressource[][]{{new Or(3)}}), 4);

    @Test
    public void retirerFace(){
        b0.retirerFace(0);
        assertEquals(b0.getFace().size(), 3);
        b0.retirerFace(0);
        assertEquals(b0.getFace().size(), 2);
        b0.retirerFace(0);
        assertEquals(b0.getFace().size(), 1);
    }
}
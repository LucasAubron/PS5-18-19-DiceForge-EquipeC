package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class BassinTest {
    private Bassin b0 = new Bassin(3, new Face(new Ressource[][]{{new Or(3)}}), 4);
    private Bassin b1 = new Bassin(3, new Face(new Ressource[][]{{new Or(3)}}), 4);
    private Bassin b2 = new Bassin(3, new Face(new Ressource[][]{{new Lune(3)}}), 4);

    @Test
    public void retirerFace(){
        b0.retirerFace(0);
        assertEquals(b0.getFaces().size(), 3);
        b0.retirerFace(0);
        assertEquals(b0.getFaces().size(), 2);
        b0.retirerFace(0);
        assertEquals(b0.getFaces().size(), 1);
    }

    @Test
    public void equals(){
        assertTrue(b0.equals(b1));
        assertTrue(b0.equals(b1));
        assertTrue(b1.equals(b0));
        assertFalse(b0.equals(b2));
        assertFalse(b2.equals(b0));
    }
}
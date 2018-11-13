package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class MarteauTest {

private Marteau m0 = new Marteau();
private Marteau m1 = new Marteau();


    @Test
    public void ajouterPoints() {
        m0.ajouterPoints(5);
        assertEquals(m0.getPoints(),5);
        assertEquals(m0.getNiveau(),0);

        m0.ajouterPoints(6);
        assertEquals(m0.getPoints(),11);
        assertEquals(m0.getNiveau(),0);

        m0.ajouterPoints(6);
        assertEquals(m0.getPoints(),2);
        assertEquals(m0.getNiveau(),1);

        m0.ajouterPoints(14);
        assertEquals(m0.getPoints(),1);
        assertEquals(m0.getNiveau(),2);
    }

    @Test
    public void getNbrPointGloire() {
        m1.ajouterPoints(15);
        assertEquals(m1.getNbrPointGloire(),10);
        m1.ajouterPoints(15);
        assertEquals(m1.getNbrPointGloire(),25);
    }


}
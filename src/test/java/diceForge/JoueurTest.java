package diceForge;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JoueurTest {

    private TestBot j0 = new TestBot(0, false);
    private TestBot j1 = new TestBot(1, false);
    private TestBot j2 = new TestBot(2, false);
    private TestBot j3 = new TestBot(3, false);

    @Test
    public void getOr(){
        assertEquals(j0.getOr(), 3);
        assertEquals(j1.getOr(), 2);
        assertEquals(j2.getOr(), 1);
        assertEquals(j3.getOr(), 0);
    }

    @Test
    public void ajouterOr(){
        j0.ajouterOr(6);
        assertEquals(j0.getOr(), 9);
        j0.ajouterOr(3);
        assertEquals(j0.getOr(), 12);
        j0.ajouterOr(1);
        assertEquals(j0.getOr(), 12);
        j0.ajouterOr(9999999);
        assertEquals(j0.getOr(), 12);
        j0.ajouterOr(-2);
        assertEquals(j0.getOr(), 10);
    }

    @Test
    public void possedeMarteau(){
        j0.ajouterLune(1);
        j0.acheterExploit(new Marteau());
        //assertTrue(j0.possedeMarteau().get(0).equals(new Marteau()));
    }

    @Test
    public void appelerRenforts(){
        j0.ajouterSoleil(1);
        Carte hf = new Carte(new Ressource[]{new Soleil(1)}, 2, "Ancien");
        j0.acheterExploit(hf);
        j0.ajouterOr(3);
        int pdgAct = j0.getPointDeGloire();
        j0.appelerRenforts(new ArrayList<Joueur.Renfort>(Arrays.asList(Joueur.Renfort.ANCIEN)));
        assertEquals(j0.getPointDeGloire(), pdgAct+4);
    }

    @Test
    public void repartitionOrMarteau(){
        j0.ajouterLune(1);
        j0.acheterExploit(new Marteau());
        j0.setNbrPointMarteau(15);
        assertEquals(j0.getMarteau().get(0).getNbrPointGloire(), 0);
        j0.ajouterOr(15);
        assertEquals(j0.getMarteau().get(0).getNbrPointGloire(), 10);
        j0.ajouterOr(15);
        assertEquals(j0.getMarteau().get(0).getNbrPointGloire(), 25);
    }
}
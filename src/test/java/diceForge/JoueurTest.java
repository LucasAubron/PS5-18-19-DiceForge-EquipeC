package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class JoueurTest {

    private RandomBot j0 = new RandomBot(0);
    private RandomBot j1 = new RandomBot(1);
    private RandomBot j2 = new RandomBot(2);
    private RandomBot j3 = new RandomBot(3);

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
    }

    @Test
    public void acheterExploit(){
        String t0 = "";
        Carte c1 = new Carte(new Ressource[]{new Soleil(3)}, 3);
        try {
            j0.acheterExploit(c1);
        } catch (DiceForgeException e){
            t0 = e.getLocalisation();
        }
        assertEquals(t0, "Joueur");
        j0.ajouterSoleil(3);
        j0.acheterExploit(c1);
        j0.additionnerPointsCartes();
        assertEquals(j0.getPointDeGloire(), 3);
    }
}
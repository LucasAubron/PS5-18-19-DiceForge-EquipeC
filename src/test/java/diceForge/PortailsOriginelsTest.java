package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class PortailsOriginelsTest {
    private Joueur j0 = new RandomBot(0, false);
    private Joueur j1 = new RandomBot(1, false);
    /*
    private PortailsOriginels p0 = new PortailsOriginels(new Joueur[]{j0, j1}, false);

    @Test
    public void retirerJoueur() {
        assertEquals(p0.getJoueurs().size(), 2);
        assertEquals(p0.retirerJoueur(0).getIdentifiant(), j0.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 1);
        assertEquals(p0.retirerJoueur(1).getIdentifiant(), j1.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 0);
        p0.ajouterJoueur(j1);
        assertEquals(p0.getJoueurs().size(), 1);
        p0.ajouterJoueur(j0);
        assertEquals(p0.getJoueurs().size(),2);
    }
    */
}
package diceForge;

import org.junit.Test;

import static org.junit.Assert.*;

public class IleTest {
    private Ile i0 = new Ile(new Carte(new Ressource[]{new Soleil(2)}, 3, Carte.Noms.Coffre),
            new Carte(new Ressource[]{new Soleil(3)}, 4, Carte.Noms.Coffre), 4);
    private Joueur j0 = new RandomBot(0, false);
    private Joueur j1 = new RandomBot(1, false);

    @Test
    public void prendreCarte() {
        j0.ajouterSoleil(2);
        assertEquals(i0.prendreCarte(j0, new Carte(new Ressource[]{new Soleil(2)}, 3, Carte.Noms.Coffre)), null);
        assertEquals(i0.getCartes().get(0).size(), 3);
        j1.ajouterSoleil(3);
        assertEquals(i0.prendreCarte(j1, new Carte(new Ressource[]{new Soleil(3)}, 4, Carte.Noms.Coffre)), j0);
        assertEquals(i0.getCartes().get(1).size(), 3);
    }
}
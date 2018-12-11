package diceForge;

import bot.ResteDesBot.RandomBot;
import diceForge.Cartes.*;
import diceForge.ElementPlateau.Ile;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import diceForge.Structure.Coordinateur;
import org.junit.Test;

import static org.junit.Assert.*;

public class IleTest {
    Ile ile0 = new Ile(new Marteau(), new Marteau(), 2, new Afficheur(false));
    private Joueur j0 = new RandomBot(1, new Afficheur(false), null);
    private Joueur j1 = new RandomBot(2, new Afficheur(false), null);

    @Test
    public void prendreCarte() {
        assertNull(ile0.prendreCarte(j0, ile0.getCartes().get(0).get(0)));
        assertEquals(ile0.getCartes().get(0).size(), 1);
        assertEquals(ile0.prendreCarte(j1, ile0.getCartes().get(0).get(0)), j0);
        assertEquals(ile0.getCartes().get(0).size(), 0);
    }
}
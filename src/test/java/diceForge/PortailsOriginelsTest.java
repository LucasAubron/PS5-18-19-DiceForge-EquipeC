package diceForge;


import bot.ResteDesBot.RandomBot;
import diceForge.ElementPlateau.PortailsOriginels;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Afficheur;
import org.junit.Test;

import static org.junit.Assert.*;

public class PortailsOriginelsTest {
    private Joueur j0 = new RandomBot(1,new Afficheur(false), null);
    private Joueur j1 = new RandomBot(2,new Afficheur(false), null);
    
    private PortailsOriginels p0 = new PortailsOriginels(new Joueur.Bot[]{Joueur.Bot.EasyBot,Joueur.Bot.RandomBot}, new Afficheur(false), null);

    @Test
    public void retirerJoueur() {
        assertEquals(p0.getJoueurs().size(), 2);
        assertEquals(p0.retirerJoueur(1).getIdentifiant(), j0.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 1);
        assertEquals(p0.retirerJoueur(2).getIdentifiant(), j1.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 0);
        p0.ajouterJoueur(j1);
        assertEquals(p0.getJoueurs().size(), 1);
        p0.ajouterJoueur(j0);
        assertEquals(p0.getJoueurs().size(),2);

    }

    @Test
    public void ajouterJoueur() {
        assertEquals(p0.getJoueurs().size(), 2);
        assertEquals(p0.retirerJoueur(1).getIdentifiant(), j0.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 1);
        assertEquals(p0.retirerJoueur(2).getIdentifiant(), j1.getIdentifiant());
        assertEquals(p0.getJoueurs().size(), 0);
        p0.ajouterJoueur(j1);
        assertEquals(p0.getJoueurs().size(), 1);
        p0.ajouterJoueur(j0);
        assertEquals(p0.getJoueurs().size(),2);
    }
}
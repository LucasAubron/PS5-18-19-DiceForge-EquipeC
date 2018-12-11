package diceForge;

import diceForge.Cartes.Marteau;
import diceForge.ElementPlateau.Plateau;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Afficheur;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlateauTest {

    private Plateau p0 = new Plateau(new Joueur.Bot[]{Joueur.Bot.RandomBot, Joueur.Bot.RandomBot}, new Afficheur(false));

    @Test
    public void getJoueurs() {
        assertEquals(2, p0.getJoueurs().size());
        assertEquals(2, p0.getJoueurs().get(1).getIdentifiant());//On vérifie que les joueurs sont bien triés
        Joueur j = p0.getPortail().retirerJoueur(1);
        p0.getIles()[0].prendreCarte(j, p0.getIles()[0].getCartes().get(0).get(0));
        assertEquals(2, p0.getJoueurs().size());
        assertEquals(1, p0.getJoueurs().get(0).getIdentifiant());
        assertEquals(2, p0.getJoueurs().get(1).getIdentifiant());
    }
}
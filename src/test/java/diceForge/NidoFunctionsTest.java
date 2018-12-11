package diceForge;

import bot.NidoBot.NidoBot;
import bot.NidoBot.NidoFunctions;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Ressource;
import org.junit.jupiter.api.Test;

import static diceForge.OutilJoueur.Joueur.choixJetonTriton.Soleil;
import static diceForge.OutilJoueur.Ressource.type.*;
import static org.junit.Assert.assertEquals;

public class NidoFunctionsTest {
    @Test
    public void TestgetNbFaces(){
        NidoBot nb = new NidoBot(1, null, null);
        De[] des = new De[]{
                new De(
                        new Face[]{new Face(new Ressource(1, OR)),
                                new Face(new Ressource(1, LUNE)),
                                new Face(new Ressource(2, PDG)),
                                new Face(new Ressource(2, SOLEIL)),
                                new Face(new Ressource(1, SOLEIL)),
                                new Face(new Ressource(1, SOLEIL))},
                        null, nb, 0),
                new De(
                        new Face[]{new Face(new Ressource(1, OR)),
                                new Face(new Ressource(1, SOLEIL)),
                                new Face(new Ressource(1, OR)),
                                new Face(new Ressource(1, LUNE)),
                                new Face(new Ressource(1, LUNE)),
                                new Face(new Ressource(1, LUNE))}, null, nb, 1)};
        assertEquals(NidoFunctions.getNbFaces(0, des, new Ressource(1, SOLEIL)).getNbSoleils(), 3);
        assertEquals(NidoFunctions.getNbFaces(0, des, new Ressource(1, LUNE)).getNbLunes(), 1);
        assertEquals(NidoFunctions.getNbFaces(1, des, new Ressource(1, SOLEIL)).getNbSoleils(), 1);
        assertEquals(NidoFunctions.getNbFaces(1, des, new Ressource(1, LUNE)).getNbLunes(), 3);
    }
}

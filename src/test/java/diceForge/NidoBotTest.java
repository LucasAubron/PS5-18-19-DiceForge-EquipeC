package diceForge;

import bot.NidoBot.NidoBot;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class NidoBotTest {

    @Test
    public void dummyTest(){
        Soleil s = new Soleil(2);
        Lune l = new Lune(3);
        assertEquals(s.getClass().getName(), "diceForge.Soleil");
        assertEquals(l.getClass().getName(), "diceForge.Lune");
    }

    @Test
    public void TestgetNbFaces(){
        NidoBot nb = new NidoBot(1, null, null);
        De[] des = new De[]{
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new PointDeGloire(2)}}),
                        new Face(new Ressource[][]{{new Soleil(2)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}})},
                        null, nb, 0),
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Soleil(1)}}),
                        new Face(new Ressource[][]{{new Or(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}}),
                        new Face(new Ressource[][]{{new Lune(1)}})}, null, nb, 1)};
        assertEquals(nb.getNbFaces(0, des, new Soleil(1)).getNbSoleils(), 3);
        assertEquals(nb.getNbFaces(0, des, new Lune(1)).getNbLunes(), 1);
        assertEquals(nb.getNbFaces(1, des, new Soleil(1)).getNbSoleils(), 1);
        assertEquals(nb.getNbFaces(1, des, new Lune(1)).getNbLunes(), 3);
    }

    @Test
    public void TestgetPosFaceOrQteMin(){
        NidoBot nb = new NidoBot(1, null, null);
        De[] des = new De[]{
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                                new Face(new Ressource[][]{{new Lune(1)}}),
                                new Face(new Ressource[][]{{new Or(3)}}),
                                new Face(new Ressource[][]{{new Soleil(2)}}),
                                new Face(new Ressource[][]{{new Or(4)}}),
                                new Face(new Ressource[][]{{new Or(6)}})},
                        null, nb, 0),
                new De(
                        new Face[]{new Face(new Ressource[][]{{new Soleil(1)}}),
                                new Face(new Ressource[][]{{new Soleil(1)}}),
                                new Face(new Ressource[][]{{new Or(6)}}),
                                new Face(new Ressource[][]{{new Lune(1)}}),
                                new Face(new Ressource[][]{{new Or(4)}}),
                                new Face(new Ressource[][]{{new Or(3)}})},
                        null, nb, 1)};
        assertEquals(nb.getPosFaceQteMin(0, des, new Or(1)), 0); //!\
        assertEquals(nb.getPosFaceQteMin(1, des, new Or(1)), 5);
    }

    @Test
    public void TestgetNbCarteType(){
        NidoBot nb = new NidoBot();
        Carte[] cartes = {
                new Carte(new Ressource[]{new Lune(1)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Soleil(1)}, 0, Carte.Noms.Ancien),
                new Carte(new Ressource[]{new Soleil(1)}, 2, Carte.Noms.HerbesFolles),
                new Carte(new Ressource[]{new Lune(1)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Lune(1)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Soleil(1)}, 0, Carte.Noms.Ancien)
        };
        assertEquals(nb.getNbCarteType(Arrays.asList(cartes), Carte.Noms.Coffre),3);
        assertEquals(nb.getNbCarteType(Arrays.asList(cartes), Carte.Noms.Ancien),2);
        assertEquals(nb.getNbCarteType(Arrays.asList(cartes),  Carte.Noms.Cyclope), 0);
    }

    @Test
    public void TesthaveSoleilsOuLunesBassins(){
        NidoBot nb = new NidoBot();
        int nbrFaceParBassin = 3;
        Bassin[] sanctuaire1 = {
                new Bassin(2, new Face(new Ressource[][]{{new Or(3)}}), nbrFaceParBassin),
                new Bassin(2, new Face(new Ressource[][]{{new Lune(1)}}), nbrFaceParBassin),
                new Bassin(3, new Face(new Ressource[][]{{new Or(4)}}), nbrFaceParBassin),
                new Bassin(3, new Face(new Ressource[][]{{new Soleil(1)}}), nbrFaceParBassin)};
        Bassin[] sanctuaire2 = {
                new Bassin(2, new Face(new Ressource[][]{{new Or(3)}}), nbrFaceParBassin),
                new Bassin(2, new Face(new Ressource[][]{{new Lune(1)}}), nbrFaceParBassin),
                new Bassin(3, new Face(new Ressource[][]{{new Or(4)}}), nbrFaceParBassin)};
        Bassin[] sanctuaire3 = {
                new Bassin(2, new Face(new Ressource[][]{{new Or(3)}}), nbrFaceParBassin),
                new Bassin(2, new Face(new Ressource[][]{{new PointDeGloire(1)}}), nbrFaceParBassin),
                new Bassin(3, new Face(new Ressource[][]{{new Or(4)}}), nbrFaceParBassin)};
        assertTrue(nb.haveSoleilsOuLunesBassins(sanctuaire1));
        assertTrue(nb.haveSoleilsOuLunesBassins(sanctuaire2));
        assertFalse(nb.haveSoleilsOuLunesBassins(sanctuaire3));
    }
}

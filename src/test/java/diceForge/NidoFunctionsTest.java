package diceForge;

import bot.NidoBot.NidoBot;
import bot.NidoBot.NidoFunctions;
import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Ressource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static bot.NidoBot.NidoFunctions.*;
import static diceForge.OutilJoueur.Ressource.type.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void TestgetPosFaceOrQteMin(){
        NidoBot nb = new NidoBot(1, null, null);
        De[] des = new De[]{
                new De(
                        new Face[]{new Face(new Ressource(1, OR)),
                                new Face(new Ressource(1, LUNE)),
                                new Face(new Ressource(3, OR)),
                                new Face(new Ressource(2, SOLEIL)),
                                new Face(new Ressource(4, OR)),
                                new Face(new Ressource(6, OR))},
                        null, nb, 0),
                new De(
                        new Face[]{new Face(new Ressource(1, SOLEIL)),
                                new Face(new Ressource(1, SOLEIL)),
                                new Face(new Ressource(6, OR)),
                                new Face(new Ressource(1, LUNE)),
                                new Face(new Ressource(4, OR)),
                                new Face(new Ressource(3, OR))},
                        null, nb, 1)};
        assertEquals(getPosFaceQteMin(0, des, new Ressource(1, OR)), 0); //!\
        assertEquals(getPosFaceQteMin(1, des, new Ressource(1, OR)), 5);
    }

    @Test
    public void TestgetNbCarteType(){
        NidoBot nb = new NidoBot();
        Carte[] cartes = {
                new Carte(new Ressource[]{new Ressource(1, LUNE)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Ressource(1, SOLEIL)}, 0, Carte.Noms.Ancien),
                new Carte(new Ressource[]{new Ressource(1, SOLEIL)}, 2, Carte.Noms.HerbesFolles),
                new Carte(new Ressource[]{new Ressource(1, LUNE)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Ressource(1, LUNE)}, 2, Carte.Noms.Coffre),
                new Carte(new Ressource[]{new Ressource(1, SOLEIL)}, 0, Carte.Noms.Ancien)
        };
        assertEquals(getNbCarteType(Arrays.asList(cartes), Carte.Noms.Coffre),3);
        assertEquals(getNbCarteType(Arrays.asList(cartes), Carte.Noms.Ancien),2);
        assertEquals(getNbCarteType(Arrays.asList(cartes),  Carte.Noms.Cyclope), 0);
    }

    @Test
    public void TesthaveSoleilsOuLunesBassins(){
        NidoBot nb = new NidoBot();
        int nbrFaceParBassin = 3;
        Bassin[] sanctuaire1 = {
                new Bassin(2, new Face(new Ressource(3, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceOr),
                new Bassin(2, new Face(new Ressource(1, LUNE)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceLune),
                new Bassin(3, new Face(new Ressource(4, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceOr),
                new Bassin(3, new Face(new Ressource(1, SOLEIL)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceSoleil)};
        Bassin[] sanctuaire2 = {
                new Bassin(2, new Face(new Ressource(3, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceOr),
                new Bassin(2, new Face(new Ressource(1, LUNE)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceLune),
                new Bassin(3, new Face(new Ressource(4, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceOr)};
        Bassin[] sanctuaire3 = {
                new Bassin(2, new Face(new Ressource(3, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout2FaceOr),
                new Bassin(8, new Face(new Ressource(3, PDG)), nbrFaceParBassin, Bassin.typeBassin.Cout8FacePdg),
                new Bassin(3, new Face(new Ressource(4, OR)), nbrFaceParBassin, Bassin.typeBassin.Cout3FaceOr)
        };
        assertTrue(haveSoleilsOuLunesBassins(sanctuaire1));
        assertTrue(haveSoleilsOuLunesBassins(sanctuaire2));
        assertFalse(haveSoleilsOuLunesBassins(sanctuaire3));
    }
}

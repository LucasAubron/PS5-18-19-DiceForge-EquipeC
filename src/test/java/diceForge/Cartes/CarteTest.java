package diceForge.Cartes;

import bot.ResteDesBot.EasyBot;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import org.junit.Test;

import static org.junit.Assert.*;

public class CarteTest {
    private Plateau p0 = new Plateau(new Joueur.Bot[]{Joueur.Bot.EasyBot, Joueur.Bot.EasyBot}, new Afficheur(false));
    private Joueur j0 = p0.getJoueurs().get(0);
    private Carte biche = new Carte(new Ressource[]{new Ressource(2, Ressource.type.LUNE)}, 2,  Carte.Noms.Biche);
    private Carte ancien = new Carte(new Ressource[]{new Ressource(1, Ressource.type.SOLEIL)}, 0,  Carte.Noms.Ancien);
    private Carte hibou = new Carte(new Ressource[]{new Ressource(2, Ressource.type.SOLEIL)}, 4,  Carte.Noms.Hibou);
    private Carte triton= new Carte(new Ressource[]{new Ressource(4, Ressource.type.SOLEIL)}, 8,  Carte.Noms.Triton);
    private Carte cerbere= new Carte(new Ressource[]{new Ressource(4, Ressource.type.LUNE)}, 6,  Carte.Noms.Cerbere);
    private Carte casqueInvi= new Carte(new Ressource[]{new Ressource(5, Ressource.type.LUNE)}, 4,  Carte.Noms.CasqueDinvisibilite);
    private Carte cyclope = new Carte(new Ressource[]{new Ressource(6, Ressource.type.SOLEIL)}, 8,  Carte.Noms.Cyclope);

    @Test
    public void effetDirect(){
        biche.effetDirect(j0);
        assertEquals(j0.getRenforts().get(0),Joueur.Renfort.BICHE);

        ancien.effetDirect(j0);
        assertEquals(j0.getRenforts().get(1),Joueur.Renfort.ANCIEN);

        hibou.effetDirect(j0);
        assertEquals(j0.getRenforts().get(2),Joueur.Renfort.HIBOU);

        triton.effetDirect(j0);
        assertEquals(j0.getJetons().get(0),Joueur.Jeton.TRITON);

        cerbere.effetDirect(j0);
        assertEquals(j0.getJetons().get(1),Joueur.Jeton.CERBERE);

        casqueInvi.effetDirect(j0);
        assertEquals(j0.getDe(1).getFace(0).getTypeFace(), Face.typeFace.X3);

        int pointBefore=j0.getPointDeGloire();
        int soleilAvant=j0.getSoleil();
        int orAvant=j0.getOr();

        cyclope.effetDirect(j0);
        assertTrue(pointBefore< j0.getPointDeGloire() || soleilAvant < j0.getSoleil() || orAvant < j0.getOr());
        

    }
}
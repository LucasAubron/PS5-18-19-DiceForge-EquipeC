package diceForge;

import bot.ResteDesBot.EasyBot;
import diceForge.Cartes.Carte;
import diceForge.Cartes.Marteau;
import diceForge.ElementPlateau.Temple;
import diceForge.Faces.Face;
import diceForge.Faces.FaceBouclier;
import diceForge.Faces.FaceVoileCeleste;
import diceForge.Faces.FaceX3;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JoueurTest {

    private Joueur j1 = new EasyBot(1, new Afficheur(false), null);
    private Temple t1 = new Temple(2);

    private void setRessourceAZero(Joueur joueur){
        joueur.ajouterSoleil(-50);
        joueur.ajouterOr(-50);
        joueur.ajouterLune(-50);
        joueur.ajouterPointDeGloire(-500);
    }

    @Test
    public void ajouterOrSansMarteau(){
        setRessourceAZero(j1);
        assertEquals(0, j1.getOr());
        j1.ajouterOr(3);
        assertEquals(3, j1.getOr());
        j1.ajouterOr(7);
        assertEquals(10, j1.getOr());
    }

    @Test
    public void AjouterOrAvecMarteau(){
        setRessourceAZero(j1);
        assertEquals(0, j1.getLune());
        j1.ajouterLune(1);
        assertEquals(1, j1.getLune());
        j1.acheterExploit(new Marteau());
        assertTrue(j1.possedeCarte(Carte.Noms.Marteau));
        assertEquals(0, j1.getOr());
        j1.ajouterOr(8);
        assertEquals(0, j1.getOr());
        assertEquals(8, j1.getMarteau().get(0).getPoints());
        j1.ajouterOr(8);
        assertEquals(0, j1.getOr());
        assertEquals(1, j1.getMarteau().get(0).getPoints());
        assertEquals(10, j1.getMarteau().get(0).getNbrPointGloire());
        j1.ajouterLune(1);
        j1.acheterExploit(new Marteau());
        j1.ajouterOr(8);
        assertEquals(9, j1.getMarteau().get(0).getPoints());
        assertEquals(0, j1.getMarteau().get(1).getPoints());
        j1.ajouterOr(8);
        assertEquals(25, j1.getMarteau().get(0).getNbrPointGloire());
        assertEquals(2, j1.getMarteau().get(1).getPoints());
        assertEquals(0, j1.getOr());
        j1.ajouterOr(10);
        assertEquals(12, j1.getMarteau().get(1).getPoints());
        assertEquals(0, j1.getOr());
        j1.ajouterOr(8);
        j1.ajouterOr(8);
        j1.ajouterOr(8);
        assertEquals(25, j1.getMarteau().get(1).getNbrPointGloire());
        assertEquals(6, j1.getOr());
    }

    @Test
    public void jetons(){
        setRessourceAZero(j1);
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        assertEquals(Joueur.Jeton.TRITON, j1.getJetons().get(0));
        j1.appliquerJetonTriton(Joueur.choixJetonTriton.Or);
        assertEquals(6, j1.getOr());
        j1.retirerJeton(Joueur.Jeton.TRITON);
        assertEquals(0, j1.getJetons().size());
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        assertEquals(2, j1.getJetons().size());
        j1.retirerJeton(Joueur.Jeton.TRITON);
        assertEquals(1, j1.getJetons().size());
    }

    @Test
    public void gagnerRessourceDesDeuxDes(){
        setRessourceAZero(j1);
        j1.getDe(0).setFaceActive(new Face(new Ressource(1, Ressource.type.OR)));
        j1.getDe(1).setFaceActive(new Face(new Ressource(1, Ressource.type.OR)));
        j1.gagnerRessourceDesDeuxDes();
        assertEquals(j1.getOr(), 2);

        setRessourceAZero(j1);
        j1.getDe(0).setFaceActive(new Face(Face.typeFace.X3));
        j1.getDe(1).setFaceActive(new Face(new Ressource(1, Ressource.type.SOLEIL)));
        j1.gagnerRessourceDesDeuxDes();
        assertEquals(j1.getSoleil(), 3);

        setRessourceAZero(j1);
        j1.getDe(1).setFaceActive(new Face(Face.typeFace.X3));
        j1.getDe(0).setFaceActive(new Face(new Ressource(2, Ressource.type.PDG)));
        j1.gagnerRessourceDesDeuxDes();
        assertEquals(j1.getPointDeGloire(), 6);

        setRessourceAZero(j1);
        ((EasyBot) j1).numManche = 8;
        j1.getDe(0).setFaceActive(new FaceBouclier(new Ressource(2, Ressource.type.SOLEIL)));
        j1.getDe(1).setFaceActive(new FaceX3());
        j1.gagnerRessourceDesDeuxDes();
        assertEquals(j1.getSoleil(), 6);

        setRessourceAZero(j1);
        ((EasyBot) j1).numManche = 8;
        j1.getDe(0).setFaceActive(new FaceBouclier(new Ressource(2, Ressource.type.SOLEIL)));
        j1.getDe(1).setFaceActive(new Face(new Ressource(1, Ressource.type.SOLEIL)));
        j1.gagnerRessourceDesDeuxDes();
        assertEquals(j1.getSoleil(), 1);
        assertEquals(j1.getPointDeGloire(), 5);
    }

    @Test
    public void additionerPointsCartes(){
        j1.additionnerPointsCartes();
        assertEquals(0, j1.getPointDeGloire());
        j1.ajouterLune(5);
        j1.ajouterSoleil(5);
        j1.acheterExploit(new Carte(new Ressource[]{new Ressource(5, Ressource.type.SOLEIL), new Ressource(5, Ressource.type.LUNE)}, 26, Carte.Noms.Hydre));
        j1.additionnerPointsCartes();
        assertEquals(26, j1.getPointDeGloire());
    }
}

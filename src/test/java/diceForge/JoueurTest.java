package diceForge;

import bot.TestBot;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JoueurTest {
    private Joueur j1 = new TestBot(1, new Afficheur(false), null);

    @Test
    public void ajouterOrSansMarteau(){
        assertEquals(3, j1.getOr());
        j1.ajouterOr(3);
        assertEquals(6, j1.getOr());
        j1.ajouterOr(7);
        assertEquals(12, j1.getOr());
    }

    @Test
    public void AjouterOrAvecMarteau(){
        assertEquals(0, j1.getLune());
        j1.ajouterLune(1);
        assertEquals(1, j1.getLune());
        j1.acheterExploit(new Marteau());
        assertEquals(0, j1.getLune());
        assertTrue(j1.possedeCarte(Carte.Noms.Marteau));
        TestBot j1T = (TestBot) j1;
        j1T.setNbrPointMarteau(8);
        assertEquals(3, j1.getOr());
        j1.ajouterOr(8);
        assertEquals(3, j1.getOr());
        assertEquals(8, j1.getMarteau().get(0).getPoints());
        j1.ajouterOr(8);
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
        assertEquals(3, j1.getOr());
        j1.ajouterOr(10);
        assertEquals(10, j1.getMarteau().get(1).getPoints());
        assertEquals(5, j1.getOr());
        j1.ajouterOr(8);
        j1.ajouterOr(8);
        j1.ajouterOr(8);
        assertEquals(25, j1.getMarteau().get(1).getNbrPointGloire());
        assertEquals(9, j1.getOr());
    }

    @Test
    public void jetons(){
        assertEquals(3, j1.getOr());
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        assertEquals(Joueur.Jeton.TRITON, j1.getJetons().get(0));
        j1.appliquerJetonTriton(Joueur.choixJetonTriton.Or);
        assertEquals(9, j1.getOr());
        j1.retirerJeton(Joueur.Jeton.TRITON);
        assertEquals(0, j1.getJetons().size());
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        j1.ajouterJeton(Joueur.Jeton.TRITON);
        assertEquals(2, j1.getJetons().size());
        j1.retirerJeton(Joueur.Jeton.TRITON);
        assertEquals(1, j1.getJetons().size());
    }

    @Test
    public void renforts(){
        List<Joueur.Renfort> listeR = new ArrayList<>();
        listeR.add(Joueur.Renfort.ANCIEN);
        listeR.add(Joueur.Renfort.BICHE);
        listeR.add(Joueur.Renfort.HIBOU);
        TestBot j1T = (TestBot) j1;
        j1T.setNumDe(1);
        j1T.setNumFace(1);
        j1.appelerRenforts(listeR);
        assertEquals(4, j1.getPointDeGloire());
        assertTrue(j1.getOr() == 1 || j1.getSoleil() == 1);
        assertEquals(1, j1.getLune());
    }

    @Test
    public void forgerDe(){
        j1.forgerDe(1, new FaceX3(), 1);
        assertEquals(new FaceX3().toString(), j1.getDes()[1].getFace(1).toString());
    }

    @Test
    public void additionerPointsCartes(){
        j1.additionnerPointsCartes();
        assertEquals(0, j1.getPointDeGloire());
        j1.ajouterLune(5);
        j1.ajouterSoleil(5);
        j1.acheterExploit(new Carte(new Ressource[]{new Or(5), new Lune(5)}, 26, Carte.Noms.Hydre));
        j1.additionnerPointsCartes();
        assertEquals(26, j1.getPointDeGloire());
    }

    @Test
    public void gagnerRessourceFace(){
        j1.gagnerRessourceFace(new Face(new Ressource[][]{{new Lune(5)}}));
        assertEquals(5, j1.getLune());
        TestBot j1T = (TestBot) j1;
        j1T.setNumFace(1);
        j1.gagnerRessourceFace(new Face(new Ressource[][]{{new Soleil(1)}, {new Soleil(2)}, {new Soleil(3)}}));
        assertEquals(2, j1.getSoleil());
    }
}

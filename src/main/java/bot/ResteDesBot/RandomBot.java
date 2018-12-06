package bot.ResteDesBot;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot extends Joueur {
    private Random random = new Random();
    public RandomBot(int identifiant, Afficheur afficheur, Plateau plateau){ super(identifiant, afficheur, plateau);
    }

    @Override
    public Action choisirAction() {
        switch (random.nextInt(2)) {
            case 0:
                return Action.EXPLOIT;
            case 1:
                return Action.FORGER;
            case 2:
                return Action.PASSER;
        }
        return null;
    }


    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins){
        return new ChoixJoueurForge(bassins.get(random.nextInt(bassins.size())), 0, random.nextInt(2), random.nextInt(6));
    }

    @Override
    public int[] choisirOuForgerFaceSpeciale(Face faceSpeciale){
        return new int[]{random.nextInt(2), random.nextInt(6)};
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes){
        return cartes.get(random.nextInt(cartes.size()));
    }

    @Override
    public boolean choisirActionSupplementaire(){
        return (random.nextInt(2)==0) ? true : false;
    }

    @Override
    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources){
        return ressources[random.nextInt(ressources.length)];
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int nbrOr){
        return random.nextInt(nbrOr+1);
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){
        return renfortsUtilisables;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces){
        return faces.get(random.nextInt(faces.size()));
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources){
        return ressources[random.nextInt(ressources.length)];
    }

    @Override
    public int choisirDeFaveurMineure(){
        return random.nextInt(2);
    }

    @Override
    public int choisirDeCyclope(){
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return random.nextInt(joueurs.size());
    }

    @Override
    public Joueur.choixJetonTriton utiliserJetonTriton(){
        switch (random.nextInt(4)){
            case 0:
                return choixJetonTriton.Or;
            case 1:
                return choixJetonTriton.Soleil;
            case 2:
                return choixJetonTriton.Lune;
            case 3:
                return choixJetonTriton.Rien;
            default:
                return null;
        }
    }

    @Override
    public boolean utiliserJetonCerbere(){
        return (random.nextInt()==0) ? true : false;
    }

    @Override
    public boolean choisirPdgPlutotQueRessource(Ressource ressource){
        return (random.nextInt()==0) ? true : false;
    }

    @Override
    public String toString(){return "RandomBot";}
}

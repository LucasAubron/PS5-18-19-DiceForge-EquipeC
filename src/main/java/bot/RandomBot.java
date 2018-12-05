package bot;

import diceForge.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot extends Joueur {
    private Random random = new Random();
    public RandomBot(int identifiant, Afficheur afficheur, Plateau plateau){ super(identifiant, afficheur, plateau);
    }

    @Override
    public Action choisirAction(int numManche) {
        switch (random.nextInt(3)) {
            case 0:
                return Action.EXPLOIT;
            case 1:
                return Action.FORGER;
            case 2:
                return Action.PASSER;
        }
    }


    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        return ChoixJoueurForge()
    }

    @Override
    public int[] choisirOuForgerFaceSpeciale(Face faceSpeciale){}

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){}

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){}

    @Override
    public boolean choisirActionSupplementaire(int numManche){}

    @Override
    public int choisirRessourceFaceAchoix(Ressource[] ressources){}

    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){}

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables){}

    @Override
    public Face choisirFaceACopier(List<Face> faces){}

    @Override
    public int choisirRessourceAPerdre(Ressource[] ressources){}

    @Override
    public int choisirDeFaveurMineure(){}

    @Override
    public int choisirDeCyclope(){}

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){}

    @Override
    public choixJetonTriton utiliserJetonTriton(){}

    @Override
    public boolean utiliserJetonCerbere(){}

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource){}

    @Override
    public String toString(){return "RandomBot";}
}

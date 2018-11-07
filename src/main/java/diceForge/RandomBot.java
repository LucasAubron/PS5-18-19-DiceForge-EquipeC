package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot extends Joueur{
    private Random random = new Random();
    RandomBot(int identifiant){
        super(identifiant);
    }

    @Override
    public Action choisirAction(int numTour){
        Random random = new Random();
        int action = random.nextInt(3);
        switch (action) {
            case 0:
                return Action.FORGER;
            case 1:
                return Action.EXPLOIT;
            case 2:
                return Action.PASSER;
        }
        throw new DiceForgeException("RandomBot","?");
    }

    @Override
    public ChoixJoueurForge choisirFaceAForger(List<Bassin> bassins, int numManche){
        int numBassin = random.nextInt(bassins.size()+1);//On génére tout les nombres random dont on a besoin, +1 correspond au cas où il décide de s'arrêter de forger
        if (numBassin == bassins.size())
            return new ChoixJoueurForge(null, 0, 0, 0);
        int numFace = random.nextInt(bassins.get(numBassin).getFaces().size());
        int numDe = random.nextInt(getDes().length);
        int posFace = random.nextInt(getDes()[0].getFaces().length);
        return new ChoixJoueurForge(bassins.get(numBassin), numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        return cartes.get(random.nextInt(cartes.size()));
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        int pileFace = random.nextInt(2);
        return pileFace == 1;
    }

    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){
        return random.nextInt(nbrOr+1);
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        List<Renfort> renforts = new ArrayList<>();
        for (Object renfort: renfortsUtilisables)
            if (random.nextInt(2) == 1)//1 chance sur 2 d'ajouter chaque renfort
                renforts.add((Renfort) renfort);
        return renforts;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        return random.nextInt(faceAChoix.getRessource().length);
    }

    @Override
    public int choisirDeBiche(){
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return (getIdentifiant() == 1 ? 0 : 1);
    }

    @Override
    public void forgerFace(Face face){
        forgerDe(random.nextInt(2), face, random.nextInt(6));
    }

    @Override
    public int choisirFace(List<Face> faces){
        return random.nextInt(faces.size());
    }
}

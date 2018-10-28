package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot extends Joueur{
    private Random random = new Random();
    public RandomBot(int identifiant){
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
        throw new DiceForgeException("RandomBot","Il y a petit problème dans ma plantation, pourquoi ça pousse pas ?");
    }

    @Override
    public Bassin choisirFaceAForger(List<Bassin> bassins, int numManche){
        int numBassin = random.nextInt(bassins.size()+1);//On génére tout les nombres random dont on a besoin, +1 correspond au cas où il décide de s'arrêter de forger
        if (numBassin == bassins.size())
            return null;
        int numFace = random.nextInt(bassins.get(numBassin).getFace().size());
        int numDe = random.nextInt(getDes().length);
        int posFace = random.nextInt(getDes()[0].getFaces().length);
        //System.out.println(bassins.get(numBassin).getFace().get(numFace));
        forgerDe(numDe, bassins.get(numBassin).retirerFace(numFace), posFace);//On forge le dé et on retire la face en meme temps
        return bassins.get(numBassin);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        return cartes.get(random.nextInt(cartes.size()));
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        int pileFace = random.nextInt(2);
        if (pileFace == 1) ajouterSoleil(-2);
        return pileFace == 1;
    }

    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){
        return random.nextInt(nbrOr);
    }

    @Override
    public List<Renfort> choisirRenforts(){
        List<Renfort> renforts = new ArrayList<>();
        for (Renfort renfort:getRenforts())
            if (random.nextInt(2) == 1)//1 chance sur 2 d'ajouter chaque renfort
                renforts.add(renfort);
        return renforts;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        return random.nextInt(faceAChoix.getRessource().length);
    }
}

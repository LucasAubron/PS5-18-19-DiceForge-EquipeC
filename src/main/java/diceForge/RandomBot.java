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
        int numBassin = random.nextInt(bassins.size());//On génére tout les nombres random que l'on a besoin
        int numFace = random.nextInt(bassins.get(numBassin).getFace().size());
        int numDe = random.nextInt(des.length);
        int posFace = random.nextInt(des[0].getFaces().length);

        forgerDe(numDe, bassins.get(numBassin).retirerFace(numFace), posFace);//On forge le dé et on retire la face en meme temps
        return bassins.get(numBassin);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        int numCarte = random.nextInt(cartes.size());
        return cartes.get(numCarte);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        int pileFace = random.nextInt(2);
        if (pileFace == 1) soleil -= 2;
        return pileFace == 1;
    }

    @Override
    public boolean choisirContinuerForger(){
        int pileFace = random.nextInt(2);
        return pileFace == 1;
    }
}

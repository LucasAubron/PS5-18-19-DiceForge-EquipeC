package diceForge;

import java.util.ArrayList;
import java.util.Random;

public class RandomBot extends Joueur{
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
        throw new RuntimeException("Il y a petit problème dans ma plantation, pourquoi ça pousse pas ?");
    }

    @Override
    public void choisirFaceAForger(ArrayList<Bassin> bassins, int numManche){
        Random random = new Random();//On génére tout les nombres random que l'on a besoin
        int numBassin = random.nextInt(bassins.size());
        int numFace = random.nextInt(bassins.get(numBassin).getFace().length);
        int numDe = random.nextInt(des.length);
        int posFace = random.nextInt(des[0].getFaces().length);
        forgerDe(numDe, bassins.get(numBassin).retirerFace(numFace), posFace);//On forge le dé et on retire la face en meme temps
    }
}

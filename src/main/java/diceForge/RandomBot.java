package diceForge;

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
}

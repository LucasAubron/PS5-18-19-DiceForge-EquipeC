package diceForge;

import java.util.ArrayList;
import java.util.List;
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
        throw new DiceForgeException("RandomBot","Il y a petit problème dans ma plantation, pourquoi ça pousse pas ?");
    }

    @Override
    public void choisirFaceAForger(List<Bassin> bassins, int numManche){
        Random random = new Random();//On génére tout les nombres random que l'on a besoin
        int numBassin = random.nextInt(bassins.size());
        int numFace = random.nextInt(bassins.get(numBassin).getFace().size());
        int numDe = random.nextInt(des.length);
        int posFace = random.nextInt(des[0].getFaces().length);

        forgerDe(numDe, bassins.get(numBassin).retirerFace(numFace), posFace);//On forge le dé et on retire la face en meme temps
        bassins.remove(numBassin);//On retire le bassin (on ne peut plus forger depuis ce bassin)

        List<Bassin> bassinsAffordables = new ArrayList<>();//On va regarder quelle face sont encore abordable
        for (Bassin bassin:bassins)//On parcours les bassins que l'on a
            if (bassin.getCout() <= or)//S'il est pas vide et affordable
                bassinsAffordables.add(bassin);//On l'ajoute

        if (!bassinsAffordables.isEmpty())//Si la liste que l'on obtient n'est pas vide
            choisirFaceAForger(bassinsAffordables, numManche);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Random random = new Random();
        int numCarte = random.nextInt(cartes.size());
        return cartes.get(numCarte);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        Random random = new Random();
        int pileFace = random.nextInt(2);
        return pileFace == 1;
    }
}

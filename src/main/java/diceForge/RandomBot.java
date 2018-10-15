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
        int numFace = random.nextInt(bassins.get(numBassin).nbrFaceRestante());
        numFace = bassins.get(numBassin).numFacesRestante().get(numFace);
        int numDe = random.nextInt(des.length);
        int posFace = random.nextInt(des[0].getFaces().length);
        forgerDe(numDe, bassins.get(numBassin).retirerFace(numFace), posFace);//On forge le dé et on retire la face en meme temps
        bassins.remove(numBassin);//On retire le bassin (on ne peut plus forger depuis ce bassin)
        ArrayList<Bassin> bassinsAffordables = new ArrayList<>();//On va regarder quelle face sont encore abordable
        for (Bassin bassin:bassins)//On parcours les bassins que l'on a
            if (bassin.getCout() <= or)//S'il est pas vide et affordable
                bassinsAffordables.add(bassin);//On l'ajoute
        if (!bassinsAffordables.isEmpty())//Si la liste que l'on obtient n'est pas vide
            choisirFaceAForger(bassinsAffordables, numManche);
    }

    @Override
    public Joueur choisirCarte(ArrayList<Carte> cartes, int numManche){
        return null;
    }
}

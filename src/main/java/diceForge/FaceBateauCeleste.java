package diceForge;

import java.util.ArrayList;
import java.util.List;

class FaceBateauCeleste extends Face {
    private Temple temple;
    private int multiplierX3 = 1;
    FaceBateauCeleste(Temple temple){
        super(new Ressource[][]{});
        this.temple = temple;
    }

    Temple getTemple() {
        return temple;
    }

    void multiplierX3Actif(){
        multiplierX3 = 3;
    }

    @Override
    void effetActif(Joueur joueur){
            List<Bassin> bassinsAbordables = new ArrayList<>();
            for (Bassin bassin : temple.getSanctuaire())
                if (bassin.getCout() - 2*multiplierX3 >= joueur.getOr())
                    bassinsAbordables.add(bassin);
            if (!bassinsAbordables.isEmpty()) {
                ChoixJoueurForge choixJoueurForge = joueur.choisirFaceAForgerEtARemplacer(bassinsAbordables, 5);//numManche au pif, parce qu'on ne le connais pas
                if (choixJoueurForge.getBassin() != null) {
                    joueur.forgerDe(choixJoueurForge.getNumDe(), choixJoueurForge.getBassin().retirerFace(choixJoueurForge.getNumFace()), choixJoueurForge.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
                    joueur.ajouterOr(-choixJoueurForge.getBassin().getCout()+2*multiplierX3);//On oublie pas de faire payer le joueur
                }
            }
            multiplierX3 = 1;
    }

    @Override
    public String toString(){
        return "Face bateau celeste";
    }
}

package diceForge;

import java.util.ArrayList;
import java.util.List;

class FaceBateauCeleste extends Face {
    private Temple temple;
    FaceBateauCeleste(Temple temple){
        super(new Ressource[][]{});
        this.temple = temple;
    }

    Temple getTemple() {
        return temple;
    }

    @Override
    void effetActif(Joueur joueur){
            List<Bassin> bassinsAbordables = new ArrayList<>();
            for (Bassin bassin : temple.getSanctuaire())
                if (bassin.getCout() - 2 >= joueur.getOr())
                    bassinsAbordables.add(bassin);
            if (!bassinsAbordables.isEmpty()) {
                ChoixJoueurForge choixJoueurForge = joueur.choisirFaceAForgerEtARemplacer(bassinsAbordables, 5);//numManche au pif, parce qu'on ne le connais pas
                if (choixJoueurForge.getBassin() != null) {
                    joueur.forgerDe(choixJoueurForge.getNumDe(), choixJoueurForge.getBassin().retirerFace(choixJoueurForge.getNumFace()), choixJoueurForge.getPosFace()); //on forge un dé (= enlever une face d'un dé et la remplacer), et on retire la face du bassin
                    joueur.ajouterOr(-choixJoueurForge.getBassin().getCout()+2);//On oublie pas de faire payer le joueur
                }
            }
    }

    @Override
    public String toString(){
        return "Face bateau celeste";
    }
}

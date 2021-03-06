package diceForge.Faces;

import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Temple;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;

import java.util.ArrayList;
import java.util.List;

public class FaceVoileCeleste extends Face {
    private Temple temple;
    private int multiplierX3 = 1;
    public FaceVoileCeleste(Temple temple){
        super(typeFace.VOILECELESTE);
        this.temple = temple;
    }

    public void multiplierX3Actif(){
        multiplierX3 = 3;
    }

    @Override
    public void effetActif(Joueur joueur){
        List<Bassin> bassinsAbordables = new ArrayList<>();
        for (Bassin bassin : temple.getSanctuaire())
            if (bassin.getCout() - 2*multiplierX3 <= joueur.getOr() && !bassin.getFaces().isEmpty())
                bassinsAbordables.add(bassin);
        if (!bassinsAbordables.isEmpty()) {
            ChoixJoueurForge choixDuJoueur = joueur.choisirFaceAForgerEtARemplacer(bassinsAbordables);
            if (choixDuJoueur != null) {
                Bassin bassinChoisi = choixDuJoueur.getBassin();
                int numFaceBassinChoisi = choixDuJoueur.getNumFaceDansBassin();
                int idDeChoisi = choixDuJoueur.getNumDe();
                int numPosDeChoisi = choixDuJoueur.getPosFaceSurDe();
                joueur.getDe(idDeChoisi).forger(bassinChoisi.retirerFace(numFaceBassinChoisi), numPosDeChoisi);
                joueur.ajouterOr(-choixDuJoueur.getBassin().getCout()+2*multiplierX3);//On oublie pas de faire payer le joueur
            }
        }
        multiplierX3 = 1;
    }

    @Override
    public String toString(){
        return "voile celeste ";
    }
}
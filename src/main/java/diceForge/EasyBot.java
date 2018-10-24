package diceForge;

import java.util.List;

public class EasyBot extends Joueur{
    public EasyBot(int identifiant) {super(identifiant);}

    @Override
    public Action choisirAction(int numManche){
        if (numManche < 6 && getOr() > 5)//Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;//Sinon on passe
    }

    @Override
    public Bassin choisirFaceAForger(List<Bassin> bassins, int numManche){
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 3 && bassin.getFace().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches
                for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
                    for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                        if (getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                            forgerDe(i, bassin.retirerFace(0), j);//Si c'est un face 1 or, on forge
                            return bassin;
                        }
                    }
                }
            }
            else if (bassinAChoisir != null && bassinAChoisir.getCout() < bassin.getCout())//Sinon, on cherche la face la plus chere
                bassinAChoisir = bassin;
            else if (bassinAChoisir == null)
                bassinAChoisir = bassin;
        }
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    forgerDe(i, bassinAChoisir.retirerFace(0), j);//Si c'est un face 1 or, on forge
                    return bassinAChoisir;
                }
            }
        }
        return null;//Si on ne trouve rien à forger
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals("Marteau") && !possedeCarte("Marteau"))//Au moins 1 marteau
                return carte;
            if (carte.getNom().equals("Coffre") && !possedeCarte("Coffre"))//Et un coffre
                return carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        return carteAChoisir;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return ((getOr() > 10 && numManche < 6) || getSoleil() > 3 || getLune() > 1);//Si on a assez de ressource pour refaire un tour
    }

    @Override
    public boolean choisirContinuerForger(){return true;}//On veut toujours forger

    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){return 0;}//On met tous dans le marteau

    @Override
    public List<Renfort> choisirRenforts(){
        return getRenforts();//On appelle tous les renforts, si on ne peut pas, c'est géré plus tard
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil)
                    return i;//On cherche une face qui donne des soleils ou des lunes
            }
        }
        return 0;
    }
}
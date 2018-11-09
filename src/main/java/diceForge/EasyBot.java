package diceForge;

import java.util.List;
import java.util.Random;

class EasyBot extends Joueur{
    EasyBot(int identifiant) {super(identifiant);}

    @Override
    Action choisirAction(int numManche){
        if (numManche < 6 && getOr() > 5)//Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;//Sinon on passe
    }

    @Override
    ChoixJoueurForge choisirFaceAForger(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            else if (bassinAChoisir != null && bassinAChoisir.getCout() < bassin.getCout())//Sinon, on cherche la face la plus chere
                bassinAChoisir = bassin;
            else if (bassinAChoisir == null)
                bassinAChoisir = bassin;
        }
        for (int i = 0; i != getDes().length; ++i) {//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j) {//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1) {

                }
            }
        }
        int[] posFace = getPosFace1Or();
        if (posFace[0] != -1)
            return new ChoixJoueurForge(bassinAChoisir, 0, posFace[0], posFace[1]);

        return new ChoixJoueurForge(null, 0, 0, 0);
    }

    @Override
    Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals("Marteau") && !possedeCarte("Marteau"))//Au moins 1 marteau
                return carte;
            if (carte.getNom().equals("Coffre") && !possedeCarte("Coffre"))//Et un coffre
                return carte;
            if (carte.getNom().equals("Miroir Abyssal") && !possedeCarte("Miroir Abyssal"))
                return  carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        return carteAChoisir;
    }

    @Override
    boolean choisirActionSupplementaire(int numManche){
        return ((getOr() > 10 && numManche < 6) || getSoleil() > 3 || getLune() > 1);//Si on a assez de ressource pour refaire un tour
    }

    @Override
    int choisirRepartitionOrMarteau(int nbrOr){return 0;}//On met tout dans le marteau

    @Override
    List<Renfort> choisirRenforts(List renfortsUtilisables){
        return renfortsUtilisables;//On appelle tous les renforts
    }

    @Override
    int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil)
                    return i;//On cherche un résultat sur la face qui donne des soleils ou des lunes
            }
        }
        return 0;
    }

    @Override
    int choisirRessourceAPerdre(Face faceAChoix){
        return 0;
    }

    @Override
    int choisirDeBiche(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 0 : 1);
    }

    @Override
    void forgerFace(Face face){
        boolean aForge = false;
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    forgerDe(i, face, j);
                    aForge = true;
                }
            }
        }
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            forgerDe(0, face, 0);
    }

    @Override
    int choisirFace(List<Face> faces){
        Random random = new Random();
        return random.nextInt(faces.size());
    }



    @Override
    int[] choisirFaceARemplacePourMiroir(){
        int[] res = getPosFace1Or();
        Random random = new Random();
        if (res[0] != -1)
            return res;
        return new int[]{random.nextInt(2), random.nextInt(6)};
    }

    @Override
    int choisirFaceMiroir(Face[] tab){
        int qteLune = 0;
        int qteSoleil = 0;
        for (int i = 0; i < tab.length; i++){
            if (true){}
        }
    }
}

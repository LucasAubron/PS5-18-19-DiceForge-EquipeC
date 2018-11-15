package bot.NidoBot;

import diceForge.*;

import java.util.List;
import java.util.Random;

public class NidoBot extends Joueur {
    /*
    * stratégie globale:    forger max lune, soleil, pour acheter hydre, gorgogne, pince
    *
    */
    private int numeroManche = 0;//On est jamais mieux servi que par soi même
    private int maxPdg = 0;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer

    public NidoBot(){
        super(1, null, null);
    }
    public NidoBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
//        choixAction = new int[plateau.getJoueurs().size() == 3 ? 10 : 9];
    }
    public Stats getNbFaces(int numDe, De[] jeuDes, Ressource uneRess){
        Stats count = new Stats();
        for (Face face : jeuDes[numDe].getFaces())
            if (face.getRessource().length == 1 && face.getRessource()[0][0] instanceof  Soleil &&
            uneRess.getClass().getName().equals("diceForge.Soleil"))
                count.incrementNbSoleils();
            else if(face.getRessource().length == 1 && face.getRessource()[0][0] instanceof Lune &&
            uneRess.getClass().getName().equals("diceForge.Lune"))
                count.incrementNbLunes();
        return count;
    }

    public int getPosFaceQteMin(int numDe, De[] jeuDes, Ressource uneRess){ //recherche de min classique
        int min = 10000;
        int res = -1;
        Face[] faces = jeuDes[numDe].getFaces();
        for (int i = 0; i < faces.length; i++)
            if (faces[i].getRessource().length == 1)
                if (faces[i].getRessource()[0][0] instanceof Or
                    && uneRess.getClass().getName().equals("diceForge.Or")
                    && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource()[0][0] instanceof Lune
                        && uneRess.getClass().getName().equals("diceForge.Lune")
                        && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource()[0][0] instanceof Soleil
                        && uneRess.getClass().getName().equals("diceForge.Soleil")
                        && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
        return res;
    }

    public int getNbCarteType(List<Carte> cartes, Carte.Noms nom){
        return (int) cartes.stream()
                .filter(carte -> carte.getNom() == nom)
                .count();
    }

    public void setCartes(List<Carte> cartes){
        this.cartes = cartes;
    }

    @Override
    public Action choisirAction(int numManche){
        if (numManche < 6 && getOr() > 5)//Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches //forger de l'or au maximum.
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            else if (bassin.getFaces().get(0).getRessource().length == 1)
                for (int indexDe = 0; indexDe < getDes().length; indexDe++) //on parcourt tous les dés
                    if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Soleil)
                        if (getNbFaces(indexDe, getDes(), new Soleil(1)).getNbSoleils() <= 2) {
                            int posFace = getPosFaceQteMin(indexDe, getDes(), new Or(1));
                            if (posFace != -1)
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        } else {
                            //on a deja au moins 3 faces Soleil alors on remplace la face Soleil la moins valuable
                            //par une face Soleil plus chère
                            int posFace = getPosFaceQteMin(indexDe, getDes(), new Soleil(1));
                            if (bassin.getFaces().get(0).getRessource()[0][0].getQuantite() >
                                    getDes()[indexDe].getFaces()[posFace].getRessource()[0][0].getQuantite() )
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        }
                    else if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Lune)
                        if (getNbFaces(indexDe, getDes(), new Lune(1)).getNbLunes() <= 2) {
                            int posFace = getPosFaceQteMin(indexDe, getDes(), new Or(1));
                            if (posFace != -1)
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        } else {
                            //on a deja au moins 3 faces Lune alors on remplace la face Lune la moins valuable
                            //par une face Lune plus chère
                            int posFace = getPosFaceQteMin(indexDe, getDes(), new Lune(1));
//                            for (De unde: getDes())
                            System.out.println(getDes()[indexDe]);
                            System.out.println("posFace ==> " + posFace);
                            if (bassin.getFaces().get(0).getRessource()[0][0].getQuantite() >
                                    getDes()[indexDe].getFaces()[posFace].getRessource()[0][0].getQuantite() )
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        }
            }
        return new ChoixJoueurForge(null, 0, 0, 0);
    }



    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals(Carte.Noms.Coffre) && getNbCarteType(cartes, Carte.Noms.Coffre) < getPlateau().getJoueurs().size() - 1)//Et un coffre
                return carte;
            if (carte.getNom().equals(Carte.Noms.Hydre))
                return carte;
            if (carte.getNom().equals(Carte.Noms.Typhon))
                return carte;
            if (carte.getNom().equals(Carte.Noms.Meduse))
                return carte;
            if (carte.getNom().equals(Carte.Noms.Passeur))
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
    public int choisirRepartitionOrMarteau(int quantiteOr){
        Random random = new Random();
        return random.nextInt(2) == 1 ? 0 : 1;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        return getRenforts();//On appelle tous les renforts
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil) {
                    return i;//On cherche un résultat sur la face qui donne des soleils ou des lunes
                }
            }
        }
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirDeCyclope(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        boolean aForge = false;
        int[] posFace = getPosFace1Or();
        if(posFace[0] != -1) { //si on a trouvé une face 1 or sur un des dés)
            forgerDe(posFace[0], face, posFace[1]);
            aForge = true;
        }
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            forgerDe(0, face, getPosFaceQteMin(0, getDes(), new Lune(1)));

    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        for (int i = 0; i != faces.size(); ++i){
            for (Ressource[] ressources:faces.get(i).getRessource()){
                for(Ressource ressource:ressources){
                    if (ressource instanceof Soleil && ressource.getQuantite() > maxSoleil){
                        posMaxSoleil = i;
                        maxSoleil = ressource.getQuantite();
                    }
                    else if (ressource instanceof Lune && ressource.getQuantite() > maxLune){
                        posMaxLune = i;
                        maxLune = ressource.getQuantite();
                    }
                    else if (ressource instanceof Or && ressource.getQuantite() > maxOr){
                        posMaxOr = i;
                        maxOr = ressource.getQuantite();
                    }
                }
            }
        }
        if (posMaxSoleil != -1) return posMaxSoleil;
        if (posMaxLune != -1) return posMaxLune;
        if (posMaxOr != -1) return posMaxOr;
        return 0;
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        Random random = new Random();
        int choix = random.nextInt(choixJetonTriton.values().length);
        switch (choix){
            case 0:
                return choixJetonTriton.Rien;
            case 1:
                return choixJetonTriton.Or;
            case 2:
                return choixJetonTriton.Soleil;
            case 3:
                return choixJetonTriton.Lune;
        }
        throw new DiceForgeException("Bot","Impossible, utiliserJetonTriton ne renvoit rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere(){
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        return true;
    }
    @Override
    public String toString(){return "NidoBot";}
}

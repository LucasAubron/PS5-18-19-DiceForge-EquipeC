package bot.NidoBot;

import diceForge.*;

import java.util.List;

public class NidoBot extends Joueur {
    /*
    * stratégie globale:    forger max lune, soleil, pour acheter hydre, gorgogne, pince
    *
    */
    private int numeroManche = 0;//On est jamais mieux servi que par soi même
    private int maxPdg = 0;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer

//    public NidoBot(){}
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
        return res;
    }

    @Override
    public Action choisirAction(int numManche){
        if (numManche < 6 && getOr() > 5)//Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
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
                            if (bassin.getFaces().get(0).getRessource()[0][0].getQuantite() >
                                    getDes()[indexDe].getFaces()[posFace].getRessource()[0][0].getQuantite() )
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        }
            }
        return new ChoixJoueurForge(null, 0, 0, 0);
    }



    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        return null;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return true;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        return 1;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        return null;
    }

    @Override
    public int choisirRessource(Face face){
        return 1;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        return 1;
    }

    @Override
    public int choisirDeFaveurMineure(){
        return 1;
    }

    @Override
    public int choisirDeCyclope(){
        return 1;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return 1;
    }

    @Override
    public void forgerFace(Face face){
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        return 1;
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        return null;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        return true;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        return true;
    }
}

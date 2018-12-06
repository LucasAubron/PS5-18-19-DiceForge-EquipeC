package bot.NidoBot;

import diceForge.*;
import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
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
    }


    public List<Bassin> getBassinsAbordable() {
        List<Bassin> bassinAbordable = new ArrayList<>();//On créé la liste des bassins abordables
        for (Bassin bassin : getPlateau().getTemple().getSanctuaire())
            if (!bassin.getFaces().isEmpty() && bassin.getCout() <= getOr())//Si on peut ajouter ce bassin
                bassinAbordable.add(bassin);//On l'ajoute
        return bassinAbordable;
    }



    @Override
    public Action choisirAction(int numManche){
        //Si on est au début du jeu et que l'on a assez d'or, on forge
        if ( numManche < 3 && getOr() > 5 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        else if (numManche < 6 && getOr() > 5 && !getBassinsAbordable().isEmpty() &&
                NidoFunctions.haveSoleilsOuLunesBassins(getPlateau().getTemple().getSanctuaire()))
            return Action.FORGER;

        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty()){
            afficheur.NidoBotAfficheur("bassins empty in choisirFaceAForgerEtARemplacer");
            return new ChoixJoueurForge(null, 0, 0, 0);
        }
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 2 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches //forger de l'or au maximum.
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            else if (bassin.getFaces().get(0).getRessource().length == 1 && numManche >= 2)     //forger lune et soleil  pendant les 2 premiers tours 1face 3, or , 4 or, 1 lune
                // manche 1 7or alors faire
                // face lune sur dé ou il y a lune et face or achetées répartir sur les 2 dés cout 3+2+2
                //gere exception pas 7 or a chaque fois pour commencer
                /*
                * on a 13 or sur le dé maintenant
                * 13/12 un peu plus d'un or par lancé
                * 1.08*2 == 2.16 * nb joueurs (4) == moins de 9 or 8.64 en moy  //2 joueur comme 4 joueur
                * 
                * manche 2 forger 2soleil sur le dé ou y a deja un soleil
                *
                * macnche 3: 8 or en moyenne gagné par tour 9 or pour faire 6 + 3 => 2 lune sur le dé ou il y a lune+ 1 soleil sur dé ou il y a un soleil
                *
                * 1 dé ou : 1 lune, 1lune, 2lune, 2pdg, 1 or, 3/4or
                * 2 dé: 2soleil, 1sol, 1sol, 1 or, 1or, 3/4Or
                *
                * acheter marteau eventuellement
                * */
                //on parcourt tous les dés
                for (int indexDe = 0; indexDe < getDes().length; indexDe++)
                    if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Soleil)
                        // on replace la face Or la moins valuable si elle existe par la face Soleil du bassin
                        if (NidoFunctions.getNbFaces(indexDe, getDes(), new Soleil(1)).getNbSoleils() <= 2) {
                            int posFace = NidoFunctions.getPosFaceQteMin(indexDe, getDes(), new Or(1));
                            if (posFace != -1)
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        } else {
                            //on a deja au moins 3 faces Soleil alors on remplace la face Soleil la moins valuable
                            //par une face Soleil plus chère
                            int posFace = NidoFunctions.getPosFaceQteMin(indexDe, getDes(), new Soleil(1));
                            if (bassin.getFaces().get(0).getRessource()[0][0].getQuantite() >
                                    getDes()[indexDe].getFaces()[posFace].getRessource()[0][0].getQuantite() )
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        }
                    else if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Lune)
                        if (NidoFunctions.getNbFaces(indexDe, getDes(), new Lune(1)).getNbLunes() <= 2) {
                            int posFace = NidoFunctions.getPosFaceQteMin(indexDe, getDes(), new Or(1));
                            if (posFace != -1)
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        } else {
                            //on a deja au moins 3 faces Lune alors on remplace la face Lune la moins valuable
                            //par une face Lune plus chère
                            int posFace = NidoFunctions.getPosFaceQteMin(indexDe, getDes(), new Lune(1));
                            if (bassin.getFaces().get(0).getRessource()[0][0].getQuantite() >
                                    getDes()[indexDe].getFaces()[posFace].getRessource()[0][0].getQuantite() )
                                return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                        }
            }
        afficheur.NidoBotAfficheur("end of function choisirFaceAForgerEtARemplacer");
        return new ChoixJoueurForge(null, 0, 0, 0);
    }



    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals(Carte.Noms.Coffre) && NidoFunctions.getNbCarteType(cartes, Carte.Noms.Coffre) == 0)//Et un coffre
                // 4eme manche coffre + marteau faire action suppl qui coute 2soleils faire coffre + marteau, coffre + ancien, marteau, ancien
                return carte;
            if (carte.getNom().equals(Carte.Noms.Hydre))    //Miroir Abyss. et gérer alternatives tirage au hasard sur les iles.
                return carte;
            if (carte.getNom().equals(Carte.Noms.Typhon))   //gérer le choix de forgeage en amont.
                return carte;
            if (carte.getNom().equals(Carte.Noms.Meduse))
                return carte;
            if (carte.getNom().equals(Carte.Noms.Passeur)) //miroir abyssal
                return carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la moins chere
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
        int posFaceQteMin;
        int[] posFace = getPosFace1Or();
        if(posFace[0] != -1) { //si on a trouvé une face 1 or sur un des dés)
            forgerDe(posFace[0], face, posFace[1]);
            aForge = true;
        }

        posFaceQteMin = NidoFunctions.getPosFaceQteMin(0, getDes(), new Lune(1));
        if (posFaceQteMin == -1)
            posFaceQteMin = NidoFunctions.getPosFaceQteMin(0, getDes(), new Soleil(1));
        //else
            //posFaceQteMin = getPosFaceQteMin(0, getDes(), new Random().nextInt(6));
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            if (posFaceQteMin != -1)
                forgerDe(0, face, posFaceQteMin);
            else
                forgerDe(0, face, new Random().nextInt(6));

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

    /**
     * utile pour les bots autre que random
     * permet de chercher une face de base 1 or et de renvoyer sa position
     * @return un tableau = [numéro du dé, numéro de la face sur le dé en question]
     */
    public int[] getPosFace1Or(){
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        return true;
    }
    @Override
    public String toString(){return "NidoBot";}
}

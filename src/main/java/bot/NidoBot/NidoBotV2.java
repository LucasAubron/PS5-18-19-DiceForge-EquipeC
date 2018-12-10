package bot.NidoBot;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bot.NidoBot.NidoFunctions.*;
import static diceForge.Cartes.Carte.Noms.*;

public class NidoBotV2 extends Joueur {
    private int manche;
    private int mancheExploit;
    private int nombreDeJoueurs;

    public NidoBotV2() {
        super(1, null, null);
    }

    public NidoBotV2(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
        mancheExploit = 4;
        manche = 0;
    }

    private int nombreCartePossedee(Carte.Noms nom) {
        int compte = 0;
        for (Carte carte : getCartes())
            if (carte.getNom() == nom)
                compte++;
        return compte;
    }

    public List<Bassin> getBassinsAbordable() {
        List<Bassin> bassinAbordable = new ArrayList<>();//On créé la liste des bassins abordables
        for (Bassin bassin : getPlateau().getTemple().getSanctuaire())
            if (!bassin.getFaces().isEmpty() && bassin.getCout() <= getOr())//Si on peut ajouter ce bassin
                bassinAbordable.add(bassin);//On l'ajoute
        return bassinAbordable;
    }

    public int getPosFaceOrDuDe(Joueur joueur, int numDe, int qte) {
//        afficheur.NidoBotAfficheur("numDe ==> " + numDe);
        for (int i = 0; i != 6; i++) {
//            afficheur.NidoBotAfficheur("getRessources.length == " + joueur.getDe(numDe).getFace(i).getRessource().length);
            if (joueur.getDe(numDe).getFace(i).getTypeFace() == Face.typeFace.SIMPLE) {
                if (joueur.getDe(numDe).getFace(i).getRessource().getType() == Ressource.type.OR &&
                        joueur.getDe(numDe).getFace(i).getRessource().getQuantite() == qte) {
//                    afficheur.NidoBotAfficheur("gesPosFaceOrDuDe BP: 2");
                    return i;
                }
            }
        }
//        afficheur.NidoBotAfficheur("return - 1");
        return -1;
    }

    @Override
    public Action choisirAction() {
        //Si on est au début du jeu et que l'on a assez d'or, on forge
        this.manche++;
        if (this.manche == 1)
            nombreDeJoueurs = getPlateau().getJoueurs().size();
        if (this.manche == 1 && getOr() >= 3 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        if (this.manche == 2 && getOr() >= 6 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        if (this.manche == 3 && getOr() >= 8 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;
    }


    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins) {
        Bassin bassinAChoisir = null;
        List<Bassin.typeBassin> ordrePrioBassin = new ArrayList<>();
        int numFaceAChoisirDansBassin = 0, numDeSurLequelForger = -1, numFaceARemplacerSurLeDe = -1;

        if (this.manche == 1) { //si on est dans les deux premières manches
            // on priorise l'achat d'or
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceLune);
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceOr);
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceOr);
            // on add dans le sens inverse de priorité
        }
        //        } else if (this.manche <= 3) {
////            System.out.println("enteres in manche 2 or 3");
//            BassinType[] listBassins = {
//                    new BassinType(8, Ressource.type.SOLEIL),
//                    new BassinType(3, Ressource.type.SOLEIL),
//                    new BassinType(6, Ressource.type.LUNE),
//                    new BassinType(2, Ressource.type.LUNE),
//                    new BassinType(3, Ressource.type.OR),
//                    new BassinType(2, Ressource.type.OR)};
//            }
        else if (this.manche <= 3) {
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceOr);
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceOr);
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceLune);
            ordrePrioBassin.add(Bassin.typeBassin.Cout6); //Lune
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceSoleil);
            ordrePrioBassin.add(Bassin.typeBassin.Cout8FaceSoleil);
        }
        if (bassins.get(0).estLeBassin(Bassin.typeBassin.Bouclier)) {
            bassinAChoisir = bassins.get(0);
            for (int i = 0; i < bassinAChoisir.getFaces().size(); i++) {
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.SOLEIL)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.LUNE)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.PDG)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
                if (bassinAChoisir.getFace(i).getRessource().estDuType(Ressource.type.OR)) {
                    numFaceAChoisirDansBassin = i;
                    break;
                }
            }
        }
        for (Bassin.typeBassin bassinPrio : ordrePrioBassin)
            for (Bassin bassin : bassins)
                if (bassin.estLeBassin(bassinPrio) && bassinAChoisir == null)
                    bassinAChoisir = bassin;

        if (bassinAChoisir == null) // Sinon on achète la face la plus chère disponible !
            bassinAChoisir = getBassinLePlusCher(bassins);

        if (bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout8FaceSoleil)
                || bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout3FaceSoleil)) {
            numDeSurLequelForger = 1;
            numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(1));
        } else if (bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout6)
                || bassinAChoisir.estLeBassin(Bassin.typeBassin.Cout2FaceLune)) {
            numDeSurLequelForger = 0;
            numFaceARemplacerSurLeDe = getPosDeLaFaceLaPlusFaible(getDe(0));
        }
        if (numDeSurLequelForger == -1)
            numDeSurLequelForger = new Random().nextInt(2);
        if (numFaceARemplacerSurLeDe == -1)
            numFaceARemplacerSurLeDe = new Random().nextInt(6);

        return new ChoixJoueurForge(
                bassinAChoisir, numFaceAChoisirDansBassin,
                numDeSurLequelForger, numFaceARemplacerSurLeDe);
    }

    /**
     * manche 1 7or alors faire
     * face lune sur dé ou il y a lune et face or achetées répartir sur les 2 dés cout 3+2+2
     * gere exception pas 7 or a chaque fois pour commencer
     * forger lune et soleil  pendant les 2 premiers tours 1face 3, or , 4 or, 1 lune
     * 1re manche 3Or + 4or + 1Lune
     */
//
    /* on a 13 or sur le dé maintenant
     * 13/12 un peu plus d'un or par lancé
     * 1.08*2 == 2.16 * nb joueurs (4) == moins de 9 or 8.64 en moy  //2 joueur comme 4 joueur
     * manche 2 forger 2soleil sur le dé ou y a deja un soleil
     */

    /*
     * manche 3: 8 or en moyenne gagné par tour 9 or pour faire 6 + 3 => MANCHE 3 ressemble bcp a manche 2.
     * 2 lune sur le dé ou il y a lune+ 1 soleil sur dé ou il y a un soleil
     *
     * 1 dé ou : 1 lune, 1lune, 2lune, 2pdg, 1 or, 3/4or
     * 2 dé: 2soleil, 1sol, 1sol, 1 or, 1or, 3/4Or
     *
     * acheter marteau eventuellement
     */
//        }
//    }
    @Override
    public Carte choisirCarte(List<Carte> cartes) {
        Carte carteAChoisir = cartes.get(0);
        for (Carte carte : cartes) {
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 1)
                return carte;
            //on prend tous les marteau si 2 joueurs
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 2 && nombreDeJoueurs == 2)
                return carte;
            if (carte.getNom() == Ours && nombreDeJoueurs == 4)
                return carte;
            if (carte.getNom() == Ancien && nombreCartePossedee(Ancien) < 1 && manche <= 5)
                return carte;
            if (carte.getNom() == Bouclier && nombreCartePossedee(Bouclier) == 0 && manche <= 6)
                return carte;
            if (carte.getNom() == CasqueDinvisibilite && !possedeCarte(CasqueDinvisibilite) && manche <= 4)
                return carte;
//            if (carte.getNom() == BateauCeleste && nombreCartePossedee(BateauCeleste) == 0 && manche <= 4)
//                return carte;
            if (manche >= 5 && (carte.getNom() == Hydre || carte.getNom() == Typhon))
                return carte;
            if (manche >= 5 && (carte.getNom() == Meduse))
                return carte;
            if (manche >= 5 && (carte.getNom() == Passeur))
                return carte;
//            if (manche >= 5 && (carte.getNom() == MiroirAbyssal))
//                return carte;
            if (manche >= 5 && (carte.getNom() == Sphinx))
                return carte;
//            if (manche >= 5 && (carte.getNom() == Cancer))
//                return carte;
//            if (manche >=5 && carte.getNom() == Ancien)
//                return carte;

            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
        }
        return carteAChoisir;
    }

    @Override
    public boolean choisirActionSupplementaire() {
        if ((getOr() > 10 && this.manche < mancheExploit && !getBassinsAbordable().isEmpty()) ||
                (this.manche >= mancheExploit && (getSoleil() > 3 || getLune() > 1) && !cartesAbordables(this, getPlateau()).isEmpty())) {
            this.manche--;
            return true;
        }
        //Si on a assez de ressource pour refaire un tour
        return false;
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int quantiteOr) {
        if (manche < mancheExploit || getOr() < 3 * nombreCartePossedee(Ancien) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables) {
        if (manche < mancheExploit) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            int nombreAncien = nombreCartePossedee(Ancien);
            for (int i = 0; i < nombreAncien; i++)
                renfortsUtilisables.remove(Ancien);
        }
        return renfortsUtilisables;
    }

    @Override
    public Ressource choisirRessourceFaceAchoix(Ressource[] ressources) {
        //intressant a modif pour avoir plus de stats de victoire
        for (int i = 0; i != ressources.length; ++i) {
            //On cherche un résultat sur la face qui donne des soleils ou des lunes
            if (ressources[i].getType() == Ressource.type.LUNE || ressources[i].getType() == Ressource.type.SOLEIL) {
                return ressources[i];
            }
        }
        return ressources[0];
    }

    @Override
    public Ressource choisirRessourceAPerdre(Ressource[] ressources) {
        return ressources[0];
    }

    @Override
    public int choisirDeFaveurMineure() {
        Random random = new Random();
        if (haveFaceType(getDe(0), new Ressource(1, Ressource.type.SOLEIL)))
            return 0;
        if (haveFaceType(getDe(1), new Ressource(1, Ressource.type.SOLEIL)))
            return 1;
        if (haveFaceType(getDe(0), new Ressource(1, Ressource.type.LUNE)))
            return 0;
        if (haveFaceType(getDe(1), new Ressource(1, Ressource.type.LUNE)))
            return 1;
        return random.nextInt(2);
    }

    @Override
    public int choisirDeCyclope() {
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public int[] choisirOuForgerFaceSpeciale(Face face) { //BateauCeleste, X3, tous spéciales sauf bouclier.
        boolean aForge = false;
        int posFaceQteMin = -1;
        int numDeLune = -1;
        int[] resultat = new int[2];

        if (face.getTypeFace() == Face.typeFace.X3) {
            for (int i = 0; i < getDes().length; i++)
                for (int j = 0; j < 6; j++)
                    if (getDe(i).getFace(j).getTypeFace() == Face.typeFace.SIMPLE &&
                            getDe(i).getFace(j).getRessource().getType() == Ressource.type.LUNE) {
                        numDeLune = i;
                        break;
                    }
            if (numDeLune == -1) {
                int tab[] = {1, 3, 4};
                int res = -1;
                for (int k = 0; k < tab.length && res == -1; k++)
                    for (int i = 0; i < getDes().length; i++) {
                        res = getPosFaceOrDuDe(this, i, tab[k]);
                        if (res != -1)
                            numDeLune = i;
                    }
            }
            if (numDeLune == -1)
                numDeLune = new Random().nextInt(2) == 0 ? 1 : 0;

//            afficheur.NidoBotAfficheur("numDeLune == " + numDeLune);
            posFaceQteMin = getPosFaceOrDuDe(this, numDeLune, 1);
            if (posFaceQteMin == -1) {
                posFaceQteMin = getPosFaceQteMin(numDeLune, getDes(), new Ressource(1, Ressource.type.LUNE));
            }
//            forgerDe(numDeLune, face, posFaceQteMin);
            resultat[0] = numDeLune;
            resultat[1] = posFaceQteMin;
            aForge = true;
        } else if (face.getTypeFace() == Face.typeFace.BOUCLIER) {
            switch (face.getRessource().getType()) {
                case SOLEIL:
                    resultat[0] = 0;
                    break;
                case LUNE:
                    resultat[0] = 1;
                    break;
                default:
                    resultat[0] = new Random().nextInt(2);
            }
        } else {
            resultat = getPosFace1Or();
            //si on a trouvé une face 1 or sur un des dés)
            if (resultat[0] != -1) {
                aForge = true;
            }

            //pas de face 1Or sur les 2 Des
            if (resultat[0] == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Ressource(1, Ressource.type.OR));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Ressource(1, Ressource.type.OR));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Ressource(1, Ressource.type.LUNE));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Ressource(1, Ressource.type.LUNE));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Ressource(1, Ressource.type.SOLEIL));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Ressource(1, Ressource.type.SOLEIL));
        }

        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            if (posFaceQteMin != -1) {
                resultat[0] = 0;
                resultat[1] = posFaceQteMin;
            } else {
                resultat[0] = 0;
                resultat[1] = new Random().nextInt(6);
            }
        return resultat;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces) {
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        Ressource ressource;
        for (int i = 0; i != faces.size(); ++i) {
            ressource = faces.get(i).getRessource();
            if (faces.get(i).getTypeFace() == Face.typeFace.SIMPLE
                    && ressource.getType() == Ressource.type.SOLEIL
                    && ressource.getQuantite() > maxSoleil) {
                posMaxSoleil = i;
                maxSoleil = ressource.getQuantite();
            } else if (faces.get(i).getTypeFace() == Face.typeFace.SIMPLE
                    && ressource.getType() == Ressource.type.LUNE
                    && ressource.getQuantite() > maxLune) {
                posMaxLune = i;
                maxLune = ressource.getQuantite();
            } else if (faces.get(i).getTypeFace() == Face.typeFace.SIMPLE
                    && ressource.getType() == Ressource.type.OR
                    && ressource.getQuantite() > maxOr) {
                posMaxOr = i;
                maxOr = ressource.getQuantite();
            }
        }
        if (posMaxSoleil != -1) return faces.get(posMaxSoleil);
        if (posMaxLune != -1) return faces.get(posMaxLune);
        if (posMaxOr != -1) return faces.get(posMaxOr);
        return faces.get(new Random().nextInt(faces.size()));
    }

    @Override
    public choixJetonTriton utiliserJetonTriton() {
        Random random = new Random();
//        int choix = random.nextInt(choixJetonTriton.values().length);
//        choix = 2;
//        switch (choix) {
//            case 0:
//                return choixJetonTriton.Rien;
//            case 1:
//                return choixJetonTriton.Or;
//            case 2:
//                return choixJetonTriton.Soleil;
//            case 3:
//                return choixJetonTriton.Lune;
//        }
        if (getSoleil() <= 4)
            return choixJetonTriton.Soleil;
        else if (getLune() <= 4)
            return choixJetonTriton.Lune;
        else if (getOr() == 0)
            return choixJetonTriton.Or;
        else
            return choixJetonTriton.Rien;
        //throw new DiceForgeException("Bot", "Impossible, utiliserJetonTriton ne renvoit rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere() {
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirPdgPlutotQueRessource(Ressource ressource) {
        return true;
    }

    /**
     * utile pour les bots autre que random
     * permet de chercher une face de base 1 or et de renvoyer sa position
     *
     * @return un tableau = [numéro du dé, numéro de la face sur le dé en question]
     */
    public int[] getPosFace1Or() {
        for (int i = 0; i != getDes().length; ++i) {//On parcours tous les dés
            for (int j = 0; j != getDe(i).getFaces().length; ++j) {//Toutes les faces
                if (getDes()[i].getFace(j).getTypeFace() == Face.typeFace.SIMPLE
                        && getDe(i).getFace(j).getRessource().getType() == Ressource.type.OR
                        && getDe(i).getFace(j).getRessource().getQuantite() == 1) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
    }

    private int getPosDeLaFaceLaPlusFaible(De de) {
        Random random = new Random();
        int choixPos = -1;
        for (int i = 0; i < de.getFaces().length; i++) {
            if (de.getFace(i).getTypeFace() == Face.typeFace.SIMPLE) {
                if (de.getFace(i).getRessource().estDuType(Ressource.type.OR)
                        && de.getFace(i).getRessource().getQuantite() == 1)
                    choixPos = i;
                if (de.getFace(i).getRessource().estDuType(Ressource.type.LUNE)
                        && de.getFace(i).getRessource().getQuantite() == 1 && choixPos == -1)
                    choixPos = i;
                if (de.getFace(i).getRessource().estDuType(Ressource.type.SOLEIL)
                        && de.getFace(i).getRessource().getQuantite() == 1 && choixPos == -1)
                    choixPos = i;
            }
        }
        if (choixPos == -1)
            choixPos = random.nextInt(6);
        return choixPos;
    }


    private Bassin getBassinLePlusCher(List<Bassin> bassins) {
        int maxCout = 0;
        Bassin bassinLePlusCher = null;
        for (Bassin bassin : bassins) {
            if (bassin.getCout() > maxCout) {
                maxCout = bassin.getCout();
                bassinLePlusCher = bassin;
            }
        }
        return bassinLePlusCher;
    }

    @Override
    public String toString() {
        return "NidoBot";
    }
}

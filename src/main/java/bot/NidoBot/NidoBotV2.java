package bot.NidoBot;

import com.sun.org.apache.xpath.internal.operations.Or;
import diceForge.*;
import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.ChoixJoueurForge;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.Afficheur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bot.NidoBot.NidoFunctions.*;

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
        int numManche = this.manche;
        Bassin bassin = getPlateau().getTemple().getSanctuaire()[0];
        int[] posFace = new int[2];
        int posFaceInt = -1;
        int j = -1;
        if (bassins.isEmpty()) {
            afficheur.NidoBotAfficheur("bassins empty in choisirFaceAForgerEtARemplacer");
            return new ChoixJoueurForge(null, 0, 0, 0);
        }
        /**
         * manche 1 7or alors faire
         * face lune sur dé ou il y a lune et face or achetées répartir sur les 2 dés cout 3+2+2
         * gere exception pas 7 or a chaque fois pour commencer
         * forger lune et soleil  pendant les 2 premiers tours 1face 3, or , 4 or, 1 lune
         * 1re manche 3Or + 4or + 1Lune
         */
        //afficheur.NidoBotAfficheur("attribut manche de NidoBot ==> " + manche);
        if (numManche == 1) {
            BassinType[] listBassins = {
                    new BassinType(3, "Or"),
                    new BassinType(2, "Or"),
                    new BassinType(2, "Lune")};
            int i = 0;
            while (i < listBassins.length && (bassin = NidoFunctions.trouveBassinCout(bassins, listBassins[i].getNum(), listBassins[i].getNom())) == null)
                i++;
            //afficheur.NidoBotAfficheur("Nidobot: i == " + i);
            //afficheur.NidoBotAfficheur("bassin == " + bassin.toString());
            if (i == 3)
                bassin = getPlateau().getTemple().getSanctuaire()[0];

            if (bassin != null && !bassin.getFaces().isEmpty() && bassin.getFace(0).getRessource().getType() == Ressource.type.LUNE) {
                for (j = 0; j < getDes().length; j++)
                    if (haveFaceType(getDe(j), new Ressource(1, Ressource.type.LUNE)))
                        break;
                posFaceInt = getPosFaceOrDuDe(this, j, 1);
            } else if (bassin != null && !bassin.getFaces().isEmpty() && bassin.getFace(0).getRessource().getType() == Ressource.type.OR) {
                if ((getPosFaceOrDuDe(this, 0, 3) != -1 || getPosFaceOrDuDe(this, 0, 4) != -1)
                        && getPosFaceOrDuDe(this, 1, 3) == -1
                        && getPosFaceOrDuDe(this, 1, 4) == -1) {
                    posFaceInt = getPosFaceOrDuDe(this, 1, 1);
                    j = 1;
                } else if ((getPosFaceOrDuDe(this, 1, 3) != -1 || getPosFaceOrDuDe(this, 1, 4) != -1)
                        && getPosFaceOrDuDe(this, 0, 3) == -1
                        && getPosFaceOrDuDe(this, 0, 4) == -1) {
                    posFaceInt = getPosFaceOrDuDe(this, 0, 1);
                    j = 0;
                } else if ((getPosFaceOrDuDe(this, 0, 3) != -1 || getPosFaceOrDuDe(this, 0, 4) != -1) &&
                        (getPosFaceOrDuDe(this, 1, 3) != -1 || getPosFaceOrDuDe(this, 1, 4) != -1)) {
                    j = new Random().nextInt(2) == 0 ? 1 : 0;
                    posFaceInt = getPosFaceOrDuDe(this, j, 1);
                } else {
                    //les 2 dés ne contiennent pas de face 3/4Or, donc on remplace une face 1Or sur le 1er
                    posFaceInt = getPosFaceOrDuDe(this, 0, 1);
                    j = 0;
                    if (posFaceInt == -1) {
                        posFaceInt = getPosFaceOrDuDe(this, 1, 1);
                        j = 1;
                    }
                    if (posFaceInt == -1) {
                        posFaceInt = getPosFaceOrDuDe(this, 0, 3);
                        j = 0;
                    }
                    if (posFaceInt == -1) {
                        posFaceInt = getPosFaceOrDuDe(this, 1, 3);
                        j = 1;
                    }
                    if (posFaceInt == -1) {
                        posFaceInt = getPosFaceOrDuDe(this, 0, 4);
                        j = 0;
                    }
                    if (posFaceInt == -1) {
                        posFaceInt = getPosFaceOrDuDe(this, 1, 4);
                        j = 1;
                    }
                }
                afficheur.NidoBotAfficheur("posFaceInt == " + posFaceInt);
            }
            if (posFaceInt != -1) {
//                System.out.println("yo first manche return!.");
//                System.out.println("posfaceInt == " + posFaceInt + " j == " + j);
                return new ChoixJoueurForge(bassin, 0, j, posFaceInt);
            }
            /* on a 13 or sur le dé maintenant
             * 13/12 un peu plus d'un or par lancé
             * 1.08*2 == 2.16 * nb joueurs (4) == moins de 9 or 8.64 en moy  //2 joueur comme 4 joueur
             * manche 2 forger 2soleil sur le dé ou y a deja un soleil
             */
        } else if (numManche <= 3) {
//            System.out.println("enteres in manche 2 or 3");
            BassinType[] listBassins = {
                    new BassinType(8, "Soleil"),
                    new BassinType(3, "Soleil"),
                    new BassinType(6, "Lune"),
                    new BassinType(2, "Lune"),
                    new BassinType(3, "Or"),
                    new BassinType(2, "Or")};
            int i = 0;
            while (i < listBassins.length && (bassin = NidoFunctions.trouveBassinCout(bassins, listBassins[i].getNum(), listBassins[i].getNom())) == null)
                i++;

            if (i == 6)
                bassin = getPlateau().getTemple().getSanctuaire()[0];

//            System.out.println("bassin is size == " + bassin.getFaces().size());
            if (bassin != null && !bassin.getFaces().isEmpty() && bassin.getFace(0).getRessource().getType() == Ressource.type.SOLEIL) {
                for (j = 0; j < getDes().length; j++)
                    if (haveFaceType(getDe(j), new Ressource(1, Ressource.type.SOLEIL)))
                        break;
                posFaceInt = getPosFaceOrDuDe(this, j, 1);
            } else if (bassin != null && !bassin.getFaces().isEmpty() && bassin.getFace(0).getRessource().getType() == Ressource.type.LUNE) {
                for (j = 0; j < getDes().length; j++)
                    if (haveFaceType(getDe(j), new Ressource(1, Ressource.type.LUNE)))
                        break;
                posFaceInt = getPosFaceOrDuDe(this, j, 1);
            } else if (bassin != null && !bassin.getFaces().isEmpty() && bassin.getFace(0).getRessource().getType() == Ressource.type.OR) {
                if ((getPosFaceOrDuDe(this, 0, 3) != -1 || getPosFaceOrDuDe(this, 0, 4) != -1) &&
                (getPosFaceOrDuDe(this, 1, 3) != -1 || getPosFaceOrDuDe(this, 1, 4) != -1))
                    j = new Random().nextInt(2) == 1 ? 1 : 0;
                else if ((getPosFaceOrDuDe(this, 0, 3) != -1 || getPosFaceOrDuDe(this, 0, 4) != -1) &&
                (getPosFaceOrDuDe(this, 1, 3) == -1 && getPosFaceOrDuDe(this, 1, 4) == -1))
                    j = 1;
                else if ((getPosFaceOrDuDe(this, 0, 3) == -1 && getPosFaceOrDuDe(this, 0, 4) == -1) &&
                (getPosFaceOrDuDe(this, 1, 3) != -1 || getPosFaceOrDuDe(this, 1, 4) != -1))
                    j = 0;
                else if ((getPosFaceOrDuDe(this, 0, 3) == -1 && getPosFaceOrDuDe(this, 0, 4) == -1) &&
                (getPosFaceOrDuDe(this, 1, 3) == -1 && getPosFaceOrDuDe(this, 1, 4) == -1))
                    j = new Random().nextInt(2) == 1 ? 1 : 0;

                posFaceInt = getPosFaceOrDuDe(this, j, 1);
//                System.out.println("yo BP 2: nummanche == " + numManche + " j == " + j + " posfaceInt == " + posFaceInt);
                if (posFaceInt == -1) {
                    posFaceInt = getPosFaceOrDuDe(this, j, 3);
                }
                if (posFaceInt == -1) {
                    posFaceInt = getPosFaceOrDuDe(this, j, 4);
                }
            }

            if (posFaceInt != -1) {
//                System.out.println("yo return nummanche == " + numManche + " j == " + j);
                return new ChoixJoueurForge(bassin, 0, j, posFaceInt);
            }
            /*
             * manche 3: 8 or en moyenne gagné par tour 9 or pour faire 6 + 3 => MANCHE 3 ressemble bcp a manche 2.
             * 2 lune sur le dé ou il y a lune+ 1 soleil sur dé ou il y a un soleil
             *
             * 1 dé ou : 1 lune, 1lune, 2lune, 2pdg, 1 or, 3/4or
             * 2 dé: 2soleil, 1sol, 1sol, 1 or, 1or, 3/4Or
             *
             * acheter marteau eventuellement
             */
        }


        afficheur.NidoBotAfficheur("end of function choisirFaceAForgerEtARemplacer");
//        System.out.println("end of function");
        return new ChoixJoueurForge(null, 0, 0, 0);
    }


    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche) {
        Carte carteAChoisir = cartes.get(0);
        for (Carte carte : cartes) {
//            switch (numManche) {
//                case 1:
//                case 2:
//                case 3:
//                    // 3 1ere manches si pas forge alors exploit
//                case 4:
//                    // 4eme manche coffre + marteau faire action suppl qui coute 2soleils faire coffre + marteau, coffre + ancien, marteau, ancien
//                case 5:
//                    if (!possedeCarte(Carte.Noms.Coffre) && carte.getNom().equals(Carte.Noms.Coffre))
//                        return carte;
//                    if (!possedeCarte(Carte.Noms.Ancien) && carte.getNom().equals(Carte.Noms.Ancien))
//                        return carte;
//                    if (!possedeCarte(Carte.Noms.Marteau) && carte.getNom().equals(Carte.Noms.Marteau))
//                        return carte;
//                    if (!possedeCarte(Carte.Noms.CasqueDinvisibilite) && carte.getNom().equals(Carte.Noms.CasqueDinvisibilite))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Hydre))    //Miroir Abyss. et gérer alternatives tirage au hasard sur les iles.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Typhon))   //gérer le choix de forgeage en amont.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Meduse))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Passeur))
//                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.MiroirAbyssal))
////                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.Coffre))
////                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Hibou))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Ours))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Biche))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Marteau))
//                        return carte;
//
//                case 6:
//                    if (!possedeCarte(Carte.Noms.Marteau) && carte.getNom().equals(Carte.Noms.Marteau))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Hydre))    //Miroir Abyss. et gérer alternatives tirage au hasard sur les iles.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Typhon))   //gérer le choix de forgeage en amont.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Meduse))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Passeur)) //miroir abyssal
//                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.MiroirAbyssal))
////                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.HerbesFolles))
////                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.))
//                    break;
//                case 7:
//                case 8:
//                case 9:
//                case 10:
//                    if (carte.getNom().equals(Carte.Noms.Hydre))    //Miroir Abyss. et gérer alternatives tirage au hasard sur les iles.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Typhon))   //gérer le choix de forgeage en amont.
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Meduse))
//                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.Passeur))
//                        return carte;
////                    if (carte.getNom().equals(Carte.Noms.MiroirAbyssal))
////                        return carte;
//                    if (carte.getNom().equals(Carte.Noms.HerbesFolles))
//                        return carte;
//                    break;
//
//            }
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 1)
                return carte;
            //on prend tous les marteau si 2 joueurs
            if (carte.getNom() == Marteau && nombreCartePossedee(Marteau) < 2 && nombreDeJoueurs == 2)
                return carte;
            if (carte.getNom() == Ours && nombreDeJoueurs == 4)
                return carte;
            if (carte.getNom() == Ancien && nombreCartePossedee(Ancien) < 1 && manche <= 5)
                return carte;
            if (carte.getNom() == CasqueDinvisibilite && !possedeCarte(CasqueDinvisibilite) && manche <= 4)
                return carte;
//            if (carte.getNom() == BateauCeleste && nombreCartePossedee(BateauCeleste) == 0 && manche <= 4)
//                return carte;
            if (carte.getNom() == Bouclier && nombreCartePossedee(Bouclier) == 0 && manche <= 5)
                return carte;
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
    public boolean choisirActionSupplementaire(int numManche) {
        if ((getOr() > 10 && numManche < mancheExploit && !getBassinsAbordable().isEmpty()) ||
        (numManche >= mancheExploit && (getSoleil() > 3 || getLune() > 1) && !cartesAbordables(this, getPlateau()).isEmpty())) {
            this.manche--;
            return true;
        }
        //Si on a assez de ressource pour refaire un tour
        return false;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr) {
        if (manche < mancheExploit || getOr() < 3 * nombreCartePossedee(Ancien) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renfortsUtilisables) {
        if (manche < mancheExploit) {//tant qu'on a pas fini de forger nos dés on préfère garder l'or
            int nombreAncien = nombreCartePossedee(Carte.Noms.Ancien);
            for (int i = 0; i < nombreAncien; i++)
                renfortsUtilisables.remove(Ancien);
        }
        return renfortsUtilisables;
    }

    @Override
    public int choisirRessource(Face faceAChoix) {
        for (int i = 0; i != faceAChoix.getRessource().length; ++i) {
            for (Ressource ressource : faceAChoix.getRessource()[i]) {
                if (ressource instanceof Lune || ressource instanceof Soleil) {
                    return i;//On cherche un résultat sur la face qui donne des soleils ou des lunes
                }
            }
        }
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face face) {
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure() {
        Random random = new Random();
        if (haveFaceType(getDe(0), new Soleil(1)))
            return 0;
        if (haveFaceType(getDe(1), new Soleil(1)))
            return 1;
        if (haveFaceType(getDe(0), new Lune(1)))
            return 0;
        if (haveFaceType(getDe(1), new Lune(1)))
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
    public void forgerFace(Face face) { //BateauCeleste, X3, tous spéciales sauf bouclier.
        boolean aForge = false;
        int posFaceQteMin = -1;
        int numDeLune = -1;

        if (face instanceof FaceX3) {
            for (int i = 0; i < getDes().length; i++)
                for (int j = 0; j < 6; j++)
                    if (getDe(i).getFace(j).getRessource().length == 1 &&
                            getDe(i).getFace(j).getRessource()[0][0] instanceof Lune) {
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


            for (int i = 0; i < getDes().length; i++) {


            }
            afficheur.NidoBotAfficheur("numDeLune == " + numDeLune);
            posFaceQteMin = getPosFaceOrDuDe(this, numDeLune, 1);
            if (posFaceQteMin == -1) {
                posFaceQteMin = getPosFaceQteMin(numDeLune, getDes(), new Lune(1));
            }
            forgerDe(numDeLune, face, posFaceQteMin);
            aForge = true;
        } else {
            int[] posFace = getPosFace1Or();
            //si on a trouvé une face 1 or sur un des dés)
            if (posFace[0] != -1) {
                forgerDe(posFace[0], face, posFace[1]);
                aForge = true;
            }

            //pas de face 1Or sur les 2 Des
            if (posFace[0] == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Or(1));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Or(1));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Lune(1));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Lune(1));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(0, getDes(), new Soleil(1));
            if (posFaceQteMin == -1)
                posFaceQteMin = getPosFaceQteMin(1, getDes(), new Soleil(1));
        }

        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            if (posFaceQteMin != -1)
                forgerDe(0, face, posFaceQteMin);
            else
                forgerDe(0, face, new Random().nextInt(6));

    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces) {
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        for (int i = 0; i != faces.size(); ++i) {
            for (Ressource[] ressources : faces.get(i).getRessource()) {
                for (Ressource ressource : ressources) {
                    if (ressource instanceof Soleil && ressource.getQuantite() > maxSoleil) {
                        posMaxSoleil = i;
                        maxSoleil = ressource.getQuantite();
                    } else if (ressource instanceof Lune && ressource.getQuantite() > maxLune) {
                        posMaxLune = i;
                        maxLune = ressource.getQuantite();
                    } else if (ressource instanceof Or && ressource.getQuantite() > maxOr) {
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
        else if (getLune() <=4)
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
            for (int j = 0; j != getDes()[i].getFaces().length; ++j) {//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
    }

    @Override
    public String toString() {
        return "NidoBot";
    }
}

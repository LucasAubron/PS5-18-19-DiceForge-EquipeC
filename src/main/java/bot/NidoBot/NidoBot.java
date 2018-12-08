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
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NidoBot extends Joueur {
    /*
     * stratégie globale:    forger max lune, soleil, pour acheter hydre, gorgogne, pince
     *
     */
    private int numeroManche;//On est jamais mieux servi que par soi même
    private int maxPdg;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer

    public NidoBot() {
        super(1, null, null);
    }

    public NidoBot(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
        this.numeroManche = 0;
        this.maxPdg = 0;
    }

    public List<Bassin> getBassinsAbordable() {
        List<Bassin> bassinAbordable = new ArrayList<>();//On créé la liste des bassins abordables
        for (Bassin bassin : getPlateau().getTemple().getSanctuaire())
            if (!bassin.getFaces().isEmpty() && bassin.getCout() <= getOr())//Si on peut ajouter ce bassin
                bassinAbordable.add(bassin);//On l'ajoute
        return bassinAbordable;
    }

    @Override
    public Action choisirAction() {
        this.numeroManche++;
        //Si on est au début du jeu et que l'on a assez d'or, on forge
        if (this.numeroManche == 1 && getOr() >= 4 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        if (this.numeroManche <= 3 && getOr() > 5 && !getBassinsAbordable().isEmpty())
            return Action.FORGER;
        else if (this.numeroManche < 6 && getOr() >= 8 && !getBassinsAbordable().isEmpty() &&
                NidoFunctions.haveSoleilsOuLunesBassins(getPlateau().getTemple().getSanctuaire()))
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;
    }


    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins) {
        Bassin bassinAChoisir = null;
        List<Bassin.typeBassin> ordrePrioBassin = new ArrayList<>();
        int numFaceAChoisirDansBassin = 0, numDeSurLequelForger = -1 , numFaceARemplacerSurLeDe = -1;

        if (this.numeroManche == 1) { //si on est dans les deux premières manches
            // on priorise l'achat d'or
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceOr);
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceOr);
            // on add dans le sens inverse de priorité
        }
        if (this.numeroManche >= 2) {
            ordrePrioBassin.add(Bassin.typeBassin.Cout2FaceLune);
            ordrePrioBassin.add(Bassin.typeBassin.Cout6); //Lune
            ordrePrioBassin.add(Bassin.typeBassin.Cout3FaceSoleil);
            ordrePrioBassin.add(Bassin.typeBassin.Cout8FaceSoleil);
        }
        if (bassins.get(0).estLeBassin(Bassin.typeBassin.Bouclier))
            bassinAChoisir = bassins.get(0);
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


    @Override
    public Carte choisirCarte(List<Carte> cartes) {
        Carte carteAChoisir = null;
        for (Carte carte : cartes) {
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
    public boolean choisirActionSupplementaire() {
        if ((getOr() > 10 && this.numeroManche < 6) || getSoleil() > 3 || getLune() > 1) {
            //Si on a assez de ressource pour refaire un tour
            this.numeroManche--;
            return true;
        }
        return false;
    }

    @Override
    public int choisirOrQueLeMarteauNePrendPas(int quantiteOr) {
        Random random = new Random();
        return random.nextInt(2) == 1 ? 0 : 1;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts) {
        return getRenforts();//On appelle tous les renforts
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
    public int[] choisirOuForgerFaceSpeciale(Face face) {
        boolean aForge = false;
        int posFaceQteMin;
        int[] resultat = new int[2];
        resultat = getPosFace1Or();
        if (resultat[0] != -1) { //si on a trouvé une face 1 or sur un des dés)
//            forgerDe(posFace[0], face, posFace[1]);
            aForge = true;
        }
        posFaceQteMin = NidoFunctions.getPosFaceQteMin(0, getDes(), new Ressource(1, Ressource.type.LUNE));
        if (posFaceQteMin == -1)
            posFaceQteMin = NidoFunctions.getPosFaceQteMin(0, getDes(), new Ressource(1, Ressource.type.SOLEIL));
        //else
        //posFaceQteMin = getPosFaceQteMin(0, getDes(), new Random().nextInt(6));
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            if (posFaceQteMin != -1) {
                resultat[0] = 0;
                resultat[1] = posFaceQteMin;
//                forgerDe(0, face, posFaceQteMin);
            } else {
                resultat[0] = 0;
                resultat[1] = new Random().nextInt(6);
//                forgerDe(0, face, new Random().nextInt(6));
            }
        return resultat;
    }

    @Override
    public Face choisirFaceACopier(List<Face> faces) {
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        Ressource ressource;
        for (int i = 0; i != faces.size(); ++i) {
//            for (Ressource ressource : faces.get(i).getRessources()) {
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

//            }
        }
        if (posMaxSoleil != -1) return faces.get(posMaxSoleil);
        if (posMaxLune != -1) return faces.get(posMaxLune);
        if (posMaxOr != -1) return faces.get(posMaxOr);
        return faces.get(new Random().nextInt(faces.size()));
    }

    @Override
    public choixJetonTriton utiliserJetonTriton() {
        Random random = new Random();
        int choix = random.nextInt(choixJetonTriton.values().length);
        switch (choix) {
            case 0:
                return choixJetonTriton.Rien;
            case 1:
                return choixJetonTriton.Or;
            case 2:
                return choixJetonTriton.Soleil;
            case 3:
                return choixJetonTriton.Lune;
        }
        throw new DiceForgeException("Bot", "Impossible, utiliserJetonTriton ne renvoit rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere() {
        Random random = new Random();
        return random.nextInt(2) == 1;
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
                if (getDes()[i].getFace(j).getTypeFace() == Face.typeFace.SIMPLE
                        && getDe(i).getFace(j).getRessource().getType() == Ressource.type.OR
                        && getDe(i).getFace(j).getRessource().getQuantite() == 1) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
    }

    private int getIdDuDeLePlusFaible() {//Le dé le plus faible est celui qui possède le plus
        int compteurFaceUnOrDeZero = 0; // de face un or
        for (int i = 0; i < getDes().length; i++)
            for (Face face : getDe(0).getFaces())
                if (face.getTypeFace() == Face.typeFace.SIMPLE)
                    if (face.getRessource().getQuantite() == 1)
                        if (face.getRessource().estDuType(Ressource.type.OR))
                            if (i == 0)
                                compteurFaceUnOrDeZero++;
                            else
                                compteurFaceUnOrDeZero--;
        return (compteurFaceUnOrDeZero >= 0) ? 0 : 1;
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
    public boolean choisirPdgPlutotQueRessource(Ressource ressource) {
        return true;
    }

    @Override
    public String toString() {
        return "NidoBot";
    }
}

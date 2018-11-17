package bot;

import diceForge.*;
import java.lang.reflect.AnnotatedArrayType;
import java.util.List;
import java.util.Random;
import static diceForge.Carte.Noms.*;
import static diceForge.Carte.Noms.Bouclier;

public class AubotLeGrand extends Joueur{
    private Random random;
    private boolean desComplet;
    private int nombreAncien = 0;
    private boolean troisJoueurs;
    private int manche = 0;
    private int compteurForge;
    private Carte cartesDispo[];
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
        compteurForge = 0;
        cartesDispo = new Carte[15];
        desComplet = false;
        random = new Random();
        }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            int indice = 0;
            troisJoueurs = (getPlateau().getJoueurs().size() == 3) ? true : false;
            }
        manche++;
        return Action.FORGER;
        }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        Bassin bassin;
        int numFace = 0;
        int numDe;
        int posFace;
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        bassin = bassins.get(0);
        numDe = 1;
        posFace = getPosFace1Or(numDe);
        compteurForge++;
        return new ChoixJoueurForge(bassin, numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = cartes.get(0);
        return carteAChoisir;
    }


    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        if ((!desComplet || getOr() < 3*nombreAncien) && getOr() + quantiteOr <= getMaxOr())
            return quantiteOr;
        return 0;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return false;
    }

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        return renfortsUtilisables;
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix){
        int indice = -1;
        for (Ressource[] ressources: faceAChoix.getRessource()) {
            indice++;
            if (ressources[0] instanceof Or)
                return indice;
        }
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        return 0;
    }

    @Override
    public int choisirDeCyclope(){
        return 0;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        int numDe = 1;
        int numFace = getPosFace1Or(numDe);
        forgerDe(0, face, numFace);
    }


    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
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
    public boolean choisirRessourceOuPdg(Ressource ressource){
        return true;
    }

    @Override
    public String toString(){
        return "AubotLeGrand (bot de Lucas)";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Extrêmement utile pour connaitre la force des dés du bot, lui permet de savoir quelles stratégies
     * il devra adopter.
     * @param numDe
     * @return un tableau des ressources moyennes gagnées par lancé par le dé dans l'ordre suivant: or/soleil/lune/pdg
     */
    private void statsDe(int numDe) {
        int or = 0;
        int lune = 0;
        int soleil = 0;
        if (desComplet)
            for (De de : getDes())
                for (Face face : de.getFaces())
                    for (Ressource[] ressources : face.getRessource())
                        for (Ressource ressource : ressources) {
                            if (ressource instanceof Or)
                                or += ressource.getQuantite();
                            if (ressource instanceof Soleil)
                                soleil += ressource.getQuantite();
                            if (ressource instanceof Lune)
                                lune += ressource.getQuantite();
                        }
    }

    private int getPosFace1Or(int numDe){
        for (int i = 0; i != 6; i++) {
            if (getDe(numDe).getFace(i).getRessource().length == 1)
                if (getDe(numDe).getFace(i).getRessource()[0][0] instanceof Or && getDe(numDe).getFace(i).getRessource()[0][0].getQuantite() == 1)
                    return i;
        }
        return random.nextInt(6);
    }

    private Bassin trouveBassinCout(List<Bassin> bassins, int cout, String typeRessource){
        if (typeRessource.equals("Or")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Or)
                                    return bassin;
        }
        if (typeRessource.equals("Soleil")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Soleil)
                                    return bassin;
        }
        if (typeRessource.equals("Lune")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Lune)
                                    return bassin;
        }
        return null;
    }
}

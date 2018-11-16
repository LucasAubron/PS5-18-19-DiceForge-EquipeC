package bot;

import diceForge.*;

import java.awt.*;
import java.util.List;

public class AubotLeGrand extends Joueur{
    private boolean desComplet = false;
    private int manche = 1;
    private int nombreDeJoueurs;
    private Joueur.Action[] historiqueAction;
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    @Override
    public Joueur.Action choisirAction(int numManche) {
        if (manche == 0) {//on fait ici ce qu'on n'a pas pu faire dans le constructeur (du fait que le plateau n'est pas encore complètement initialisé à ce moment là)
            this.nombreDeJoueurs = getPlateau().getJoueurs().size();
            historiqueAction = (nombreDeJoueurs == 3) ? new Joueur.Action[10] : new Joueur.Action[9];
        }
        return Action.EXPLOIT;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        Bassin bassin = bassins.get(0);
        int numFace = 0;
        int numDe = 0;
        int posFace = 0;
        return new ChoixJoueurForge(bassin, numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        return cartes.get(0);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return false; //if true, faire manche--
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        return 0;
    }

    @Override
    public List<Joueur.Renfort> choisirRenforts(List<Joueur.Renfort> renforts){
        return renforts;
    }

    @Override
    public int choisirRessource(Face face){
        return  0;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
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
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return 2;
    }

    @Override
    public void forgerFace(Face face){
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        return 0;
    }

    @Override
    public Joueur.choixJetonTriton utiliserJetonTriton(){
        return choixJetonTriton.Soleil;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        return true;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
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
        int or = 0, soleil = 0, lune = 0, pdg = 0;
        for (Face face : getDes()[numDe].getFaces()) {
            if (face.getRessource().length > 1){ //Si face a choix
                for (Ressource[] ressource : face.getRessource()) {
                    if (ressource[0] instanceof Or) {
                        or += ressource[0].getQuantite();
                    } else if (ressource[0] instanceof Lune) {
                        lune += ressource[0].getQuantite();
                    } else if (ressource[0] instanceof Soleil) {
                        soleil += ressource[0].getQuantite();
                    } else if (ressource[0] instanceof PointDeGloire) {
                        pdg += ressource[0].getQuantite();
                    }
                }
            } else if (face.getRessource()[0].length > 1) { // Si face addition (on else if car une face ne peut être à la fois à choix et addition)
                for (Ressource ressource : face.getRessource()[0]) {
                    if (ressource instanceof Or) {
                        or += ressource.getQuantite();
                    } else if (ressource instanceof Lune) {
                        lune += ressource.getQuantite();
                    } else if (ressource instanceof Soleil) {
                        soleil += ressource.getQuantite();
                    } else if (ressource instanceof PointDeGloire) {
                        pdg += ressource.getQuantite();
                    }
                }
            }
            else {//Si face "normale"
                if (face.getRessource()[0][0] instanceof Or) {
                    or += face.getRessource()[0][0].getQuantite();
                } else if (face.getRessource()[0][0] instanceof Lune) {
                    lune += face.getRessource()[0][0].getQuantite();
                } else if (face.getRessource()[0][0] instanceof Soleil) {
                    soleil += face.getRessource()[0][0].getQuantite();
                } else if (face.getRessource()[0][0] instanceof PointDeGloire) {
                    pdg += face.getRessource()[0][0].getQuantite();
                }
            }

        }
    }

    private int getPosFace1Or(int numDe){
        for (int i = 0; i != 6; ++i){//Toutes les faces
            if (getDes()[numDe].getFaces()[i].getRessource().length != 0 && getDes()[numDe].getFaces()[i].getRessource()[0][0] instanceof Or && getDes()[numDe].getFaces()[i].getRessource()[0][0].getQuantite() == 1){
                return i;
                }
            }
        return -1;
    }

}

package bot;

import diceForge.*;

import java.util.List;

public class AubotLeGrand extends Joueur{
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){ super(identifiant, afficheur, plateau); }

    @Override
    public Joueur.Action choisirAction(int numManche){ return null; }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        return null;
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
        return 0;
    }

    @Override
    public List<Joueur.Renfort> choisirRenforts(List<Joueur.Renfort> renforts){
        return null;
    }

    @Override
    public int choisirRessource(Face face){
        return 0;
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
        return 0;
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

    /**
     * Extrêmement utile pour connaitre la force des dés du bot, lui permet de savoir quelles stratégies
     * il devra adopter.
     * @param numDe
     * @return un tableau des ressources moyennes gagnées par lancé par le dé dans l'ordre suivant: or/soleil/lune/pdg
     */
    private float[] statsDe(int numDe) {
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
        return new float[]{or/6, soleil/6, lune/6, pdg/6};
    }
}

package bot;

import diceForge.*;

import java.util.List;

public class AubotLeGrand extends Joueur{
    private int manche = 0;
    private float de1or;
    private float de1soleil;
    private float de1lune;
    private float de1pdg;
    private float de2or;
    private float de2soleil;
    private float de2lune;
    private float de2pdg;
    private float deOr;
    private float deSoleil;
    private float deLune;
    private float dePdg;
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){ super(identifiant, afficheur, plateau); }

    @Override
    public Joueur.Action choisirAction(int numManche){
        statsDe(0); statsDe(1);
        manche++;
        switch(manche) {
            case 1:
                if (getLune() == 0 && getSoleil() == 0)
                    return Action.FORGER;
                if (getOr() < 4 && getLune() == 1 && getSoleil() == 1)
                    return Action.EXPLOIT;
                if (getOr() == 4 && ((getSoleil() >=2 && getLune() >=1) || getSoleil() >= 3))
                    return Action.EXPLOIT;
                else
                    return Action.FORGER;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
        return Action.FORGER; //temporaire pour compiler
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        switch(manche) {
            case 1:
                for (Carte carte: cartes){
                    if (carte.equals("HerbesFolles"));
                        return carte;
                }
                for (Carte carte: cartes){
                    if (carte.equals("Coffre"));
                        return carte;
                }
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
        return cartes.get(0);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        switch(manche) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
        return true;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        switch(manche) {// trouver un moyen d'envoyer le numéro de la manche
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
        return 0;
    }

    @Override
    public List<Joueur.Renfort> choisirRenforts(List<Joueur.Renfort> renforts){
        return null;
    }

    @Override
    public int choisirRessource(Face face){
        switch(manche) {//trouver un moyen d'envoyer le numéro de la manche
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
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
        if (numDe == 0)
            de1or = or/(float)6; de1soleil = soleil/(float)6; de1lune = lune/(float)6; de1pdg = pdg/(float)6;
        if (numDe == 1)
            de2or = or/(float)6; de2soleil = soleil/(float)6; de2lune = lune/(float)6; de2pdg = pdg/(float)6;
        deOr = de1or +de2or; deSoleil = de1soleil + de2soleil; deLune = de1lune + de2lune; dePdg = de1pdg + de2pdg;
    }
}

package bot;

import diceForge.*;

import java.awt.*;
import java.util.List;

public class AubotLeGrand extends Joueur{
    private int manche = 0;
    private int nombreDeJoueurs;
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
    private Plateau plateau;
    private Joueur.Action[] historiqueAction;
    public AubotLeGrand(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    @Override
    public Joueur.Action choisirAction(int numManche){
        if (manche == 0){
            this.nombreDeJoueurs = getPlateau().getJoueurs().size();
            historiqueAction = (nombreDeJoueurs == 3) ? new Joueur.Action[10] : new Joueur.Action[9];
        }
        statsDe(0); statsDe(1);
        manche++;
        switch(manche) {
            case 1:
                if (getOr() >= 5)
                    return Action.FORGER;
                if (getLune() >= 2 && nombreDeJoueurs > 2) {
                    historiqueAction[0] = Action.EXPLOIT;
                    return Action.EXPLOIT;
                }
                if (getOr() <= 3 && getLune() >=1) {
                    historiqueAction[0] = Action.EXPLOIT;
                    return Action.EXPLOIT;
                }
                else {
                    historiqueAction[0] = Action.FORGER;
                    return Action.FORGER;
                }
            case 2:
                return Action.FORGER;
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
        Bassin bassin = null;
        int numFace = -1;
        int numDe = -1;
        int posFace = -1;
        switch(manche) {
            case 1:
                if (getOr() >= 6)
                    bassin = trouveBassin(bassins, 4, "Or");
                    if (bassin != null) {
                        numFace = trouvePosFace(bassin, 6, "Or");
                        if (numFace == -1) {
                            numFace = trouvePosFace(bassin, 2, "Or");
                        }
                    }
                if (bassin == null && getOr() >=5)
                    bassin = trouveBassin(bassins, 3, "Or");
                    if (bassin != null) {
                        numFace = trouvePosFace(bassin, 4, "Or");
                    }
                if (bassin == null) {
                    bassin = trouveBassin(bassins, 2, "Or");
                    numFace = trouvePosFace(bassin, 3, "Or");
                }
                numDe = 0;
                posFace = 0;
                numFace = 0;
                break;
            case 2:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 3:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 4:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 5:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 6:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 7:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 8:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 9:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
            case 10:
                bassin = bassins.get(0);
                numFace = 0;
                numDe = 0;
                posFace = 0;
                break;
        }
        return new ChoixJoueurForge(bassin, numFace, numDe, posFace);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        switch(manche) {
            case 1:
                Carte carte = chercheCarteDansListe(Carte.Noms.Ours, cartes);
                if (carte != null && nombreDeJoueurs > 2)
                    return carte;
                carte = chercheCarteDansListe(Carte.Noms.Marteau, cartes);
                if (carte != null)
                    return carte;
                carte = chercheCarteDansListe(Carte.Noms.Ancien, cartes);
                if (carte != null)
                    return carte;
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
        if (numDe == 0)
            de1or = or/(float)6; de1soleil = soleil/(float)6; de1lune = lune/(float)6; de1pdg = pdg/(float)6;
        if (numDe == 1)
            de2or = or/(float)6; de2soleil = soleil/(float)6; de2lune = lune/(float)6; de2pdg = pdg/(float)6;
        deOr = de1or +de2or; deSoleil = de1soleil + de2soleil; deLune = de1lune + de2lune; dePdg = de1pdg + de2pdg;
    }

    private int nombreCartesDisponible(Carte carteAChercher){
        int nombre = 0;
        for (Ile ile: getPlateau().getIles())
            for (List<Carte> typeCarte: ile.getCartes())
                for (Carte carte: typeCarte)
                    if (carte.getNom() == carteAChercher.getNom())
                        nombre++;
        return nombre;
    }

    private int nombreFacesDisponible(Face faceAChercher){
        int nombre = 0;
        for (Bassin bassin: getPlateau().getTemple().getSanctuaire())
            for (Face face: bassin.getFaces())
                if (face.equals(faceAChercher))
                    nombre++;
        return nombre;
    }

    private Carte chercheCarteDansListe(Carte.Noms nom, List<Carte> cartes){
        for (Carte carte: cartes) {
            if (carte.getNom() == nom)
                return carte;
        }
        return null;
    }

    private Bassin trouveBassin(List<Bassin> bassins, int cout, String typeRessource) {
        if (typeRessource.equals("Or"))
            for (Bassin bassin: bassins)
                if (bassin.getCout() == cout)
                    for (Face face: bassin.getFaces())
                        for (Ressource[] ressources: face.getRessource())
                            for (Ressource ressource: ressources)
                                if (ressource instanceof Or)
                                    return bassin;
        if (typeRessource.equals("Soleil"))
            for (Bassin bassin: bassins)
                if (bassin.getCout() == cout)
                    for (Face face: bassin.getFaces())
                        for (Ressource[] ressources: face.getRessource())
                            for (Ressource ressource: ressources)
                                if (ressource instanceof Soleil)
                                    return bassin;
        if (typeRessource.equals("Lune"))
            for (Bassin bassin: bassins)
                if (bassin.getCout() == cout)
                    for (Face face: bassin.getFaces())
                        for (Ressource[] ressources: face.getRessource())
                            for (Ressource ressource: ressources)
                                if (ressource instanceof Lune)
                                    return bassin;
        if (typeRessource.equals("PointDeGloire"))
            for (Bassin bassin: bassins)
                if (bassin.getCout() == cout)
                    for (Face face: bassin.getFaces())
                        for (Ressource[] ressources: face.getRessource())
                            for (Ressource ressource: ressources)
                                if (ressource instanceof PointDeGloire)
                                    return bassin;
        return null;
    }

    private int trouvePosFace(Bassin bassin, int quantite, String typeRessource){
        if (typeRessource.equals("Or"))
            for (int i=0; i < bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Or && ressource.getQuantite() == quantite)
                            return i;
        if (typeRessource.equals("Soleil"))
            for (int i=0; i<bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Soleil && ressource.getQuantite() == quantite)
                            return i;
        if (typeRessource.equals("Lune"))
            for (int i=0; i<bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof Lune && ressource.getQuantite() == quantite)
                            return i;
        if (typeRessource.equals("PointDeGloire"))
            for (int i=0; i<bassin.getFaces().size(); i++)
                for (Ressource[] ressources: bassin.getFace(i).getRessource())
                    for (Ressource ressource: ressources)
                        if (ressource instanceof PointDeGloire && ressource.getQuantite() == quantite)
                            return i;
         return -1;
    }

}

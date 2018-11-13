package bot;

import diceForge.*;

import java.util.List;

public class PlanteBot extends Joueur {//Plus communément appelé MLG Bot
    public PlanteBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    @Override
    public Action choisirAction(int numManche){
        return null;
    }

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
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
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

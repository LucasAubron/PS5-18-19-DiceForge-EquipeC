package bot;

import diceForge.*;

import java.util.List;

public class PlanteBot extends Joueur {//Plus communément appelé MLG Bot
    private int numManche = 1;//On est jamais mieux servi que par soi même
    public PlanteBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    private void refresh(){
    }

    @Override
    public Action choisirAction(int numManche){
        refresh();
        numManche++;
        return null;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        refresh();
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        refresh();
        return null;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        refresh();
        return true;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        refresh();
        return 0;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        refresh();
        return null;
    }

    @Override
    public int choisirRessource(Face face){
        refresh();
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        refresh();
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        refresh();
        return 0;
    }

    @Override
    public int choisirDeCyclope(){
        refresh();
        return 0;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        refresh();
        return 0;
    }

    @Override
    public void forgerFace(Face face){
        refresh();
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        refresh();
        return 0;
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        refresh();
        return null;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        refresh();
        return true;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        refresh();
        return true;
    }
}

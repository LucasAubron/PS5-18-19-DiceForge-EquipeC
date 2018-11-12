package bot;

import diceForge.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe pour tester le comportemement d'un joueur
 * Pour tester un comportement d'un joueur, il faut d'abord initialiser ce que va renvoyer le bot,
 * puis appeler la méthode à tester
 */
public class TestBot extends Joueur {
    private Random random = new Random();

    public TestBot(int identifiant, Afficheur afficheur) {super (identifiant, afficheur);}

    /**
     * Pour tester choisirAction, il faut initialiser setActionAChoisir
     */
    private Action actionAChoisir;
    @Override
    public Action choisirAction(int numManche) {
        return actionAChoisir;
    }
    public void setActionAChoisir(Action actionAChoisir) {
        this.actionAChoisir = actionAChoisir;
    }

    /**
     * num[0] = numBassin, num[1] = numFace, num[2] = numDe, num[3] = posFace
     */
    private int[] num = new int[4];

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        return new ChoixJoueurForge(bassins.get(num[0]), num[1], num[2], num[3]);
    }
    public void setNum(int[] num) {
        this.num = num;
    }

    private int numCarte;
    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        return cartes.get(numCarte);
    }
    public void setNumCarte(int numCarte) {
        this.numCarte = numCarte;
    }

    /**
     * choix sert pour toute les méthodes ou il n'y a que 2 choix
     */
    private boolean choix;
    @Override
    public boolean choisirActionSupplementaire(int numManche){
        if (choix) ajouterSoleil(-2);
        return choix;
    }
    public void setChoixActionSup(boolean choix) {
        this.choix = choix;
    }

    private int nbrPointMarteau = 0;
    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){return nbrOr-nbrPointMarteau;}
    public void setNbrPointMarteau(int nbrPointMarteau) {
        this.nbrPointMarteau = nbrPointMarteau;
    }

    private int numRenfort;
    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        List<Renfort> renforts = new ArrayList<>();
        renforts.add((Renfort) renfortsUtilisables.get(numRenfort));
        return renforts;
    }
    public void setNumRenfort(int numRenfort) {
        this.numRenfort = numRenfort;
    }

    private int numFace;
    @Override
    public int choisirRessource(Face faceAChoix){
        return numFace;
    }
    public void setNumFace(int numFace) {
        this.numFace = numFace;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix) {return numFace;}

    private int numDe;
    @Override
    public int choisirDeFaveurMineure() { return numDe; }
    public void setNumDe(int numDe) {
        this.numDe = numDe;
    }

    @Override
    public int choisirDeCyclope(){return numDe;}

    private int id;
    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void forgerFace(Face face){
        forgerDe(numDe, face, numFace);
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces) {
        return numFace;
    }

    @Override
    public int[] choisirFaceARemplacerPourMiroir(){return new int[]{
            random.nextInt(2),
            random.nextInt(6)};
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
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
        throw new DiceForgeException("Bot","Impossible, utiliserJetonTriton ne renvoi rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere(){
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource){return choix;}

    @Override
    public String toString(){return "TestBot";}

}

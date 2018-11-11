package diceForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe pour tester le comportemement d'un joueur
 * Pour tester un comportement d'un joueur, il faut d'abord initialiser ce que va renvoyer le bot,
 * puis appeler la méthode à tester
 */
class TestBot extends Joueur {
    private Random random = new Random();

    TestBot(int identifiant) {super (identifiant);}

    /**
     * Pour tester choisirAction, il faut initialiser setActionAChoisir
     */
    private Action actionAChoisir;
    @Override
    Action choisirAction(int numManche) {
        return actionAChoisir;
    }
    void setActionAChoisir(Action actionAChoisir) {
        this.actionAChoisir = actionAChoisir;
    }

    /**
     * num[0] = numBassin, num[1] = numFace, num[2] = numDe, num[3] = posFace
     */
    private int[] num = new int[4];

    @Override
    ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        return new ChoixJoueurForge(bassins.get(num[0]), num[1], num[2], num[3]);
    }
    void setNum(int[] num) {
        this.num = num;
    }

    private int numCarte;
    @Override
    Carte choisirCarte(List<Carte> cartes, int numManche){
        return cartes.get(numCarte);
    }
    void setNumCarte(int numCarte) {
        this.numCarte = numCarte;
    }

    /**
     * choix sert pour toute les méthodes ou il n'y a que 2 choix
     */
    private boolean choix;
    @Override
    boolean choisirActionSupplementaire(int numManche){
        if (choix) ajouterSoleil(-2);
        return choix;
    }
    void setChoixActionSup(boolean choix) {
        this.choix = choix;
    }

    private int nbrPointMarteau = 0;
    @Override
    int choisirRepartitionOrMarteau(int nbrOr){return nbrOr-nbrPointMarteau;}
    void setNbrPointMarteau(int nbrPointMarteau) {
        this.nbrPointMarteau = nbrPointMarteau;
    }

    private int numRenfort;
    @Override
    List<Renfort> choisirRenforts(List renfortsUtilisables){
        List<Renfort> renforts = new ArrayList<>();
        renforts.add((Renfort) renfortsUtilisables.get(numRenfort));
        return renforts;
    }
    void setNumRenfort(int numRenfort) {
        this.numRenfort = numRenfort;
    }

    private int numFace;
    @Override
    int choisirRessource(Face faceAChoix){
        return numFace;
    }
    void setNumFace(int numFace) {
        this.numFace = numFace;
    }

    @Override
    int choisirRessourceAPerdre(Face faceAChoix) {return numFace;}

    private int numDe;
    @Override
    int choisirDeBiche() { return numDe; }
    void setNumDe(int numDe) {
        this.numDe = numDe;
    }

    private int id;
    @Override
    int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return id;
    }
    void setId(int id) {
        this.id = id;
    }

    @Override
    void forgerFace(Face face){
        forgerDe(numDe, face, numFace);
    }

    @Override
    int choisirFacePourGagnerRessource(List<Face> faces) {
        return numFace;
    }

    @Override
    int[] choisirFaceARemplacerPourMiroir(){return new int[]{
            random.nextInt(2),
            random.nextInt(6)};
    }

    @Override
    void utiliserJetonTriton(){
        Random random = new Random();
        int choix;
        if (1 == random.nextInt(2)){
            choix = random.nextInt(3);
            switch (choix){
                case 0:
                    ajouterSoleil(2);
                    break;
                case 1:
                    ajouterLune(2);
                    break;
                case 2:
                    ajouterOr(6);
                    break;
            }
            retirerJeton("TRITON");
        }
    }

    @Override
    void utiliserJetonCerbere(){
        Random random = new Random();
        int choix;
        if (1 == random.nextInt(2)){
            switch (getDernierLanceDes()){
                case 0:
                    gagnerRessourceFace(getDesFaceCourante()[0]);
                    break;
                case 1:
                    gagnerRessourceFace(getDesFaceCourante()[1]);
                    break;
                case 2:
                    gagnerRessourceFace(getDesFaceCourante()[0]);
                    gagnerRessourceFace(getDesFaceCourante()[1]);
                    break;
            }
            retirerJeton("CERBERE");
        }
    }

    @Override
    public String toString(){return "TestBot";}
}

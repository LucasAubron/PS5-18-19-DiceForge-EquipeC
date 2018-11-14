package bot;

import diceForge.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class NidoBot extends Joueur {
    /*
    * stratégie globale:    forger max lune, soleil, pour acheter hydre, gorgogne, pince
    *
    * */
    private int numeroManche = 0;//On est jamais mieux servi que par soi même
    private int maxPdg = 0;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer
    public NidoBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
        choixAction = new int[plateau.getJoueurs().size() == 3 ? 10 : 9];
    }


    @Override
    public Action choisirAction(int numManche){
        return null;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches //forger de l'or un max!
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            else if (bassinAChoisir != null && bassin.getFaces().get(0).getRessource().length == 1) {
                for (int indexDe = 0; indexDe < getDes().length; indexDe++)
                    if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Soleil && getDes()[indexDe].getNbFacesSoleil() <= 2) {
                        int posFace = getDes()[indexDe].getPosFaceOrQteMin();
                        if (posFace != -1)
                            return new ChoixJoueurForge(bassin, 0, indexDe, posFace);
                    }
//                if (bassin.getFaces().get(0).getRessource()[0][0] instanceof Lune && getDe().getNbFacesLune() <= 2) {
//
//                }

                bassinAChoisir = bassin;
            }
            else if (bassinAChoisir == null)
                bassinAChoisir = bassin;
        }
        int[] posFace = getPosFace1Or();
        if (posFace[0] != -1)
            return new ChoixJoueurForge(bassinAChoisir, 0, posFace[0], posFace[1]);

        return new ChoixJoueurForge(null, 0, 0, 0);
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
        return 1;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        return null;
    }

    @Override
    public int choisirRessource(Face face){
        return 1;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        return 1;
    }

    @Override
    public int choisirDeFaveurMineure(){
        return 1;
    }

    @Override
    public int choisirDeCyclope(){
        return 1;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return 1;
    }

    @Override
    public void forgerFace(Face face){
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        return 1;
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
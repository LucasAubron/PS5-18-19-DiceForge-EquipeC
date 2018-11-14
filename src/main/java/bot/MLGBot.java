package bot;

import diceForge.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class MLGBot extends Joueur {
    private int numeroManche = 0;//On est jamais mieux servi que par soi même
    private int maxPdg = 0;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
        choixAction = new int[plateau.getJoueurs().size() == 3 ? 10 : 9];
    }

    private byte[] pdgToByte(){
        byte[] bytes = new byte[3];
        bytes[2] = (byte)(getPointDeGloire() % 10);
        bytes[1] = (byte)((getPointDeGloire()%100)/10);
        bytes[0] = (byte)(getPointDeGloire()/100);
        return bytes;
    }

    private void ecrireAction(){
        try {
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\MLGBotProp\\MLGBotAction", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(choixAction.length+5);
            for (int i = 0; i != choixAction.length; ++i)
                buffer.put((byte)choixAction[i]);
            buffer.put((byte)';');
            buffer.put(pdgToByte());
            buffer.put((byte)'\n');
            buffer.flip();
            channel.write(buffer, file.length());
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void refresh(){
        if (getPointDeGloire() > maxPdg){
            maxPdg = getPointDeGloire();
        }
    }

    @Override
    public Action choisirAction(int numManche){
        numeroManche++;
        Action actionChoisi = null;
        switch (actionChoisi){
            case FORGER: choixAction[numeroManche] = 0;
            case EXPLOIT: choixAction[numeroManche] = 1;
            case PASSER: choixAction[numeroManche] = 2;
        }
        if (numeroManche == choixAction.length)
            ecrireAction();
        refresh();
        return actionChoisi;
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

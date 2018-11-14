package bot;

import diceForge.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;

public class MLGBot extends Joueur {
    private Random random = new Random();
    private int numeroManche = -1;//On est jamais mieux servi que par soi même
    private int maxPdg = -1;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer
    private String infoPartie = ";";
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    private int getPdgAvecCarte(){
        int pdg = getPointDeGloire();
        for (Carte carte:getCartes()){
            pdg += carte.getNbrPointGloire();
            if (carte.getNom() == Carte.Noms.Typhon)
                for (De de:getDes())
                    pdg += de.getNbrFaceForge();
        }
        return pdg;
    }

    private void ecrire(){
        try {
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\MLGBot\\MLGBotProp.txt", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(infoPartie.length());
            buffer.clear();
            buffer.put(infoPartie.getBytes());
            buffer.flip();
            while (buffer.hasRemaining())
                channel.write(buffer, file.length());
            file.close();
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
        numeroManche = numManche-1;
        if (numeroManche == 0)
            choixAction = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        Action actionChoisi = null;
        int rand = random.nextInt(3);
        switch (rand){
            case 0: actionChoisi = Action.FORGER; break;
            case 1: actionChoisi = Action.EXPLOIT; break;
            case 2: actionChoisi = Action.PASSER; break;
        }
        choixAction[numeroManche] = rand;
        if (numeroManche == choixAction.length-1)
            ecrire();
        refresh();
        return actionChoisi;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        refresh();
        if (!bassins.isEmpty())
            return new ChoixJoueurForge(bassins.get(0), 0, 0, 0);
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        refresh();
        return cartes.get(0);
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
        return renforts;
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
        return getIdentifiant() == 1 ? 2 : 1;
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
        return choixJetonTriton.Soleil;
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

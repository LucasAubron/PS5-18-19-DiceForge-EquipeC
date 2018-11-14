package bot;

import diceForge.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;

public class MLGBot extends Joueur {
    private Random notLuckButSkill = new Random();
    private int numeroManche = -1;//On est jamais mieux servi que par soi même
    private int getOnMyLevel = -1;
    private int[] choixAction;//0 = forger, 1 = exploit, 2 = passer
    private String cheatCode = ";";
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    private int pwned(){
        int pdg = getPointDeGloire();
        for (Carte carte:getCartes()){
            pdg += carte.getNbrPointGloire();
            if (carte.getNom() == Carte.Noms.Typhon)
                for (De de:getDes())
                    pdg += de.getNbrFaceForge();
        }
        return pdg;
    }

    private void Xx360xX_NoScope(){
        try {
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\MLGBot\\MLGBotProp.txt", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(cheatCode.length());
            buffer.clear();
            buffer.put(cheatCode.getBytes());
            buffer.flip();
            while (buffer.hasRemaining())
                channel.write(buffer, file.length());
            file.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void gettingGood(){
        if (getPointDeGloire() > getOnMyLevel){
            getOnMyLevel = getPointDeGloire();
        }
    }

    @Override
    public Action choisirAction(int numManche){
        numeroManche = numManche-1;
        if (numeroManche == 0)
            choixAction = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        Action actionChoisi = null;
        int rand = notLuckButSkill.nextInt(3);
        switch (rand){
            case 0: actionChoisi = Action.FORGER; break;
            case 1: actionChoisi = Action.EXPLOIT; break;
            case 2: actionChoisi = Action.PASSER; break;
        }
        choixAction[numeroManche] = rand;
        if (numeroManche == choixAction.length-1)
            Xx360xX_NoScope();
        gettingGood();
        return actionChoisi;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        gettingGood();
        if (!bassins.isEmpty())
            return new ChoixJoueurForge(bassins.get(0), 0, 0, 0);
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        gettingGood();
        return cartes.get(0);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        gettingGood();
        return true;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        gettingGood();
        return 0;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        gettingGood();
        return renforts;
    }

    @Override
    public int choisirRessource(Face face){
        gettingGood();
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        gettingGood();
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        gettingGood();
        return 0;
    }

    @Override
    public int choisirDeCyclope(){
        gettingGood();
        return 0;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        gettingGood();
        return getIdentifiant() == 1 ? 2 : 1;
    }

    @Override
    public void forgerFace(Face face){
        gettingGood();
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        gettingGood();
        return 0;
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        gettingGood();
        return choixJetonTriton.Soleil;
    }

    @Override
    public boolean utiliserJetonCerbere(){
        gettingGood();
        return true;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        gettingGood();
        return true;
    }
}

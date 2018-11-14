package bot;

import diceForge.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MLGBot extends Joueur {
    private Random notLuckButSkill = new Random();
    private int numeroManche = -1;//On est jamais mieux servi que par soi même
    private int getOnMyLevel = -1;
    private boolean MOVEMENT = true;
    private int[] choixActionNext;//0 = forger, 1 = exploit, 2 = passer
    private int[] choixAction;
    private int[] choixSecondeActionNext;
    private int[] choixSecondeAction;
    private int[][] ordreCarteNext;
    private int[][] ordreCarte;
    private int[][] choixBassinNext;
    private int[][] choixBassin;
    private boolean intensiveTraining = true;
    private String cible = "";
    private int gen = -2;
    private long positionCHEF = -1;
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    private void goGoGo(int nbrJoueur){
        try{
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1);
            int x = channel.read(buf);
            if (x == 0){
                buf.clear();
                buf.put((byte)0);
                buf.flip();
                channel.write(buf);
                gen = 0;
            }
            else
                gen = (int)buf.get(0);
            file.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        cible = "src\\main\\java\\bot\\mlgBot\\MLGBot"+nbrJoueur+"JGen"+gen;
        File genFile = new File(cible);
        if (!genFile.exists()){
            if (intensiveTraining) {
                try {
                    genFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
                positionCHEF = 0;
            }
            gen = -1;
        }
        else {
            positionCHEF = genFile.length();
            //get the data to create the bot
            if (intensiveTraining){
                if (positionCHEF > 1000)
                    remuerLaSoupe(nbrJoueur);
            }
        }
    }

    private void remuerLaSoupe(int nbrJoueur){
        try {
            RandomAccessFile file = new RandomAccessFile(cible, "rw");
            FileChannel channel = file.getChannel();
            List<String> darkMatter = new ArrayList<>();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            for (long i = 0; i < file.length();){
                buf.clear();
                int x = channel.read(buf);
                String s = buf.toString();
                String[] sList = s.split("\n");
                if (!darkMatter.isEmpty() && !darkMatter.get(darkMatter.size()-1).substring(darkMatter.get(darkMatter.size()-1).length()-1).equals("\n"))
                    darkMatter.set(darkMatter.size()-1, darkMatter.get(darkMatter.size()-1)+sList[0]+"\n");
                else
                    darkMatter.add(sList[0]+"\n");
                for (int j = 1; j != sList.length; ++j)
                    darkMatter.add(sList[j]+"\n");
                i = channel.position();
            }
            file.close();
        } catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        //Partie update des fichiers
        ++gen;
        cible = "src\\main\\java\\bot\\mlgBot\\MLGBot"+nbrJoueur+"JGen"+gen;
        File newFile = new File(cible);
        positionCHEF = 0;
        try{
            newFile.createNewFile();
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.clear();
            buf.put((byte)gen);
            buf.flip();
            channel.write(buf, 0);
            file.close();
        }catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
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
        if (intensiveTraining) {
            String newCheatCode = "";
            for (int i = 0; i != choixActionNext.length; ++i)
                newCheatCode += choixActionNext[i];
            newCheatCode += ";";
            for (int i = 0; i != choixSecondeActionNext.length; ++i)
                newCheatCode += choixSecondeActionNext[i];

            newCheatCode += ";"+pwned()+"\n";
            try {
                RandomAccessFile file = new RandomAccessFile(cible, "rw");
                FileChannel channel = file.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(newCheatCode.length());
                buffer.clear();
                buffer.put(newCheatCode.getBytes());
                buffer.flip();
                while (buffer.hasRemaining())
                    channel.write(buffer, positionCHEF);
                file.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void gettingGood(){
        if (gen == -2)
            goGoGo(getPlateau().getJoueurs().size());
        if (numeroManche > 8 && numeroManche == choixActionNext.length-1) {
            if (pwned() > getOnMyLevel) {
                getOnMyLevel = pwned();
                MOVEMENT = true;
            }
            if (MOVEMENT) {
                Xx360xX_NoScope();
                MOVEMENT = false;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\

    @Override
    public Action choisirAction(int numManche){
        boolean secondeAction = (numeroManche == numManche-1);
        numeroManche = numManche-1;
        if (numeroManche == 0) {
            choixActionNext = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
            choixSecondeActionNext = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
            ordreCarteNext = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9][Carte.Noms.values().length];
            choixBassinNext = new int[getPlateau().getJoueurs().size() == 3 ? 10 : 9][12];
        }
        Action actionChoisi = null;
        int rand = notLuckButSkill.nextInt(3) + 1;
        switch (rand){
            case 1: actionChoisi = Action.FORGER; break;
            case 2: actionChoisi = Action.EXPLOIT; break;
            case 3: actionChoisi = Action.PASSER; break;
        }
        if (secondeAction)
            choixSecondeActionNext[numeroManche] = rand;
        else
            choixActionNext[numeroManche] = rand;
        MOVEMENT = true;
        if (numeroManche == choixActionNext.length-1)
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

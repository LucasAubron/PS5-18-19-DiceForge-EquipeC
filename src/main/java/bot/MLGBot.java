package bot;

import bot.mlgBot.SourceLines;
import bot.mlgBot.StatLine;
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
    private byte[] choixActionNext;//0 = forger, 1 = exploit, 2 = passer
    private byte[] choixAction;
    private byte[] choixSecondeActionNext;
    private byte[] choixSecondeAction;
    private byte[] choixCarteNext;
    private List<Byte> puissanceSoleil = new ArrayList<>();
    private List<Byte> puissanceLune = new ArrayList<>();
    private byte[][] ordreCarte;
    private List<Byte> choixBassinNext = new ArrayList<>();
    private List<Byte> choixBassinManche = new ArrayList<>();
    private List<Byte> puissanceOr = new ArrayList<>();
    private byte[][] ordreBassin;
    private boolean intensiveTraining = true;
    private String cible = "";
    private int gen = -2;
    private long positionCHEF = -1;
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    enum NomCarteOverride{Ancien, HerbesFolles, Hibou, Minautore, BateauCeleste, Bouclier, Meduse, MiroirAbyssal, Triton, Cyclope, Sphinx, Hydre, Typhon,//On ne prend on compte que les soleils pour hydre et typhon
    Marteau, Coffre, Biche, Ours, Sanglier, Satyres, Cerbere, Passeur, CasqueDinvisibilite, Sentinelle, Cancer}

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
                if (positionCHEF > 5000)
                    remuerLaSoupe(nbrJoueur);
            }
        }
        choixActionNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixSecondeActionNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixCarteNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
    }

    private int trouverPDG(byte[] bytes, int pos){
        List<Integer> pdg = new ArrayList<>();
        for (int j = pos-1; bytes[j] != ";".getBytes()[0]; --j)
            pdg.add(Integer.parseInt(new String(new byte[]{bytes[j]})));
        int reelPdg = 0;
        for (int j = 0; j != pdg.size(); ++j)
            reelPdg += pdg.get(j)*Math.pow(10, j);
        if (reelPdg < 1)
            throw new DiceForgeException("MLGBot","Le nombre de point de gloire est invalide. Min: 1, actuel: "+reelPdg);
        return reelPdg;
    }

    private void remuerLaSoupe(int nbrJoueur){
        try {
            RandomAccessFile file = new RandomAccessFile(cible, "rw");
            FileChannel channel = file.getChannel();
            byte[] bytes = new byte[(int)file.length()];
            ByteBuffer buf = ByteBuffer.allocate((int)file.length());
            int x = channel.read(buf);
            if (x != file.length())
                throw new DiceForgeException("MLGBot.java", "Le buffer n'a pas lu tout le fichier");
            file.setLength(0);
            int maxPdg = 0;
            for (int i = 0; i != x; ++i) {
                bytes[i] = buf.get(i);
                if (bytes[i] == "@".getBytes()[0]){
                    int pdg = trouverPDG(bytes, i);
                    if (pdg > maxPdg) maxPdg = pdg;
                }
            }
            List<StatLine> byteList = new ArrayList<>();
            int curseur = -1;
            for (int i = 0; i != x; ++i){
                if (bytes[i] == "@".getBytes()[0]){
                    int pdg = trouverPDG(bytes, i);
                    if (pdg > maxPdg-15){
                        byte[] byteLigne = new byte[i-curseur];
                        for (++curseur; curseur != i; ++curseur)
                            byteLigne[curseur-(i-byteLigne.length)-1] = bytes[curseur];
                        byteList.add(new StatLine(byteLigne));
                    }
                    curseur = i;
                }
            }
            SourceLines lignesSources = new SourceLines(byteList);
            for (byte[] sourceByte:lignesSources.getLigne()){
                buf = ByteBuffer.allocate(sourceByte.length);
                buf.clear();
                buf.put(sourceByte);
                buf.flip();
                channel.write(buf, file.length());
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
            ByteBuffer buffer = ByteBuffer.allocate(128);
            buffer.clear();
            if (choixActionNext.length < 9 || choixActionNext.length > 10)
                throw new DiceForgeException("MLGBot","Le nombre de manche est incorrect. Min: 9, max: 10, actuel: "+choixActionNext.length);
            for (int i = 0; i != choixActionNext.length; ++i)
                buffer.put(choixActionNext[i]);
            buffer.put(";".getBytes());
            for (int i = 0; i != choixActionNext.length; ++i)
                buffer.put(choixSecondeActionNext[i]);
            buffer.put(";".getBytes());
            for (int i = 0; i != choixBassinNext.size(); ++i) {
                buffer.put(choixBassinNext.get(i));
                buffer.put(choixBassinManche.get(i));
                buffer.put(puissanceOr.get(i));
            }
            buffer.put(";".getBytes());
            int count = 0;
            for (int i = 0; i != choixActionNext.length; ++i){
                if (choixCarteNext[i] != 0){
                    buffer.put(choixCarteNext[i]);
                    buffer.put(puissanceSoleil.get(count));
                    buffer.put(puissanceLune.get(count));
                    ++count;
                }
                if (i != choixActionNext.length-1)
                    buffer.put(",".getBytes());
            }

            buffer.put((";"+pwned()+"@").getBytes());
            try {
                RandomAccessFile file = new RandomAccessFile(cible, "rw");
                FileChannel channel = file.getChannel();
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
        Action actionChoisi = null;
        int rand = notLuckButSkill.nextInt(2) + 1;
        switch (rand){
            case 1: actionChoisi = Action.FORGER; break;
            case 2: actionChoisi = Action.EXPLOIT; break;
            case 3: actionChoisi = Action.PASSER; break;
        }
        if (secondeAction)
            choixSecondeActionNext[numeroManche] = (byte)rand;
        else
            choixActionNext[numeroManche] = (byte)rand;
        MOVEMENT = true;
        if (numeroManche == choixActionNext.length-1)
            Xx360xX_NoScope();
        gettingGood();
        return actionChoisi;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        int numBassin = notLuckButSkill.nextInt(bassins.size());
        boolean pasMiroir = true;
        for(De de:getDes()) {
            if (de.derniereFace() instanceof FaceMiroirAbyssal) {
                pasMiroir = false;
                break;
            }
        }
        if (bassins.get(numBassin).getCout() != 0 && pasMiroir) {
            if (bassins.get(numBassin).getCout() == 2 && bassins.get(numBassin).getFace(0).getRessource()[0][0] instanceof Or)
                choixBassinNext.add((byte)1);
            else if (bassins.get(numBassin).getCout() == 3 && bassins.get(numBassin).getFace(0).getRessource()[0][0] instanceof Soleil)
                choixBassinNext.add((byte)4);
            else if (bassins.get(numBassin).getCout() >= 4 && bassins.get(numBassin).getCout() <= 6)
                choixBassinNext.add((byte)(bassins.get(numBassin).getCout()+1));
            else if (bassins.get(numBassin).getCout() == 8 && bassins.get(numBassin).getFace(0).getRessource()[0][0] instanceof PointDeGloire)
                choixBassinNext.add((byte)9);
            else if (bassins.get(numBassin).getCout() == 12)
                choixBassinNext.add((byte)10);
            else
                choixBassinNext.add((byte)bassins.get(numBassin).getCout());
            if (choixBassinNext.get(choixBassinNext.size()-1) == 0)
                throw new DiceForgeException("MLGBot", "Bassin mal detecte. Nom : " + bassins.get(numBassin).toString());
            choixBassinManche.add((byte)numeroManche);
            puissanceOr.add((byte)getOr());
            MOVEMENT = true;
        }
        gettingGood();
        if (!bassins.isEmpty())
            return new ChoixJoueurForge(bassins.get(numBassin), 0, 0, 0);
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        int numCarte = notLuckButSkill.nextInt(cartes.size());
        for (int i = 0; i != NomCarteOverride.values().length; ++i) {
            if (NomCarteOverride.values()[i].toString().equals(cartes.get(numCarte).getNom().toString())) {
                choixCarteNext[numeroManche] = (byte)(i + 1);
                break;
            }
        }
        if (choixCarteNext[numeroManche] == 0)
            throw new DiceForgeException("MLGBot", "Carte non detecte. Nom : "+cartes.get(numCarte).getNom().toString());
        puissanceSoleil.add((byte)getSoleil());
        puissanceLune.add((byte)getLune());
        MOVEMENT = true;
        gettingGood();
        return cartes.get(numCarte);
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

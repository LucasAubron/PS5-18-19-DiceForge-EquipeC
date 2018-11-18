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
    private List<List<List<List<Byte>>>> ordreCarte;//Manche(Soleil/Lune(Quantite(Cartes)))
    private int approxRessource = 2;
    private List<Byte> choixBassinNext = new ArrayList<>();
    private List<Byte> choixBassinManche = new ArrayList<>();
    private List<Byte> puissanceOr = new ArrayList<>();
    private List<List<List<Byte>>> ordreBassin;//Manche(QuantiteOr(Bassin))
    private int approxOr = 3;
    private boolean intensiveTraining = false;//METTRE A TRUE POUR ACTIVER L ALGORITHME GENETIQUE-----------------SUPPRIMER LES FICHIERS GENERES AVANT DE COMMIT
    private String cible = "";
    private int gen = -2;
    private long positionCHEF = -1;
    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau){
        super(identifiant, afficheur, plateau);
    }

    enum NomCarteOverride{Ancien, HerbesFolles, Hibou, Minautore, BateauCeleste, Bouclier, Meduse, MiroirAbyssal, Triton, Cyclope, Sphinx, Hydre, Typhon,//On ne prend on compte que les soleils pour hydre et typhon
    Marteau, Coffre, Biche, Ours, Sanglier, Satyres, Cerbere, Passeur, CasqueDinvisibilite, Sentinelle, Cancer}

    private void goGoGo(int nbrJoueur){
        String fichierProp = "src\\main\\java\\bot\\mlgBot\\MLGBotProp";
        File testFichierProp = new File(fichierProp);
        if (!testFichierProp.exists()){
            try{testFichierProp.createNewFile();}
            catch(IOException ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
        try{
            RandomAccessFile file = new RandomAccessFile(fichierProp, "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1);
            int x = channel.read(buf,nbrJoueur-2);
            if (x == 0){
                buf.clear();
                buf.put((byte)0);
                buf.flip();
                channel.write(buf,nbrJoueur-2);
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
        }
        else {
            positionCHEF = genFile.length();
            if (intensiveTraining) {
                if (positionCHEF > 600000)//Nbr de charactere avant de passer à la génération suivante
                    remuerLaSoupe(nbrJoueur);
            }
        }
        if (gen > 0) {
            SourceLines lignesSource = new SourceLines("src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1));
            List<Byte> ligneSource;
            if (lignesSource.getLigne().size() > 1)
                ligneSource = lignesSource.getLigne().get(notLuckButSkill.nextInt(lignesSource.getLigne().size()));
            else
                ligneSource = lignesSource.getLigne().get(0);
            choixAction = new byte[nbrJoueur == 3 ? 10 : 9];
            choixSecondeAction = new byte[nbrJoueur == 3 ? 10 : 9];
            ordreBassin = new ArrayList<>();
            ordreCarte = new ArrayList<>();
            ordreBassin.add(new ArrayList<>());
            ordreCarte.add(new ArrayList<>());
            for (int i = 0; i != 2; ++i)
                ordreCarte.get(0).add(new ArrayList<>());
            int j = 0, partie = 1;
            int soleil = 0;
            for (int i = 0; i != ligneSource.size(); ++i) {
                if (ligneSource.get(i) == ";".getBytes()[0]) {
                    j = 0;
                    ++partie;
                    ++i;
                }
                switch (partie) {
                    case 1:
                        choixAction[j] = (notLuckButSkill.nextInt(20) == 0 ? (byte) (notLuckButSkill.nextInt(2) + 1) : ligneSource.get(i));
                        if (choixAction[j] == 0)
                            throw new DiceForgeException("MLGBot", "Choixaction pas bon");
                        break;
                    case 2:
                        choixSecondeAction[j] = (notLuckButSkill.nextInt(20) == 0 ? (byte) (notLuckButSkill.nextInt(2) + 1) : ligneSource.get(i));
                        break;
                    case 3:
                        if (ligneSource.get(i + 1) == ":".getBytes()[0])
                            ordreBassin.get(ordreBassin.size() - 1).add(new ArrayList<>());
                        else if (ligneSource.get(i) == ",".getBytes()[0])
                            ordreBassin.add(new ArrayList<>());
                        else if (ligneSource.get(i) != ":".getBytes()[0])
                            ordreBassin.get(ordreBassin.size() - 1).get(ordreBassin.get(ordreBassin.size() - 1).size() - 1).add(ligneSource.get(i));
                        break;
                    case 4:
                        if (ligneSource.get(i) == "?".getBytes()[0])
                            soleil = 1;
                        else if (i + 1 < ligneSource.size() && ligneSource.get(i + 1) == ":".getBytes()[0])
                            ordreCarte.get(ordreCarte.size() - 1).get(soleil).add(new ArrayList<>());
                        else if (ligneSource.get(i) == ",".getBytes()[0]) {
                            ordreCarte.add(new ArrayList<>());
                            for (int k = 0; k != 2; ++k)
                                ordreCarte.get(ordreCarte.size() - 1).add(new ArrayList<>());
                            soleil = 0;
                        } else if (ligneSource.get(i) != ":".getBytes()[0])
                            ordreCarte.get(ordreCarte.size() - 1).get(soleil).get(ordreCarte.get(ordreCarte.size() - 1).get(soleil).size() - 1).add(ligneSource.get(i));
                        break;

                }
                ++j;
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
            int maxPdg = 0, minPdg = 1000;
            for (int i = 0; i != x; ++i) {
                bytes[i] = buf.get(i);
                if (bytes[i] == "@".getBytes()[0]){
                    int pdg = trouverPDG(bytes, i);
                    if (pdg > maxPdg) maxPdg = pdg;
                    if (pdg < minPdg) minPdg = pdg;
                }
            }
            List<StatLine> byteList = new ArrayList<>();
            int curseur = -1;
            for (int i = 0; i != x; ++i){
                if (bytes[i] == "@".getBytes()[0]){
                    int pdg = trouverPDG(bytes, i);
                    if (pdg > (maxPdg*6+minPdg)/7){//Critere de selection des lignes pour la génération suivante
                        byte[] byteLigne = new byte[i-curseur];
                        for (++curseur; curseur != i; ++curseur)
                            byteLigne[curseur-(i-byteLigne.length)-1] = bytes[curseur];
                        byteList.add(new StatLine(byteLigne));
                    }
                    curseur = i;
                }
            }
            SourceLines lignesSources = new SourceLines(byteList);
            for (List<Byte> sourceByte:lignesSources.getLigne()){
                buf = ByteBuffer.allocate(sourceByte.size());
                buf.clear();
                for (byte b:sourceByte)
                    buf.put(b);
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
            channel.write(buf, nbrJoueur-2);
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
        if (numManche == 1)
            gettingGood();
        boolean secondeAction = (numeroManche == numManche-1);
        numeroManche = numManche-1;
        Action actionChoisi = null;
        int numChoixAction = 0;
        if (gen < 1)
            numChoixAction = notLuckButSkill.nextInt(2) + 1;
        else if (!secondeAction)
            numChoixAction = choixAction[numeroManche];
        else
            numChoixAction = choixSecondeAction[numeroManche];
        if (numChoixAction == 0)
            throw new DiceForgeException("MLGBot","choixAction toujours pas bon");
        switch (numChoixAction) {
            case 1:
                actionChoisi = Action.FORGER;
                break;
            case 2:
                actionChoisi = Action.EXPLOIT;
                break;
            case 3:
                actionChoisi = Action.PASSER;
                break;
        }
        if (secondeAction)
            choixSecondeActionNext[numeroManche] = (byte) numChoixAction;
        else
            choixActionNext[numeroManche] = (byte) numChoixAction;
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
        if (gen > 0){
            if (ordreBassin.get(numeroManche).size()*approxOr > getOr()){
                for (byte b:ordreBassin.get(numeroManche).get(getOr()/approxOr)){
                    for (int i = 0; i != bassins.size(); ++i)
                        if (getPlateau().getTemple().getSanctuaire()[b-1].toString().equals(bassins.get(i).toString())) {
                            numBassin = i;
                            break;
                        }
                }
            }
        }
        if (bassins.get(numBassin).getCout() != 0 && pasMiroir) {
            for (int i = 0; i != getPlateau().getTemple().getSanctuaire().length; ++i)
                if (getPlateau().getTemple().getSanctuaire()[i].toString().equals(bassins.get(numBassin).toString()))
                    choixBassinNext.add((byte)(i+1));
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
        if (gen > 0){
            int soleilOuLune = (getSoleil() >= getLune() ? 0 : 1);
            if (ordreCarte.get(numeroManche).get(soleilOuLune).size()*approxRessource > (soleilOuLune == 0 ? getSoleil() : getLune())){
                for (byte b:ordreCarte.get(numeroManche).get(soleilOuLune).get((soleilOuLune == 0 ? getSoleil() : getLune())/approxRessource)){
                    for(int i = 0; i != cartes.size(); ++i){
                        if (NomCarteOverride.values()[b-1].toString().equals(cartes.get(i).toString())) {
                            numCarte = i;
                            break;
                        }
                    }
                }
            }
        }
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

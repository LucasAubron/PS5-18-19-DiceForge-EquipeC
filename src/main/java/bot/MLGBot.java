package bot;

import bot.mlgBot.SourceLines;
import bot.mlgBot.StatLine;
import bot.mlgBot.TrierStatLine;
import diceForge.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MLGBot extends Joueur {
    private Random notLuckButSkill = new Random();
    private int numeroManche = -1;//On est jamais mieux servi que par soi même
    private int getOnMyLevel = -1;
    private boolean MOVEMENT = true;
    private byte[] choixActionNext;//0 = forger, 1 = exploit, 2 = passer
    private List<List<List<Byte>>> choixAction;//Manche(Or(Action))
    private byte[] choixActionOr;
    private byte[] choixCarteNext;
    private byte[] puissanceSoleil;
    private byte[] puissanceLune;
    private List<List<List<List<Byte>>>> ordreCarte;//Manche(Soleil/Lune(Quantite(Cartes)))
    private int approxRessource = 1;
    private List<Byte> choixBassinNext = new ArrayList<>();
    private List<Byte> choixBassinManche = new ArrayList<>();
    private List<Byte> puissanceOr = new ArrayList<>();
    private List<List<List<Byte>>> ordreBassin;//Manche(QuantiteOr(Bassin))
    private int approxOr = 1;
    private boolean intensiveTraining = false;//METTRE A TRUE POUR ACTIVER L ALGORITHME GENETIQUE-----------------SUPPRIMER LES FICHIERS GENERES AVANT DE COMMIT
    private String cible = "";
    private int gen = -2;
    private boolean estRandom = false;//notLuckButSkill.nextInt(4) != 0;
    private long positionCHEF = -1;
    private int scoreMin = 100;
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
            if (file.length() == 0){
                String s = "0:0;0:0;0:0";
                ByteBuffer buf = ByteBuffer.allocate(s.getBytes().length);
                buf.clear();
                buf.put(s.getBytes());
                buf.flip();
                channel.write(buf);
                gen = 0;
            }
            else {
                ByteBuffer buf = ByteBuffer.allocate((int)file.length());
                buf.clear();
                int x = channel.read(buf);
                String[] s = new String(buf.array()).split(";");
                String[] stats = s[nbrJoueur-2].split(":");
                gen = Integer.parseInt(stats[0]);
            }
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
                if (positionCHEF > 70*100)//Nbr de charactere avant de passer à la génération suivante(Environ 70 characteres/partie)
                    remuerLaSoupe(nbrJoueur);
            }
        }
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            SourceLines ligneSource = new SourceLines("src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1), nbrJoueur, false);
            choixAction = ligneSource.getChoixAction();
            ordreBassin = ligneSource.getOrdreBassin();
            ordreCarte = ligneSource.getOrdreCarte();
        }
        choixActionNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixActionOr = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixCarteNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        puissanceSoleil = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        puissanceLune = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
    }

    private int trouverPDG(byte[] bytes, int pos){
        List<Integer> pdg = new ArrayList<>();
        for (int j = pos-1; bytes[j] != ";".getBytes()[0] && bytes[j] != "@".getBytes()[0]; --j)
            pdg.add(Integer.parseInt(new String(new byte[]{bytes[j]})));
        int reelPdg = 0;
        for (int j = 0; j != pdg.size(); ++j)
            reelPdg += pdg.get(j)*Math.pow(10, j);
        return reelPdg;
    }

    private void remuerLaSoupe(int nbrJoueur){
        int moyenneParties = 0;
        int moyennePrecedente = 0;
        try {
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int) file.length());
            buf.clear();
            int x = channel.read(buf);
            String s = new String(buf.array());
            moyennePrecedente = Integer.parseInt(s.split(";")[nbrJoueur-2].split(":")[1])-2;
            file.close();


            file = new RandomAccessFile(cible, "rw");
            channel = file.getChannel();
            byte[] bytes = new byte[(int) file.length()];
            buf = ByteBuffer.allocate((int) file.length());
            buf.clear();
            x = channel.read(buf);
            if (x != file.length())
                throw new DiceForgeException("MLGBot.java", "Le buffer n'a pas lu tout le fichier");
            file.setLength(0);
            int maxPdg = 0, minPdg = 1000, nbrPartie = 0, somme = 0;
            for (int i = 0; i != x; ++i) {
                bytes[i] = buf.get(i);
                if (bytes[i] == "@".getBytes()[0]) {
                    int pdg = trouverPDG(bytes, i);
                    if (pdg > scoreMin) {
                        ++nbrPartie;
                        somme += pdg;
                    }
                    if (pdg > maxPdg) maxPdg = pdg;
                    if (pdg < minPdg) minPdg = pdg;
                }
            }
            moyenneParties = (int)(somme / (float) nbrPartie);
            if (moyenneParties > moyennePrecedente || gen < 2) {
                List<StatLine> byteList = new ArrayList<>();
                int curseur = -1;
                for (int i = 0; i != x; ++i) {
                    if (bytes[i] == "@".getBytes()[0]) {
                        int pdg = trouverPDG(bytes, i);
                        if (pdg >= (maxPdg-8)) {//Critere de selection des lignes pour la génération suivante
                            byte[] byteLigne = new byte[i - curseur];
                            for (++curseur; curseur != i; ++curseur)
                                byteLigne[curseur - (i - byteLigne.length) - 1] = bytes[curseur];
                            byteList.add(new StatLine(byteLigne, pdg));
                        }
                        curseur = i;
                    }
                }
                //System.out.println("Nombre de parties retenues: "+byteList.size()+", points de gloire max: "+maxPdg);
                SourceLines lignesSource;
                Collections.sort(byteList, new TrierStatLine());
                if (gen > 1)
                    lignesSource = new SourceLines(byteList, "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1), nbrJoueur);
                else
                    lignesSource = new SourceLines(byteList);
                buf = ByteBuffer.allocate(lignesSource.getLigne().size());
                buf.clear();
                for (byte b : lignesSource.getLigne())
                    buf.put(b);
                buf.flip();
                channel.write(buf, file.length());
            }
            else {
                file.setLength(0);
                positionCHEF = 0;
            }
            file.close();
        } catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        if (moyenneParties > moyennePrecedente || gen < 2) {
            //Partie update des fichiers
            ++gen;
            cible = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + gen;
            if (gen > 29) {
                String cibleSuppr = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 30);
                File delFile = new File(cibleSuppr);
                delFile.delete();
            }
            File newFile = new File(cible);
            positionCHEF = 0;
            try {
                newFile.createNewFile();
                RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
                FileChannel channel = file.getChannel();
                ByteBuffer buf = ByteBuffer.allocate((int) file.length());
                buf.clear();
                int x = channel.read(buf);
                file.setLength(0);
                String[] s = new String(buf.array()).split(";");
                s[nbrJoueur - 2] = gen + ":" + moyenneParties;
                String update = "";
                for (int i = 0; i != s.length; ++i) {
                    update += s[i];
                    if (i != s.length-1)
                        update += ";";
                }
                buf = ByteBuffer.allocate(update.getBytes().length);
                buf.clear();
                buf.put(update.getBytes());
                buf.flip();
                channel.write(buf, 0);
                file.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        else {
            File aSuppr = new File(cible);
            aSuppr.delete();
            try{
                String cibleSuppr = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1);
                cible = cibleSuppr;
                RandomAccessFile file = new RandomAccessFile(cibleSuppr, "rw");
                file.setLength(0);
                file.close();
                file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
                FileChannel channel = file.getChannel();
                ByteBuffer buf = ByteBuffer.allocate((int) file.length());
                buf.clear();
                int x = channel.read(buf);
                file.setLength(0);
                String[] s = new String(buf.array()).split(";");
                String[] ss = s[nbrJoueur-2].split(":");
                ss[0] = ""+(gen-1);
                s[nbrJoueur-2] = ss[0] +":"+ ss[1];
                String update = "";
                for (int i = 0; i != s.length; ++i) {
                    update += s[i];
                    if (i != s.length-1)
                        update += ";";
                }
                buf = ByteBuffer.allocate(update.getBytes().length);
                buf.clear();
                buf.put(update.getBytes());
                buf.flip();
                channel.write(buf, 0);
                file.close();
                positionCHEF = 0;
                gen -= 1;
            } catch (IOException ex){
                ex.printStackTrace();
                System.exit(1);
            }
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
        if (intensiveTraining && (pwned() > scoreMin || gen <= 1)) {
            ByteBuffer buffer = ByteBuffer.allocate(128);
            buffer.clear();
            if (choixActionNext.length < 9 || choixActionNext.length > 10)
                throw new DiceForgeException("MLGBot","Le nombre de manche est incorrect. Min: 9, max: 10, actuel: "+choixActionNext.length);
            for (int i = 0; i != choixActionNext.length; ++i) {
                buffer.put(choixActionNext[i]);
                buffer.put(choixActionOr[i]);
            }
            buffer.put(";".getBytes());
            for (int i = 0; i != choixBassinNext.size(); ++i) {
                buffer.put(choixBassinNext.get(i));
                buffer.put(choixBassinManche.get(i));
                buffer.put(puissanceOr.get(i));
            }
            buffer.put(";".getBytes());
            for (int i = 0; i != choixActionNext.length; ++i){
                if (choixCarteNext[i] != 0){
                    buffer.put(choixCarteNext[i]);
                    buffer.put(puissanceSoleil[i]);
                    buffer.put(puissanceLune[i]);
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
        if (numeroManche > 7 && numeroManche == choixActionNext.length-1) {
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

    private int meilleurDe(De[] des) {
        int[] puissanceDe = new int[des.length];
        for (int i = 0; i != des.length; ++i){
            for (int j = 0; j != des[i].getFaces().length; ++j) {
                if (des[i].getFaces()[j].getRessource().length != 0) {
                    for (int k = 0; k != des[i].getFaces()[j].getRessource()[0].length; ++k) {
                        if (des[i].getFaces()[j].getRessource()[0][k] instanceof Or)
                            puissanceDe[i] += des[i].getFaces()[j].getRessource()[0][k].getQuantite();
                        else if (des[i].getFaces()[j].getRessource()[0][k] instanceof Soleil || des[i].getFaces()[j].getRessource()[0][k] instanceof Lune)
                            puissanceDe[i] += des[i].getFaces()[j].getRessource()[0][k].getQuantite()*3;
                    }
                }
            }
        }
        return puissanceDe[0] > puissanceDe[1] ? 0 : 1;
    }

    private int meilleurFace(List<Face> faces){
        int[] puissanceFace = new int[faces.size()];
        for (int i = 0; i != faces.size(); ++i){
            if (faces.get(i).getRessource().length != 0){
                for (int j = 0; j != faces.get(i).getRessource()[0].length; ++j){
                    if (faces.get(i).getRessource()[0][j] instanceof Or)
                        puissanceFace[i] += faces.get(i).getRessource()[0][j].getQuantite();
                    else if (faces.get(i).getRessource()[0][j] instanceof Soleil || faces.get(i).getRessource()[0][j] instanceof Lune)
                        puissanceFace[i] += faces.get(i).getRessource()[0][j].getQuantite();
                }
            }
        }
        int puissanceMax = 0;
        for (int i = 0; i != puissanceFace.length; ++i)
            if (puissanceFace[i] > puissanceMax)
                puissanceMax = i;
        return puissanceMax;
    }

    private int[] premiereFaceUnOr(De[] des){
        int numDe = notLuckButSkill.nextInt(des.length), posFace = notLuckButSkill.nextInt(des[0].getFaces().length);
        boolean stop = false;
        for (int i = 0; i != des.length && !stop; ++i){
            for (int j = 0; j != des[i].getFaces().length; ++j){
                if (des[i].getFaces()[j].getRessource().length != 0 && des[i].getFaces()[j].getRessource()[0][0] instanceof Or && des[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    numDe = i;
                    posFace = j;
                    stop = true;
                    break;
                }
            }
        }
        return new int[]{numDe, posFace};
    }

    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\

    @Override
    public Action choisirAction(int numManche){
        if (numManche == 1)
            gettingGood();
        boolean secondeAction = false;
        if (numeroManche == numManche -1)
            secondeAction = true;
        numeroManche = numManche-1;
        Action actionChoisi = null;
        int numChoixAction = notLuckButSkill.nextInt(2)+1;
        if (gen > 0 && (!estRandom || !intensiveTraining) && !secondeAction) {
            int max = (choixAction.get(numeroManche).size() > getOr() / approxOr ? getOr() / approxOr : choixAction.get(numeroManche).size()-1);
            if (max != -1) {
                for (int i = max; i != 0; --i) {
                    if (!choixAction.get(numeroManche).get(i).isEmpty()) {
                        numChoixAction = choixAction.get(numeroManche).get(i).get(0);
                        break;
                    }
                }
            }
        }else if (gen == 0){
            if (numeroManche < 5 && getOr() > 5)
                numChoixAction = 1;
            else numChoixAction = 2;
        }
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
        if (!secondeAction) {
            choixActionNext[numeroManche] = (byte) numChoixAction;
            choixActionOr[numeroManche] = (byte)(getOr()/approxOr);
        }
        MOVEMENT = true;
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
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            int max = (ordreBassin.get(numeroManche).size() * approxOr > getOr() ? getOr() / approxOr : ordreBassin.get(numeroManche).size() - 1);
            if (max != -1) {
                boolean continuer = true;
                for (int j = max; j != 0 && continuer; --j) {
                    for (byte b : ordreBassin.get(numeroManche).get(j)) {
                        for (int i = 0; i != bassins.size(); ++i) {
                            if (getPlateau().getTemple().getSanctuaire()[b - 1].toString().equals(bassins.get(i).toString())) {
                                numBassin = i;
                                continuer = false;
                                break;
                            }
                        }
                        if (!continuer) break;
                    }
                }
            }
        }
        if (bassins.get(numBassin).getCout() != 0 && pasMiroir) {
            for (int i = 0; i != getPlateau().getTemple().getSanctuaire().length; ++i)
                if (getPlateau().getTemple().getSanctuaire()[i].toString().equals(bassins.get(numBassin).toString()))
                    choixBassinNext.add((byte) (i + 1));
            choixBassinManche.add((byte)numeroManche);
            puissanceOr.add((byte)getOr());
            if (choixBassinNext.get(choixBassinNext.size()-1) == 0 || puissanceOr.size() != choixBassinNext.size())
                throw new DiceForgeException("MLGBot", "Bassin mal detecte. Nom : " + bassins.get(numBassin).toString());
            MOVEMENT = true;
        }
        int[] choixDe = premiereFaceUnOr(getDes());
        while (getDes()[choixDe[0]].getFaces()[choixDe[1]] instanceof FaceSanglier){
            choixDe[0] = notLuckButSkill.nextInt(getDes().length);
            choixDe[1] = notLuckButSkill.nextInt(getDes()[choixDe[0]].getFaces().length);
        }
        gettingGood();
        if (!bassins.isEmpty())
            return new ChoixJoueurForge(bassins.get(numBassin), 0, choixDe[0], choixDe[1]);
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        int numCarte = notLuckButSkill.nextInt(cartes.size());
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            int soleilOuLune = (getSoleil() >= getLune() ? 0 : 1);
            int max = (ordreCarte.get(numeroManche).get(soleilOuLune).size() / approxRessource > (soleilOuLune == 0 ? getSoleil() : getLune()) ? (soleilOuLune == 0 ? getSoleil() : getLune()) : ordreCarte.get(numeroManche).get(soleilOuLune).size() - 1);
            if (max != -1) {
                boolean continuer = true;
                for (int j = max; j != 0 && continuer; --j) {
                    for (byte b : ordreCarte.get(numeroManche).get(soleilOuLune).get(j)) {
                        for (int i = 0; i != cartes.size(); ++i) {
                            if (NomCarteOverride.values()[b - 1].toString().equals(cartes.get(i).toString())) {
                                numCarte = i;
                                choixCarteNext[numeroManche] = b;
                                puissanceSoleil[numeroManche] = (byte)getSoleil();
                                puissanceLune[numeroManche] = (byte)getLune();
                                continuer = false;
                                break;
                            }
                        }
                        if (!continuer) break;
                    }
                }
            }
        }
        else if (gen == 0){
            for (int i = 0; i != cartes.size(); ++i){
                if (cartes.get(i).getNom().equals(Carte.Noms.Marteau) && !possedeCarte(Carte.Noms.Marteau)){
                    numCarte = i;
                    break;
                }
                else if (cartes.get(i).getNom().equals(Carte.Noms.Coffre) && !possedeCarte(Carte.Noms.Coffre)){
                    numCarte = i;
                    break;
                }
            }
        }
        if (choixCarteNext[numeroManche] == 0) {
            for (int i = 0; i != NomCarteOverride.values().length; ++i) {
                if (NomCarteOverride.values()[i].toString().equals(cartes.get(numCarte).getNom().toString())) {
                    choixCarteNext[numeroManche] = (byte) (i + 1);
                    puissanceSoleil[numeroManche] = (byte)getSoleil();
                    puissanceLune[numeroManche] = (byte)getLune();
                    break;
                }
            }
        }
        if (choixCarteNext[numeroManche] == 0)
            throw new DiceForgeException("MLGBot", "Carte non detecte. Nom : "+cartes.get(numCarte).getNom().toString());
        MOVEMENT = true;
        gettingGood();
        return cartes.get(numCarte);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        gettingGood();
        return getOr() > 5 || getSoleil() > 3 || getLune() > 1;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr) {
        int orGarde = 0;
        if (getOr() < 3 && possedeCarte(Carte.Noms.Ancien))
            orGarde = 3 - getOr();
        gettingGood();
        return orGarde;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts){
        gettingGood();
        return renforts;
    }

    @Override
    public int choisirRessource(Face face){
        gettingGood();
        return notLuckButSkill.nextInt(face.getRessource().length);
    }

    @Override
    public int choisirRessourceAPerdre(Face face){
        gettingGood();
        return notLuckButSkill.nextInt(face.getRessource().length);
    }

    @Override
    public int choisirDeFaveurMineure(){
        gettingGood();
        return meilleurDe(getDes());
    }

    @Override
    public int choisirDeCyclope(){
        gettingGood();
        return meilleurDe(getDes());
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        gettingGood();
        return getIdentifiant() == 1 ? 2 : 1;
    }

    @Override
    public void forgerFace(Face face){
        int[] choixDe = premiereFaceUnOr(getDes());
        while (getDes()[choixDe[0]].getFaces()[choixDe[1]] instanceof FaceSanglier){
            choixDe[0] = notLuckButSkill.nextInt(getDes().length);
            choixDe[1] = notLuckButSkill.nextInt(getDes()[choixDe[0]].getFaces().length);
        }
        forgerDe(choixDe[0], face, choixDe[1]);
        gettingGood();
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        gettingGood();
        return meilleurFace(faces);
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
        return (ressource instanceof Or || numeroManche >= 8);
    }
}

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

/**
 * Ce bot n'est pas fait pour être lisible, compréhensible ou maintenable,
 * il est fait pour tester le concept d'un bot majoritairement génétique
 * développé en peu de temps et ensuite de repartir à zéro pour le bot ultime
 * si ce bot fonctionne.
 */
public class MLGBot extends Joueur {

    private enum Strategies{Randomized, Hard, Copy}
    private Strategies strat = Strategies.Randomized;

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
    private boolean secondeAction = false;

    private Joueur joueurCible;
    private Face[] facesJoueurCible;
    private List<Carte> cartesJoueurCible;

    public MLGBot(int identifiant, Afficheur afficheur, Plateau plateau) {
        super(identifiant, afficheur, plateau);
    }

    enum NomCarteOverride {
        Ancien, HerbesFolles, Hibou, Minautore, BateauCeleste, Bouclier, Meduse, MiroirAbyssal, Triton, Cyclope, Sphinx, Hydre, Typhon,//On ne prend on compte que les soleils pour hydre et typhon
        Marteau, Coffre, Biche, Ours, Sanglier, Satyres, Cerbere, Passeur, CasqueDinvisibilite, Sentinelle, Cancer
    }

    private void goGoGo(int nbrJoueur) {
        String fichierProp = "src\\main\\java\\bot\\mlgBot\\MLGBotProp";
        File testFichierProp = new File(fichierProp);
        if (!testFichierProp.exists()) {
            try {
                testFichierProp.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        try {
            RandomAccessFile file = new RandomAccessFile(fichierProp, "rw");
            FileChannel channel = file.getChannel();
            if (file.length() == 0) {
                String s = "0:0;0:0;0:0";
                ByteBuffer buf = ByteBuffer.allocate(s.getBytes().length);
                buf.clear();
                buf.put(s.getBytes());
                buf.flip();
                channel.write(buf);
                gen = 0;
            } else {
                ByteBuffer buf = ByteBuffer.allocate((int) file.length());
                buf.clear();
                int x = channel.read(buf);
                String[] s = new String(buf.array()).split(";");
                String[] stats = s[nbrJoueur - 2].split(":");
                gen = Integer.parseInt(stats[0]);
                if (gen > 0)
                    scoreMin = Integer.parseInt(stats[1])-8;
            }
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        cible = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + gen;
        File genFile = new File(cible);
        if (!genFile.exists()) {
            if (intensiveTraining) {
                try {
                    genFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
                positionCHEF = 0;
            }
        } else {
            positionCHEF = genFile.length();
            if (intensiveTraining) {
                if (positionCHEF > 70 * 300 * (gen == 0 ? 10 : 1))//Nbr de charactere avant de passer à la génération suivante(Environ 70 characteres/partie)
                    remuerLaSoupe(nbrJoueur);
            }
        }
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            SourceLines ligneSource = new SourceLines("src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1), nbrJoueur, !intensiveTraining);
            choixAction = ligneSource.getChoixAction();
            if (intensiveTraining) {
                ordreBassin = ligneSource.getOrdreBassinAvecMutation();
                ordreCarte = ligneSource.getOrdreCarteAvecMutation();
            }
            else{
                ordreBassin = ligneSource.getOrdreBassin();
                ordreCarte = ligneSource.getOrdreCarte();
            }
        }
        choixActionNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixActionOr = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        choixCarteNext = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        puissanceSoleil = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        puissanceLune = new byte[getPlateau().getJoueurs().size() == 3 ? 10 : 9];
        if (gen == 0)
            scoreMin = 100;
    }

    private int trouverPDG(byte[] bytes, int pos) {
        List<Integer> pdg = new ArrayList<>();
        for (int j = pos - 1; bytes[j] != ";".getBytes()[0] && bytes[j] != "@".getBytes()[0]; --j)
            pdg.add(Integer.parseInt(new String(new byte[]{bytes[j]})));
        int reelPdg = 0;
        for (int j = 0; j != pdg.size(); ++j)
            reelPdg += pdg.get(j) * Math.pow(10, j);
        return reelPdg;
    }

    private void remuerLaSoupe(int nbrJoueur) {
        int moyenneParties = 0;
        int moyennePrecedente = 0;
        try {
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBotProp", "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int) file.length());
            buf.clear();
            int x = channel.read(buf);
            String s = new String(buf.array());
            moyennePrecedente = Integer.parseInt(s.split(";")[nbrJoueur - 2].split(":")[1]) - 20;
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
            moyenneParties = (int) (somme / (float) nbrPartie);
            if (moyenneParties > moyennePrecedente || gen < 2) {
                List<StatLine> byteList = new ArrayList<>();
                int curseur = -1;
                for (int i = 0; i != x; ++i) {
                    if (bytes[i] == "@".getBytes()[0]) {
                        int pdg = trouverPDG(bytes, i);
                        //if (pdg >= (maxPdg - (gen == 0 ? 100 : 10))) {//Critere de selection des lignes pour la génération suivante
                            byte[] byteLigne = new byte[i - curseur];
                            for (++curseur; curseur != i; ++curseur)
                                byteLigne[curseur - (i - byteLigne.length) - 1] = bytes[curseur];
                            byteList.add(new StatLine(byteLigne, pdg));
                        //}
                        curseur = i;
                    }
                }
                //System.out.println("Nombre de parties retenues: "+byteList.size()+", points de gloire max: "+maxPdg);
                SourceLines lignesSource;
                Collections.sort(byteList, new TrierStatLine());
                if (gen >= 1)
                    lignesSource = new SourceLines(byteList, "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 1), nbrJoueur);
                else
                    lignesSource = new SourceLines(byteList);
                buf = ByteBuffer.allocate(lignesSource.getLigne().size());
                buf.clear();
                for (byte b : lignesSource.getLigne())
                    buf.put(b);
                buf.flip();
                channel.write(buf, file.length());
            } else {
                file.setLength(0);
                positionCHEF = 0;
            }
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        if (moyenneParties > moyennePrecedente || gen < 2) {
            //Partie update des fichiers
            ++gen;
            cible = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + gen;
            if (gen > 2) {
                String cibleSuppr = "src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + (gen - 3);
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
                    if (i != s.length - 1)
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
        } else {
            File aSuppr = new File(cible);
            aSuppr.delete();
            try {
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
                String[] ss = s[nbrJoueur - 2].split(":");
                ss[0] = "" + (gen - 1);
                s[nbrJoueur - 2] = ss[0] + ":" + ss[1];
                String update = "";
                for (int i = 0; i != s.length; ++i) {
                    update += s[i];
                    if (i != s.length - 1)
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
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    private int pwned() {
        int pdg = getPointDeGloire();
        for (Carte carte : getCartes()) {
            pdg += carte.getNbrPointGloire();
            if (carte.getNom() == Carte.Noms.Typhon)
                for (De de : getDes())
                    pdg += de.getNbrFaceForge();
        }
        return pdg;
    }

    private void Xx360xX_NoScope() {
        if (intensiveTraining && pwned() > scoreMin) {
            ByteBuffer buffer = ByteBuffer.allocate(128);
            buffer.clear();
            if (choixActionNext.length < 9 || choixActionNext.length > 10)
                throw new DiceForgeException("MLGBot", "Le nombre de manche est incorrect. Min: 9, max: 10, actuel: " + choixActionNext.length);
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
            for (int i = 0; i != choixActionNext.length; ++i) {
                if (choixCarteNext[i] != 0) {
                    buffer.put(choixCarteNext[i]);
                    buffer.put(puissanceSoleil[i]);
                    buffer.put(puissanceLune[i]);
                }
                if (i != choixActionNext.length - 1)
                    buffer.put(",".getBytes());
            }

            buffer.put((";" + pwned() + "@").getBytes());
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

    private void gettingGood() {
        if (gen == -2)
            goGoGo(getPlateau().getJoueurs().size());
        if (numeroManche > 7 && numeroManche == choixActionNext.length - 1) {
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
        for (int i = 0; i != des.length; ++i) {
            for (int j = 0; j != des[i].getFaces().length; ++j) {
                if (des[i].getFaces()[j].getRessource().length != 0) {
                    for (int k = 0; k != des[i].getFaces()[j].getRessource()[0].length; ++k) {
                        if (des[i].getFaces()[j].getRessource()[0][k] instanceof Or)
                            puissanceDe[i] += des[i].getFaces()[j].getRessource()[0][k].getQuantite();
                        else if (des[i].getFaces()[j].getRessource()[0][k] instanceof Soleil || des[i].getFaces()[j].getRessource()[0][k] instanceof Lune)
                            puissanceDe[i] += des[i].getFaces()[j].getRessource()[0][k].getQuantite() * 3;
                    }
                }
            }
        }
        return puissanceDe[0] > puissanceDe[1] ? 0 : 1;
    }

    private int meilleurFace(List<Face> faces) {
        int[] puissanceFace = new int[faces.size()];
        for (int i = 0; i != faces.size(); ++i) {
            if (faces.get(i).getRessource().length != 0) {
                for (int j = 0; j != faces.get(i).getRessource()[0].length; ++j) {
                    if (faces.get(i).getRessource()[0][j] instanceof Or)
                        puissanceFace[i] += faces.get(i).getRessource()[0][j].getQuantite();
                    else if (faces.get(i) instanceof FaceX3)
                        puissanceFace[i] -= 6;
                    else if (faces.get(i) instanceof FaceMiroirAbyssal)
                        puissanceFace[i] += 5;
                    else if  (faces.get(i).getRessource()[0][j] instanceof Soleil && faces.get(i).getRessource()[0][j].getQuantite() + getSoleil() <= getMaxSoleil())
                        puissanceFace[i] += faces.get(i).getRessource()[0][j].getQuantite()*4;
                    else if  (faces.get(i).getRessource()[0][j] instanceof Lune && faces.get(i).getRessource()[0][j].getQuantite() + getLune() <= getMaxLune())
                        puissanceFace[i] += faces.get(i).getRessource()[0][j].getQuantite()*3;
                }
            }
        }
        int puissanceMax = 0;
        int id = 0;
        for (int i = 0; i != puissanceFace.length; ++i)
            if (puissanceFace[i] > puissanceMax) {
                puissanceMax = puissanceFace[i];
                id = i;
            }
        return id;
    }

    private int[] pirePremiereFace(De[] des) {
        int numDe = notLuckButSkill.nextInt(des.length), posFace = notLuckButSkill.nextInt(des[0].getFaces().length);
        boolean stop = false;
        for (int i = 0; i != des.length && !stop; ++i) {
            for (int j = 0; j != des[i].getFaces().length; ++j) {
                if (des[i].getFaces()[j].getRessource().length != 0 && des[i].getFaces()[j].getRessource()[0][0] instanceof Or && des[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1) {
                    numDe = i;
                    posFace = j;
                    stop = true;
                    break;
                }
                else if (des[i].getFaces()[j].getRessource().length != 0 &&
                        ((des[i].getFaces()[j].getRessource()[0][0] instanceof PointDeGloire &&
                                des[i].getFaces()[j].getRessource()[0][0].getQuantite() == 2) ||
                        (des[i].getFaces()[j].getRessource()[0][0] instanceof Lune &&
                                des[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1))){
                    numDe = i;
                    posFace = j;
                }
            }
        }
        return new int[]{numDe, posFace};
    }

    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\
    //------------------------------------------------------------------------------------------------------------------\\

    @Override
    public Action choisirAction(int numManche) {
        if (numManche == 1)
            gettingGood();
        if (numeroManche == numManche - 1)
            secondeAction = true;
        else
            secondeAction = false;
        numeroManche = numManche - 1;
        Action actionChoisi = null;
        int numChoixAction = notLuckButSkill.nextInt(2) + 1;
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            if (choixAction.get(numeroManche).size() > getOr() / approxOr && !choixAction.get(numeroManche).get(getOr() / approxOr).isEmpty()) {
                numChoixAction = choixAction.get(numeroManche).get(getOr() / approxOr).get(0);
            }
        }
        else if (gen == 0 && strat == Strategies.Hard){
            if (numManche < 6 && getOr() > 5)
                numChoixAction = 1;
            else if (getSoleil() > 0 || getLune() > 0)
                numChoixAction = 2;
        }
        if (numChoixAction == 0)
            throw new DiceForgeException("MLGBot", "choixAction toujours pas bon");
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
            choixActionOr[numeroManche] = (byte) (getOr() / approxOr);
        }
        MOVEMENT = true;
        gettingGood();
        return actionChoisi;
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche) {
        int numBassin = notLuckButSkill.nextInt(bassins.size());
        boolean pasBateau = true;
        for (Bassin bassin:bassins) {
            if (bassin.getCout() > getOr()) {
                pasBateau = false;
            }
        }
        int nbrOr = (pasBateau ? getOr() : getOr() + 2)/approxOr;
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            if (ordreBassin.get(numeroManche).size() > nbrOr) {
                for (byte b : ordreBassin.get(numeroManche).get(nbrOr)) {
                    boolean continuer = true;
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
        else if (gen == 0 && strat == Strategies.Hard) {
            boolean aChoisi = false;
            if (bassins.isEmpty())
                return new ChoixJoueurForge(null, 0, 0, 0);
            for (int i = 0; i != bassins.size(); ++i) {
                if (numManche < 3 && bassins.get(i).getFaces().get(0).getRessource()[0][0] instanceof Or) {
                    numBassin = i;
                    break;
                } else if (aChoisi && bassins.get(numBassin).getCout() < bassins.get(i).getCout())
                    numBassin = i;
                else if (!aChoisi) {
                    numBassin = i;
                    aChoisi = true;
                }
            }
        }

        if (bassins.get(numBassin).getCout() != 0 && pasBateau) {
            for (int i = 0; i != getPlateau().getTemple().getSanctuaire().length; ++i)
                if (getPlateau().getTemple().getSanctuaire()[i].toString().equals(bassins.get(numBassin).toString()))
                    choixBassinNext.add((byte) (i + 1));
            choixBassinManche.add((byte) numeroManche);
            puissanceOr.add((byte) getOr());
            if (choixBassinNext.get(choixBassinNext.size() - 1) == 0 || puissanceOr.size() != choixBassinNext.size())
                throw new DiceForgeException("MLGBot", "Bassin mal detecte. Nom : " + bassins.get(numBassin).toString());
            MOVEMENT = true;
        }
        int[] choixDe = pirePremiereFace(getDes());
        while (getDes()[choixDe[0]].getFaces()[choixDe[1]] instanceof FaceSanglier) {
            choixDe[0] = notLuckButSkill.nextInt(getDes().length);
            choixDe[1] = notLuckButSkill.nextInt(getDes()[choixDe[0]].getFaces().length);
        }
        gettingGood();
        if (!bassins.isEmpty())
            return new ChoixJoueurForge(bassins.get(numBassin), 0, choixDe[0], choixDe[1]);
        return null;
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche) {
        int numCarte = notLuckButSkill.nextInt(cartes.size());
        if (gen > 0 && (!estRandom || !intensiveTraining)) {
            boolean aChoisi = false;
            int soleilOuLune = (getSoleil() >= getLune() ? 0 : 1);
            int max = (soleilOuLune == 0 ? (getSoleil() >= 2 && !secondeAction && (getLune() > 2 || getSoleil() > 4)? getSoleil() - 2 : getSoleil()) / approxRessource : getLune() / approxRessource);
            if (getSoleil() >= 5 && getLune() >= 5){
                for (int i = 0; i != cartes.size(); ++i){
                    if (cartes.get(i).getNom() == Carte.Noms.Typhon || cartes.get(i).getNom() == Carte.Noms.Hydre){
                        numCarte = i;
                        aChoisi = true;
                        break;
                    }
                }
            }
            if (ordreCarte.get(numeroManche).get(soleilOuLune).size() / approxRessource > max && !aChoisi) {
                for (byte b : ordreCarte.get(numeroManche).get(soleilOuLune).get(max)) {
                    for (int i = 0; i != cartes.size(); ++i) {
                        if (NomCarteOverride.values()[b - 1].toString().equals(cartes.get(i).getNom().toString())) {
                            if (!(cartes.get(i).getNom() == Carte.Noms.Coffre && possedeCarte(Carte.Noms.Coffre)))
                            numCarte = i;
                            choixCarteNext[numeroManche] = b;
                            puissanceSoleil[numeroManche] = (byte) getSoleil();
                            puissanceLune[numeroManche] = (byte) getLune();
                            aChoisi = true;
                            break;
                        }
                    }
                    if (aChoisi) break;
                }
            }
        }
        else if (gen == 0 && strat == Strategies.Hard) {
            boolean aChoisi = false;
            for (int i = 0; i != cartes.size(); ++i) {
                if (cartes.get(i).getNom().equals(Carte.Noms.Marteau) && !possedeCarte(Carte.Noms.Marteau)) {
                    numCarte = i;
                    break;
                }
                if (cartes.get(i).getNom().equals(Carte.Noms.Coffre) && !possedeCarte(Carte.Noms.Coffre)) {
                    numCarte = i;
                    break;
                }
                if (aChoisi && cartes.get(numCarte).getCout()[0].getQuantite() < cartes.get(i).getCout()[0].getQuantite())
                    numCarte = i;
                else if (!aChoisi) {
                    numCarte = i;
                    aChoisi = true;
                }
            }
        }
        if (choixCarteNext[numeroManche] == 0) {
            for (int i = 0; i != NomCarteOverride.values().length; ++i) {
                if (NomCarteOverride.values()[i].toString().equals(cartes.get(numCarte).getNom().toString())) {
                    choixCarteNext[numeroManche] = (byte) (i + 1);
                    puissanceSoleil[numeroManche] = (byte) getSoleil();
                    puissanceLune[numeroManche] = (byte) getLune();
                    break;
                }
            }
        }
        if (choixCarteNext[numeroManche] == 0)
            throw new DiceForgeException("MLGBot", "Carte non detecte. Nom : " + cartes.get(numCarte).getNom().toString());
        MOVEMENT = true;
        gettingGood();
        return cartes.get(numCarte);
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche) {
        boolean prendreSecAction = notLuckButSkill.nextBoolean();
        if (getOr() > 10 && numeroManche < 6 && gen > 0){
            prendreSecAction = true;
            if (choixAction.get(numeroManche).size() > getOr()/approxOr && !choixAction.get(numeroManche).get(getOr()/approxOr).isEmpty())
                choixAction.get(numeroManche).get(getOr()/approxOr).set(0, (byte)1);
        }
        else if ((getSoleil() > 3 || getLune() > 1) && gen > 0){
            for (Ile ile:getPlateau().getIles()){
                for (List<Carte> carte:ile.getCartes()){
                    if (!carte.isEmpty() && ((carte.get(0).getCout()[0].getQuantite() <= getSoleil() - 2 && carte.get(0).getCout()[0] instanceof Soleil)
                            || (carte.get(0).getCout()[0].getQuantite() <= getLune() && carte.get(0).getCout()[0] instanceof Lune))){
                        prendreSecAction = true;
                        if (choixAction.get(numeroManche).size() > getOr()/approxOr && !choixAction.get(numeroManche).get(getOr()/approxOr).isEmpty())
                            choixAction.get(numeroManche).get(getOr()/approxOr).set(0, (byte)2);
                        break;
                    }
                }
                if (prendreSecAction) break;
            }
        }
        else if (gen == 0 && strat == Strategies.Hard){
            prendreSecAction = (getOr() > 10 && numeroManche < 6) || getSoleil() > 3 || getLune() > 1;
        }
        gettingGood();
        return prendreSecAction;
    }

    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr) {
        int orGarde = 0;
        int nbrAncien = 0;
        for (Carte carte:getCartes())
            if (carte.getNom().equals(Carte.Noms.Ancien))
                ++nbrAncien;
        orGarde = (getOr() < 3*nbrAncien ? 3*nbrAncien - getOr() : 0);
        gettingGood();
        return orGarde;
    }

    @Override
    public List<Renfort> choisirRenforts(List<Renfort> renforts) {
        gettingGood();
        return renforts;
    }

    @Override
    public int choisirRessource(Face face) {
        int choix = notLuckButSkill.nextInt(face.getRessource().length);
        for (int i = 0; i != face.getRessource().length; ++i) {
            for (int j = 0; j != face.getRessource()[i].length; ++j) {
                if ((face.getRessource()[i][j] instanceof Soleil && getSoleil() < getMaxSoleil() - 1) || (face.getRessource()[i][j] instanceof Lune && getLune() < getMaxLune() - 1))
                    choix = i;
            }
        }
        gettingGood();
        return choix;
    }

    @Override
    public int choisirRessourceAPerdre(Face face) {
        int choix = notLuckButSkill.nextInt(face.getRessource().length);
        for (int i = 0; i != face.getRessource().length; ++i) {
            for (int j = 0; j != face.getRessource()[i].length; ++j) {
                if (face.getRessource()[i][j] instanceof Or)
                    choix = i;
            }
        }
        gettingGood();
        return choix;
    }

    @Override
    public int choisirDeFaveurMineure() {
        gettingGood();
        return meilleurDe(getDes());
    }

    @Override
    public int choisirDeCyclope() {
        int choix;
        int[] puissance = new int[2];
        for (int i = 0; i != getDes().length; ++i){
            for (Face face:getDes()[i].getFaces()){
                if (face.getRessource().length != 0){
                    for (Ressource ressource:face.getRessource()[0])
                        if (ressource instanceof Or)
                            puissance[i] += ressource.getQuantite();
                }
            }
        }
        choix = (puissance[0] > puissance[1] ? 0 : 1);
        gettingGood();
        return choix;
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        int id = (getIdentifiant() == 1 ? 2 : 1);
        int minPdg = 500;
        for (int i = 0; i != getPlateau().getJoueurs().size(); ++i) {
            if (getPlateau().getJoueurs().get(i).getIdentifiant() != getIdentifiant() && getPlateau().getJoueurs().get(i).getPointDeGloire() < minPdg) {
                minPdg = getPlateau().getJoueurs().get(i).getPointDeGloire();
                id = getPlateau().getJoueurs().get(i).getIdentifiant();
            }
        }
        gettingGood();
        return id;
    }

    @Override
    public void forgerFace(Face face) {
        int[] choixDe = pirePremiereFace(getDes());
        while (getDes()[choixDe[0]].getFaces()[choixDe[1]] instanceof FaceSanglier) {
            choixDe[0] = notLuckButSkill.nextInt(getDes().length);
            choixDe[1] = notLuckButSkill.nextInt(getDes()[choixDe[0]].getFaces().length);
        }
        forgerDe(choixDe[0], face, choixDe[1]);
        gettingGood();
    }

    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces) {
        gettingGood();
        return meilleurFace(faces);
    }

    @Override
    public choixJetonTriton utiliserJetonTriton() {
        gettingGood();
        choixJetonTriton choix = choixJetonTriton.Rien;
        if (getSoleil() < getMaxSoleil() - 2)
            choix = choixJetonTriton.Soleil;
        else if (getLune() < getMaxLune() - 2)
            choix = choixJetonTriton.Lune;
        return choix;
    }

    @Override
    public boolean utiliserJetonCerbere() {
        boolean choix = false;
        int dernierDe = getDernierLanceDes();
        if (gen > 0 && numeroManche == choixAction.size() - 1)
            choix = true;
        else if (dernierDe == 2){
            for (De de:getDes()){
                if (de.derniereFace().getRessource().length != 0 && (de.derniereFace().getRessource()[0][0] instanceof Soleil || de.derniereFace().getRessource()[0][0] instanceof Lune)) {
                    choix = true;
                    break;
                }
            }
        }
        gettingGood();
        return choix;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        boolean choisiPdg = false;
        if (gen > 0 && numeroManche == choixAction.size() - 1)
            choisiPdg = true;
        else if (ressource instanceof Or)
            choisiPdg = true;
        else if (ressource instanceof Lune && getLune() == getMaxLune())
            choisiPdg = true;
        else if (ressource instanceof Soleil && getSoleil() == getMaxSoleil())
            choisiPdg = true;
        gettingGood();
        return choisiPdg;
    }

    @Override
    public String toString() {
        return "PlanteBot";
    }
}

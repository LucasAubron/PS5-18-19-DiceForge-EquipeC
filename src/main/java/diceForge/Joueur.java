package diceForge;

import java.util.ArrayList;

/**
 * Classe joueur. Ici on utilise plus d'objet pour les ressources, mais des variables distinctes.
 * Bien entendu ça peut changer. Pourquoi faire ça :
 * Parce la plupart des choses que l'on achete ne coute que d'une ressource, donc je pense
 * qu'avoir un unique tableau de ressource compliquerait les choses.
 * La classe ne doit contenir AUCUN élément d'un bot, la classe bot (il y en aura plusieurs) sera une classe à part.
 * Ainsi elle doit permettre d'avoir une grande communication avec l'extérieur
 * Chaque joueur possède un identifiant, allant de 0 à 3 (s'il y a 4 joueurs, sinon moins)
 * qui permet d'identifier le joueur par rapport au autre (un peu comme dans une base de donnée).
 * Cette classe est abstraite, on ne peut pas en faire un objet, il faut instancier un bot
 */
public abstract class Joueur {
    protected int or;
    protected int maxOr = 12;
    protected int soleil = 0;
    protected int maxSoleil = 6;
    protected int lune = 0;
    protected int maxLune = 6;
    protected int pointDeGloire = 0;
    protected int identifiant;
    protected De[] des;
    protected Face premierDeFaceCourante;
    protected Face deuxiemeDeFaceCourante;
    protected ArrayList<Carte> cartes;

    public enum Action {FORGER, EXPLOIT, PASSER}

    public Joueur(int indentifiant){
        if (identifiant < 0 || identifiant > 3)
            throw new RuntimeException("L'identifiant est invalide. Min : 0, max : 3, actuel : "+identifiant);
        this.identifiant = indentifiant;
        or = 3-identifiant;
        des = new De[]{new De(new Face[]{new Face(new Ressource[][]{{new Or(1)}}),
                new Face(new Ressource[][]{{new Soleil(1)}}),
                new Face(new Ressource[][]{{new PointDeGloire(1)}})})};//ON VA TOUS MOURRRRRIIIIRRR
    }

    public int getOr() {return or;}

    public void ajouterOr (int quantite){or = (or + quantite > maxOr) ? maxOr : or + quantite;}

    public int getSoleil() {return soleil;}

    public void ajouterSoleil(int quantite) {soleil = (soleil + quantite > maxSoleil) ? maxSoleil : soleil + quantite;}

    public int getLune() {return lune;}

    public void ajouterLune(int quantite) {lune = (lune + quantite > maxLune) ? maxLune : lune + quantite;}

    public int getPointDeGloire() {return pointDeGloire;}

    public int getIdentifiant() {return identifiant;}

    /**
     * C'est à partir d'ice qu'on lance les des, et que les problèmes arrivent...
     * Cette version ne marche que pour la version minimale, il faudra peut etre tout refaire /!\
     */
    public void lancerLesDes(){
        for (De de:des){
            Face face = de.lancerLeDe();
            if (de == des[0])
                this.premierDeFaceCourante = face;
            else
                this.deuxiemeDeFaceCourante = face;
            for (Ressource ressource:face.getRessource()[0]){
                if (ressource instanceof Or)
                    ajouterOr(ressource.getQuantite());
                else if (ressource instanceof Soleil)
                    ajouterSoleil(ressource.getQuantite());
                else if (ressource instanceof PointDeGloire)
                    pointDeGloire += ressource.getQuantite();
            }
        }
    }

    /**
     * Méthode à appeler lorsque le joueur est chassé
     * Elle servira surtout lorsque le sanglier sera introduit
     */
    public void estChasse(){
        lancerLesDes();
    }

    /**
     * Méthode à appeler lorsque le joueur en chasse un autre
     * Elle servira uniquement lorsque le sanglier sera introduit
     */
    public void chasse() {
    }

    public String printRessourcesEtDes(int numeroManche){
        String res = "Manche: " + numeroManche + " || Joueur: " + identifiant + "\n" ;
        res += "Res 1er dé: " + premierDeFaceCourante.toString() + "\t||\t" + "Res 2ème dé: Non implémenté en version minimale\n";
        res += "Or: " + or + "\t||\t" + "Soleil: " + soleil + "\t||\t" + "Lune: /" + "\t||\t" + "PointDeGloire: " + pointDeGloire + "\n";
        res += "--------------------------------------------------------------------------------------------------";
        return res;
    }

    /**
     * La méthode ne gére que la partie dépense et ingestion de la carte,
     * elle ne regarde pas si il reste de cette carte.
     * @param carte
     * @return true si la carte à pu être acheté, false sinon
     */
    public boolean acheterExploit(Carte carte){
        boolean estAcquise = true;
        for (Ressource ressource:carte.getCout()){
            if (ressource instanceof Soleil && ressource.getQuantite() <= soleil){
                soleil -= ressource.getQuantite();
                estAcquise = true;
            }
            else if (ressource instanceof Lune && ressource.getQuantite() <= lune){
                lune -= ressource.getQuantite();
            }
            else {//Si vous pensez pouvoir faire sans cela, pensez à l'hydre
                estAcquise = false;
                break;//Si vous pensez trouver un meilleur moyen, eh bien soyez sur que ça marche et implémenté le
            }
        }
        if (estAcquise) {
            pointDeGloire += carte.getNbrPointGloire();
            cartes.add(carte);
        }
        return estAcquise;
    }

    /**
     * Permet de forger une face sur un dé du joueur
     */
    public void forgerDe(int numDe, Face faceAForger, int numFace){
        if (numDe < 0 || numDe > 1)
            throw new RuntimeException("Le numéro du dé est invalide. Min : 0, max : 1, actuel : "+numDe);
        des[numDe].forger(faceAForger, numFace);
    }

    /**
     * C'est une classe abstraite, on est obligé de l'override dans une classe dérivée
     * @param numManche
     * @return L'action que le bot à choisi de prendre
     */
    public abstract Action choisirAction(int numManche);

    /**
     * Permet de forger une face sur le dé à partir de la liste des bassins affordables.
     * Il faut donc choisir un bassin et une face à l'intérieur de se bassin
     * @param bassins la liste des bassins affordables
     */
    public abstract void choisirFaceAForger(ArrayList<Bassin> bassins, int numManche);

    /**
     * Permet de choisir une carte parmis une liste de carte affordable
     * @return Le joueur chassé de l'ile (oui il se fait trimbaler partout lui)
     */
    public abstract Joueur choisirCarte(ArrayList<Carte> cartes, int numManche);

}

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
 */
public class Joueur {
    private int or;
    private int maxOr = 12;
    private int soleil = 0;
    private int maxSoleil = 6;
    private int lune = 0;
    private int maxLune = 6;
    private int pointDeGloire = 0;
    private int identifiant;
    private De[] des;
    private Face premierDeFaceCourante;//!\\Ca n'a rien à faire en attribut, il faudrait trouver un autre système
    private Face deuxiemeDeFaceCourante;//La même
    private ArrayList<Carte> cartes;

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

    public String printRessourcesEtDes(){
        String res = "Or: " + or + "\t\t\t\t1er Dé: " + premierDeFaceCourante.toString() + "\n";
        res = res + "Soleil: " + soleil + "\t\t\t2ème Dé: Non implémenté en version minimale\n" ; //+ deuxiemeDeFaceCourante.toString()
        res = res + "PointDeGloire: " + pointDeGloire + "\n";
        res = res + "--------------------------------------";
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
}

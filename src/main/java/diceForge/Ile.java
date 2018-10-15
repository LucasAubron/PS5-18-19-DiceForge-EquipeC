package diceForge;

/**
 * Une ile représente un lot de 2 cartes (ou 3 pour celle du fond).
 * C'est la où les joueurs viennent lorsqu'il choisisse une carte qui fait partie de l'ile
 * On utilise un tableau à deux dimensions pour représenter plus intuitivement les paquets de carte.
 */
public class Ile {
    private Carte[][] cartes;
    private Joueur joueur = null;

    public Ile(Carte[][] cartes){
        if (cartes.length < 1 || cartes.length > 3)//A changé < 1 en < 2 après la version minimale
            throw new RuntimeException("Le nombre de paquet de carte est invalide. Min 1, max 3, actuel : "+cartes.length);
        for (int i = 0; i != cartes.length; ++i)
            if (cartes[i].length < 2 || cartes[i].length > 4)
                throw new RuntimeException("Le nombre de carte dans un paquet est invalide. Min 2, max 4, actuel : "+cartes[i].length);
        this.cartes = cartes;
    }
    /**
     * Constructeur à utiliser dans le cas principal ou il y a 2 paquets de nbrCarteParPaquet chaqu'un
     */
    public Ile(Carte carte1, Carte carte2, int nbrCarteParPaquet){
        if (nbrCarteParPaquet < 2 || nbrCarteParPaquet > 4)
            throw new RuntimeException("Le nombre de carte dans un paquet est invalide. Min 2, max 4, actuel : "+nbrCarteParPaquet);
        cartes = new Carte[2][nbrCarteParPaquet];
        for (int i = 0; i != nbrCarteParPaquet; ++i) {
            cartes[0][i] = new Carte(carte1.getCout(), carte1.getNbrPointGloire());
            cartes[1][i] = new Carte(carte2.getCout(), carte2.getNbrPointGloire());
        }
    }

    /**
     * Sert dans le cas ou le joueur part sur une autre ile
     */
    public Joueur retirerJoueur(){
        Joueur joueurExpulse = this.joueur;
        this.joueur=null;

        return joueurExpulse;
    }

    /**
     * Méthode permettant à un joueur de prendre une carte
     * Elle gére l'arrivé de joueur, il ne faut donc pas utiliser ajouterJoueur
     * Elle gére aussi la prise de carte par le joueur
     * @return le joueur expulsé s'il existe sinon null
     */
    public Joueur prendreCarte(Joueur joueur, Carte carte){
        Joueur x = null;
        for (Carte[] paquet:cartes){//On cherche dans chaque paquet
            if (paquet[0].equals(carte)){//Si la première carte du paquet (la plus en dessous de la pile) est la carte recherché
                for (int i = paquet.length - 1; i != -1; --i){//On commence par la fin du paquet (évite une variable inutile)
                    if (paquet[i] != null){//Si il y a bien une carte la ou l'on regarde
                        if (!joueur.acheterExploit(paquet[i]))//Le joueur l'achete
                            throw new RuntimeException("Le joueur n'a pas les moyens d'acheter cette carte.");
                        if (this.joueur.getIdentifiant() != joueur.getIdentifiant())//on ajoute le joueur
                            x = ajouterJoueur(joueur);
                        paquet[i] = null;//On l'enlève du paquet
                        return x;
                    }
                }
            }
        }
        throw new RuntimeException("La carte n'est pas dans le paquet.");
    }

    /**
     * Lorsqu'un joueur arrive sur l'ile
     * Cette fonction ne peux pas être utilisé en dehors de la classe,
     * elle ne l'est que dans prendreCarte() pour l'instant
     * On ne la teste pas, on teste prendreCarte() à la place.
     * @return le joueur expulsé ou null s'il n'y en a pas
     */
    private Joueur ajouterJoueur(Joueur joueur){
        Joueur x = null;
        if(this.joueur!=null){//S'il y a déjà un joueur présent, il y a une chasse
            this.joueur.estChasse();
            joueur.chasse();
            x = retirerJoueur();
        }
        this.joueur=joueur;
        return x;
    }

    public Joueur getJoueur() {return joueur;}

    /**
     * Ne PAS utiliser pour retirer une carte !!!!
     * @return
     */
    public Carte[][] getCartes(){return cartes;}
}

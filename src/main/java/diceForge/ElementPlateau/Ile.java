package diceForge.ElementPlateau;

import diceForge.Cartes.Carte;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Une ile représente un lot de 2 cartes (ou 3 pour celle du fond).
 * C'est la où les joueurs viennent lorsqu'il choisisse une carte qui fait partie de l'ile
 * On utilise un tableau à deux dimensions pour représenter plus intuitivement les paquets de carte.
 */
public class Ile {
    private List<List<Carte>> cartes;
    private Joueur joueur = null;
    private Afficheur afficheur;

    public Ile(Carte[][] cartes, Afficheur afficheur){
        this.afficheur = afficheur;
        if (cartes.length < 1 || cartes.length > 3)//A changé < 1 en < 2 après la version minimale
            throw new DiceForgeException("Ile","Le nombre de paquet de carte est invalide. Min 1, max 3, actuel : "+cartes.length);
        for (int i = 0; i != cartes.length; ++i)
            if (cartes[i].length < 2 || cartes[i].length > 4)
                throw new DiceForgeException("Ile","Le nombre de carte dans un paquet est invalide. Min 2, max 4, actuel : "+cartes[i].length);
        this.cartes = new ArrayList<>();
        for (int i = 0; i != cartes.length; ++i) {
            this.cartes.add(new ArrayList<>());
            for (Carte carte:cartes[i])
                this.cartes.get(i).add(carte);
        }
    }
    /**
     * Constructeur à utiliser dans le cas principal ou il y a 2 paquets de nbrCarteParPaquet chaqu'un
     */
    public Ile(Carte carte1, Carte carte2, int nbrCarteParPaquet, Afficheur afficheur){
        this.afficheur = afficheur;
        if (nbrCarteParPaquet < 2 || nbrCarteParPaquet > 4)
            throw new DiceForgeException("Ile","Le nombre de carte dans un paquet est invalide. Min 2, max 4, actuel : "+nbrCarteParPaquet);
        cartes = new ArrayList<>();
        cartes.add(new ArrayList<>());
        for (int i = 0; i != nbrCarteParPaquet; ++i) {
            cartes.get(0).add(carte1.clone());
        }
        cartes.add(new ArrayList<>());
        for (int i = 0; i != nbrCarteParPaquet; ++i) {
            cartes.get(1).add(carte2.clone());
        }
    }

    public Joueur prendreCarte(Joueur acheteur, Carte carte){
        Joueur joueurChasse = null;
        for (List<Carte> paquet:cartes){//On cherche dans chaque paquet
            if (!paquet.isEmpty() && paquet.get(0).equals(carte)){//Si la première carte du paquet (la plus en dessous de la pile) est la carte recherché
                if (this.joueur == null)//Si l'ile est occupée
                    ajouterJoueur(acheteur);
                else
                   joueurChasse = remplacerJoueur(acheteur); //sinon il y a chasse
                acheteur.acheterExploit(paquet.remove(paquet.size()-1));//Le joueur l'achete
                return joueurChasse;
            }
        }
        throw new DiceForgeException("Ile","La carte n'est pas dans le paquet.");
    }

    public Joueur retirerJoueur(){
        Joueur joueurExpulse = this.joueur;
        this.joueur = null;
        return joueurExpulse;
    }

    private void ajouterJoueur(Joueur nouveauOccupant){
        this.joueur = nouveauOccupant;
    }

    private Joueur remplacerJoueur(Joueur chasseur){
        Joueur joueurChasse;
        this.joueur.estChasse();
        chasseur.chasse();
        joueurChasse = retirerJoueur();
        ajouterJoueur(chasseur);
        return joueurChasse;
    }

    public Joueur getJoueur() {return this.joueur;}

    /**
     * Ne PAS utiliser pour retirer une carte !!!!
     * @return
     */
    public List<List<Carte>> getCartes(){return cartes;}
}

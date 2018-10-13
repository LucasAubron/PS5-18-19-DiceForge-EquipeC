package diceForge;

import javax.management.relation.RoleUnresolved;

public class Ile {
    /**
     * Une ile représente un lot de 2 cartes (ou 3 pour celle du fond).
     * C'est la où les joueurs viennent lorsqu'il choisisse une carte qui fait partie de l'ile
     * On utilise un tableau à deux dimensions pour représenter plus intuitivement les paquets de carte.
     */
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
    public Ile(Carte carte1, Carte carte2, int nbrCarteParPaquet){
        /**
         * Constructeur à utiliser dans le cas principal ou il y a 2 paquets de nbrCarteParPaquet chaqu'un
         */
        if (nbrCarteParPaquet < 2 || nbrCarteParPaquet > 4)
            throw new RuntimeException("Le nombre de carte dans un paquet est invalide. Min 2, max 4, actuel : "+nbrCarteParPaquet);
        cartes = new Carte[2][nbrCarteParPaquet];
        for (int i = 0; i != nbrCarteParPaquet; ++i)
            cartes[0][i] = new Carte(carte1.getCout(), carte1.getNbrPointGloire());
        for (int i = 0; i != nbrCarteParPaquet; ++i)
            cartes[1][i] = new Carte(carte2.getCout(), carte2.getNbrPointGloire());
    }
}

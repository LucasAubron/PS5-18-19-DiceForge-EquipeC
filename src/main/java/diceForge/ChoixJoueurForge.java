package diceForge;

/**
 * Parce que le craft d'une face de dé demande une multitude d'informations aux joueurs, et de types
 * différents (1 bassin et 3 entiers) on créé une classe pour pouvoir renvoyer un seul objet lorsque le joueur choisi.
 * bassin est le bassin dans lequel la face a forger a été prise, numDe est le numéro du dé sur lequel le joueur
 * va rempalcer une face, numFace est le numéro de la face choisie dans le bassin concerné.
 * dans le bassin (utile car deux bassins accueuillent des faces différentes: ceux qui coutent 4 et 12),
 * posFace est la position de la face sur le dé que le joueur décide de remplacer.
 *
 * Proposition si cela deplait: créer 5 méthodes dans joueur qui renvoient une a une les informations
 * nécessaires au craft d'une face: travail long et complexe puisqu'il nécessite de changer et coordinateur
 * et les méthodes de choix des bots. Dans l'absolu ce serait mieux mais ce n'est pas une priorité.
 */
public class ChoixJoueurForge {
    private Bassin bassin;
    private int numDe;
    private int numFace;
    private int posFace;
    public ChoixJoueurForge(Bassin bassin, int numFace, int numDe, int posFace){
        this.bassin = bassin;
        this.numDe = numDe;
        this.numFace = numFace;
        this.posFace = posFace;
    }

    int getNumDe() { return numDe; }

    Bassin getBassin() { return bassin; }

    int getNumFace() {
        return numFace;
    }

    int getPosFace() {
        return posFace;
    }
}

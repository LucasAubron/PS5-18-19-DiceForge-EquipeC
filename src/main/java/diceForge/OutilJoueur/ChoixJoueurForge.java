package diceForge.OutilJoueur;

import diceForge.ElementPlateau.Bassin;

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
    private int numFaceDansBassin;
    private int posFaceSurDe;
    public ChoixJoueurForge(Bassin bassin, int numFaceDansBassin, int numDe, int posFaceSurDe){
        this.bassin = bassin;
        this.numDe = numDe;
        this.numFaceDansBassin = numFaceDansBassin;
        this.posFaceSurDe = posFaceSurDe;
    }

    public int getNumDe() { return numDe; }

    public Bassin getBassin() { return bassin; }

    public int getNumFaceDansBassin() {
        return numFaceDansBassin;
    }

    public int getPosFaceSurDe() {
        return posFaceSurDe;
    }
}

package diceForge.Cartes;


import diceForge.Faces.FaceX3;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.DiceForgeException;
import static diceForge.OutilJoueur.Joueur.Jeton.CERBERE;

/**
 * La classe carte peut être utilisé pour les cartes ne donnant que des points de gloire
 * Sinon il faut créer / utiliser une classe dérivée de celle ci
 * Nom des cartes:
 * Coffre ; Herbes folles ; Ancien ; Marteau ; Ours ; Sanglier ; Biche ; Satyres
 * Hibou ; Minautore ; Bateau celeste ; Bouclier ; Cerbere ; Passeur ; Casque d'invisibilite
 * Meduse ; Triton ; Abysse ; Sentinelle ; Cancer ; Hydre ; Typhon ; Sphinx ; Cyclope; Miroir Abyssal
 */
public class Carte {
    private Ressource[] cout; //un tableau de ressource car l'hydre et le typhon
    private int nbrPointGloire;// coutent des soleils ET des lunes
    private Noms nom;

    public enum Noms {Coffre, HerbesFolles, Ancien, Marteau, Ours, Sanglier, Biche, Satyres,
    Hibou, Minautore, VoileCeleste, Bouclier, Cerbere, Passeur, CasqueDinvisibilite,
    Meduse, Triton, Sentinelle, Cancer, Hydre, Typhon, Sphinx, Cyclope, MiroirAbyssal}

    public Carte(Ressource[] cout, int nbrPointGloire, Noms nom){
        if (cout.length <= 0)
            throw new DiceForgeException("Carte","Une carte doit couter quelque chose. Cout donné : "+cout);
        this.cout = cout;
        this.nbrPointGloire = nbrPointGloire;
        this.nom = nom;
    }

    /**
     * Pour cloner
     * Il faut l'override dans chaque classe fille
     * @return un clone de la carte en question, utile pour éviter les répétitions lors
     * de l'initialisation des iles
     */
    public Carte clone(){
        return new Carte(cout, nbrPointGloire, nom);
    }

    public void effetDirect(Joueur acheteur){
        switch (nom) {
            case Coffre:
                acheteur.augmenterMaxOr(4);
                acheteur.augmenterMaxSoleil(3);
                acheteur.augmenterMaxLune(3);
                break;
            case HerbesFolles:
                acheteur.ajouterLune(3);
                acheteur.ajouterOr(3);
                break;
            case Ancien:
                acheteur.ajouterRenfort(Joueur.Renfort.ANCIEN);
                break;
            case Biche:
                acheteur.ajouterRenfort(Joueur.Renfort.BICHE);
                break;
            case Hibou:
                acheteur.ajouterRenfort(Joueur.Renfort.HIBOU);
                break;
            case Triton:
                acheteur.ajouterJeton(Joueur.Jeton.TRITON);
                break;
            case Cerbere:
                acheteur.ajouterJeton(Joueur.Jeton.CERBERE);
                break;
            case CasqueDinvisibilite:
                acheteur.forgerFaceSpeciale(new FaceX3());
                break;
            case Cancer:
                acheteur.setDernierLanceDes(2);
                acheteur.lancerLesDes();
                acheteur.gagnerRessourceDesDeuxDes();
                acheteur.lancerLesDes();
                acheteur.gagnerRessourceDesDeuxDes();
                break;
            case Sphinx:
                int choix = acheteur.choisirDeFaveurMineure();
                acheteur.setDernierLanceDes(choix);
                for (int i = 0; i != 4; ++i) {
                    acheteur.gagnerRessourceFace(acheteur.getDe(choix).lancerLeDe(), false);
                    for (int j = 0; j < acheteur.getJetons().size() && acheteur.getJetons().get(j) == CERBERE && acheteur.utiliserJetonCerbere(); ++j)
                        acheteur.appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
                }
                break;
            case Sentinelle:
                acheteur.setDernierLanceDes(2);
                acheteur.setJetRessourceOuPdg(true);
                for(int i = 0; i != 2; ++i){
                    acheteur.lancerLesDes();
                    acheteur.gagnerRessourceDesDeuxDes();
                }
                acheteur.setJetRessourceOuPdg(false);
                break;
            case Cyclope:
                int choixDe = acheteur.choisirDeCyclope();
                acheteur.setDernierLanceDes(choixDe);
                acheteur.setJetOrOuPdg(true);
                for (int i = 0; i != 4; ++i){
                    acheteur.gagnerRessourceFace(acheteur.getDes()[choixDe].lancerLeDe(), false);
                    for (int j = 0; j < acheteur.getJetons().size() && acheteur.getJetons().get(j) == CERBERE && acheteur.utiliserJetonCerbere(); ++j)
                        acheteur.appliquerJetonCerbere();//On applique tout les jetons qui sont des cerberes et qu'il veut utiliser
                }
                acheteur.setJetOrOuPdg(false);
                break;
        }
    }

    public Ressource[] getCout() {
        return cout;
    }

    public int getNbrPointGloire() {
        return nbrPointGloire;
    }

    public Noms getNom() { return nom; }

    boolean equals(Carte carte){
        if (carte == null)
            return false;
        return (nom == carte.getNom());
    }

    @Override
    public String toString(){
        return nom.toString();
    }

}

package bot;

import diceForge.*;

import java.util.List;
import java.util.Random;

import static diceForge.Joueur.Jeton.CERBERE;

public class EasyBot extends Joueur {
    public EasyBot(int identifiant, Afficheur afficheur) {super(identifiant, afficheur);}

    @Override
    public Action choisirAction(int numManche){
        if (numManche < 6 && getOr() > 5)//Si on est au début du jeu et que l'on a assez d'or, on forge
            return Action.FORGER;
        else if (getSoleil() > 0 || getLune() > 0)//Sinon, si on peu, on prend des cartes
            return Action.EXPLOIT;
        else return Action.PASSER;//Sinon on passe
    }

    @Override
    public ChoixJoueurForge choisirFaceAForgerEtARemplacer(List<Bassin> bassins, int numManche){
        if (bassins.isEmpty())
            return new ChoixJoueurForge(null, 0, 0, 0);
        Bassin bassinAChoisir = null;
        for (Bassin bassin:bassins){
            if (numManche < 3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            else if (bassinAChoisir != null && bassinAChoisir.getCout() < bassin.getCout())//Sinon, on cherche la face la plus chere
                bassinAChoisir = bassin;
            else if (bassinAChoisir == null)
                bassinAChoisir = bassin;
        }
        int[] posFace = getPosFace1Or();
        if (posFace[0] != -1)
            return new ChoixJoueurForge(bassinAChoisir, 0, posFace[0], posFace[1]);

        return new ChoixJoueurForge(null, 0, 0, 0);
    }

    @Override
    public Carte choisirCarte(List<Carte> cartes, int numManche){
        Carte carteAChoisir = null;
        for (Carte carte:cartes){
            if (carte.getNom().equals("Marteau") && !possedeCarte("Marteau"))//Au moins 1 marteau
                return carte;
            if (carte.getNom().equals("Coffre") && !possedeCarte("Coffre"))//Et un coffre
                return carte;
            if (carte.getNom().equals("Miroir Abyssal") && !possedeCarte("Miroir Abyssal"))
                return  carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        return carteAChoisir;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        return ((getOr() > 10 && numManche < 6) || getSoleil() > 3 || getLune() > 1);//Si on a assez de ressource pour refaire un tour
    }

    @Override
    public int choisirRepartitionOrMarteau(int nbrOr){return 0;}//On met tout dans le marteau

    @Override
    public List<Renfort> choisirRenforts(List renfortsUtilisables){
        return renfortsUtilisables;//On appelle tous les renforts
    }

    @Override
    public int choisirRessource(Face faceAChoix){
        for (int i = 0; i != faceAChoix.getRessource().length; ++i){
            for (Ressource ressource:faceAChoix.getRessource()[i]){
                if (ressource instanceof Lune || ressource instanceof Soleil) {
                    return i;//On cherche un résultat sur la face qui donne des soleils ou des lunes
                }
            }
        }
        return 0;
    }

    @Override
    public int choisirRessourceAPerdre(Face faceAChoix){
        return 0;
    }

    @Override
    public int choisirDeFaveurMineure(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirDeCyclope(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs) {
        return (getIdentifiant() == 1 ? 0 : 1);
    }

    /**
     * Permet de craft les faces spéciales, contrairement aux faces achetables avec de l'or, on a pas besoin
     * de chercher une face a crafter puisqu'on la connait déjà, on doit juste chercher quelle face remplacer
     * @param face
     */

    @Override
    public void forgerFace(Face face){
        boolean aForge = false;
        int[] posFace = getPosFace1Or();
        if(posFace[0] != -1) //si on a trouvé une face 1 or sur un des dés)
                    forgerDe(posFace[0], face, posFace[1]);
                    aForge = true;
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            forgerDe(0, face, 0);
    }

    /**
     * Lorsqu'on doit choisir une face pour gagner les ressources indiquées dessus
     * le easyBot cherche en particulier les faces donnant de l'or, sinon des lunes, sinon des points de victoires
     * @param faces les faces disponibles
     * @return position de la face dans la liste fournie
     */
    @Override
    public int choisirFacePourGagnerRessource(List<Face> faces){
        int posMaxSoleil = -1, posMaxLune = -1, posMaxOr = -1;
        int maxSoleil = 0, maxLune = 0, maxOr = 0;
        for (int i = 0; i != faces.size(); ++i){
            for (Ressource[] ressources:faces.get(i).getRessource()){
                for(Ressource ressource:ressources){
                    if (ressource instanceof Soleil && ressource.getQuantite() > maxSoleil){
                        posMaxSoleil = i;
                        maxSoleil = ressource.getQuantite();
                    }
                    else if (ressource instanceof Lune && ressource.getQuantite() > maxLune){
                        posMaxLune = i;
                        maxLune = ressource.getQuantite();
                    }
                    else if (ressource instanceof Or && ressource.getQuantite() > maxOr){
                        posMaxOr = i;
                        maxOr = ressource.getQuantite();
                    }
                }
            }
        }
        if (posMaxSoleil != -1) return posMaxSoleil;
        if (posMaxLune != -1) return posMaxLune;
        if (posMaxOr != -1) return posMaxOr;
        return 0;
    }


    @Override
    public int[] choisirFaceARemplacerPourMiroir(){
        int[] res = getPosFace1Or();
        Random random = new Random();
        if (res[0] != -1)
            return res;
        return new int[]{random.nextInt(2), random.nextInt(6)};
    }

    @Override
    public choixJetonTriton utiliserJetonTriton(){
        Random random = new Random();
        int choix = random.nextInt(choixJetonTriton.values().length);
        switch (choix){
            case 0:
                return choixJetonTriton.Rien;
            case 1:
                return choixJetonTriton.Or;
            case 2:
                return choixJetonTriton.Soleil;
            case 3:
                return choixJetonTriton.Lune;
        }
        throw new DiceForgeException("Bot","Impossible, utiliserJetonTriton ne renvoi rien !!");
    }

    @Override
    public boolean utiliserJetonCerbere(){
        Random random = new Random();
        return random.nextInt(2) == 1;
    }

    @Override
    public boolean choisirRessourceOuPdg(Ressource ressource){
        return true;
    }

    @Override
    public String toString(){return "EasyBot";}
}
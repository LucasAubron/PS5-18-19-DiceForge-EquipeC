package bot;

import diceForge.*;
import diceForge.ElementPlateau.Plateau;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Afficheur;

import java.util.List;
import java.util.Random;

public class LataBotch extends Joueur {
    public LataBotch(int identifiant, Afficheur afficheur, Plateau plateau){ super(identifiant, afficheur, plateau); }
    boolean rushMarteau=false;

    @Override
    public Joueur.Action choisirAction(int numManche)
    {
        if(rushMarteau==true)
            return Action.EXPLOIT;
        if (numManche <=3 && getOr() > 5 && rushMarteau==false)//Si on est au début du jeu et que l'on a assez d'or, on forge
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
        if(trouveBassinCout(bassins,4,"Or")!=null){
            Bassin bassin=trouveBassinCout(bassins,4,"Or");
            for(int i=0;i<bassin.getFaces().size();i++){
                if(bassin.getFaces().get(i).getRessource()[0][0].getQuantite()==6){
                    int[] posFace = getPosFace1Or();
                    if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                        return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
                }

            }
        }
        for(int i=bassins.size()-1;i>=0;i--){
            Bassin bassin=bassins.get(i);


            if (numManche < 3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Or){//Les 2 premières manches
                int[] posFace = getPosFace1Or();
                if (posFace[0] != -1)   //si on a bien trouvé une face 1Or sur les dés du joueur
                    return new ChoixJoueurForge(bassin, 0, posFace[0], posFace[1]);
            }
            if (numManche <=3 && bassin.getFaces().get(0).getRessource()[0][0] instanceof Soleil){//Les 2 premières manches
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
            if (carte.getNom().equals(Carte.Noms.Marteau) )//Au moins 1 marteau
                return carte;
            if (carte.getNom().equals(Carte.Noms.Coffre) && !possedeCarte(Carte.Noms.Coffre))//Et un coffre
                return carte;
            if (carteAChoisir != null && carteAChoisir.getCout()[0].getQuantite() < carte.getCout()[0].getQuantite())
                carteAChoisir = carte;//Sinon on cherche la carte la plus chere
            else if (carteAChoisir == null)
                carteAChoisir = carte;
        }
        return carteAChoisir;
    }

    @Override
    public boolean choisirActionSupplementaire(int numManche){
        for(int i=0;i<getPlateau().getIles().length;i++){
            for(int j=0; j!=getPlateau().getIles()[i].getCartes().size();j++)
                for(int k=0;k!=getPlateau().getIles()[i].getCartes().get(j).size();k++){
                    if(getPlateau().getIles()[i].getCartes().get(j).get(k).getNom().equals(Carte.Noms.Marteau)&&(getSoleil() > 1 && getLune() > 0))
                        rushMarteau=true;
                }

        }
        return ((getOr() > 10 && numManche <=3) || (getSoleil() > 1 && getLune() > 0) || getSoleil()>2 || getLune()>1);//Si on a assez de ressource pour refaire un tour
    }

    //On choisit l'or à garder ici 0
    @Override
    public int choisirRepartitionOrMarteau(int quantiteOr){
        return 0;
    }

    @Override
    public List<Joueur.Renfort> choisirRenforts(List renfortsUtilisables){
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
    public int choisirIdJoueurPorteurSanglier(List<Joueur> joueurs){
        return (getIdentifiant() == 1 ? 2 : 1);
    }

    @Override
    public void forgerFace(Face face){
        boolean aForge = false;
        int[] posFace = getPosFace1Or();
        if(posFace[0] != -1) { //si on a trouvé une face 1 or sur un des dés)
            forgerDe(posFace[0], face, posFace[1]);
            aForge = true;
        }
        if (!aForge)//S'il n'a pas trouvé d'endroit ou forger le dé, on le forge sur la première face, sur le premier de
            forgerDe(0, face, 0);
    }
    //Liste de face et que tu veux une face
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
    public Joueur.choixJetonTriton utiliserJetonTriton(){
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
    public boolean choisirRessourceOuPdg(Ressource ressource) {
        return true;
    }
    private Bassin trouveBassinCout(List<Bassin> bassins, int cout, String typeRessource){
        if (typeRessource.equals("Or")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Or)
                                    return bassin;
        }
        if (typeRessource.equals("Soleil")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Soleil)
                                    return bassin;
        }
        if (typeRessource.equals("Lune")||typeRessource.equals("Tout")) {
            for (Bassin bassin : bassins)
                if (bassin.getCout() == cout)
                    for (Face face : bassin.getFaces())
                        for (Ressource[] ressources : face.getRessource())
                            for (Ressource ressource : ressources)
                                if (ressource instanceof Lune)
                                    return bassin;
        }
        return null;
    }

    /**
     * utile pour les bots autre que random
     * permet de chercher une face de base 1 or et de renvoyer sa position
     * @return un tableau = [numéro du dé, numéro de la face sur le dé en question]
     */
    private int[] getPosFace1Or(){
        for (int i = 0; i != getDes().length; ++i){//On parcours tous les dés
            for (int j = 0; j != getDes()[i].getFaces().length; ++j){//Toutes les faces
                if (getDes()[i].getFaces()[j].getRessource().length != 0 && getDes()[i].getFaces()[j].getRessource()[0][0] instanceof Or && getDes()[i].getFaces()[j].getRessource()[0][0].getQuantite() == 1){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{-1, -1}; //Si on ne trouve pas de face 1 or
    }

    @Override
    public String toString(){return "LaaaaaataBotch";}
}

package diceForge;
public class Main {
    public static void main(String[] args) {
        try {//Le coordinateur, qui comprend toute la partie temporelle du jeu
            boolean modeVerbeux = true;
            Coordinateur coordinateur = new Coordinateur(modeVerbeux, new Joueur[]{new EasyBot(), new RandomBot()});
            System.out.println(coordinateur.getAffichage());
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

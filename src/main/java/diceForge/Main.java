package diceForge;
public class Main {
    public static void main(String[] args) {
        try {//Le coordinateur, qui comprend toute la partie temporelle du jeu
            Coordinateur coordinateur = new Coordinateur(true, new Joueur[]{new EasyBot(0), new RandomBot(0)});
            System.out.println(coordinateur);
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

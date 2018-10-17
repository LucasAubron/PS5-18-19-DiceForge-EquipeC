package diceForge;
public class Main {
    public static void main(String[] args) {
        try {
            Plateau plateau = new Plateau();//Le plateau, qui comprend toute la partie physique du jeu
            Coordinateur coordinateur = new Coordinateur(plateau, 4);//Le coordinateur, qui comprend toute la partie temporelle du jeu
            System.out.println(coordinateur);
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

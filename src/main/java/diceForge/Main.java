package diceForge;
public class Main {
    public static void main(String[] args) {
        try {//Le coordinateur, qui comprend toute la partie temporelle du jeu
            boolean modeVerbeux = false; //true si on veut une seule partie détaillée, false si on veut 1000 parties avec stats
            Coordinateur coordinateur = new Coordinateur(modeVerbeux, new String[]{"EasyBot", "RandomBot"});
            System.out.println(coordinateur.getAffichage());
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

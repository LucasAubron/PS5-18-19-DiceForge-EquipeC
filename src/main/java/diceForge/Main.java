package diceForge;
public class Main {
    public static void main(String[] args) {
        try {
            Plateau plateau = new Plateau();
            Coordinateur coordinateur = new Coordinateur(plateau, 4);
            /*int i = 3;
            while (i-- > 0)
                plateau.playPlayer0();*/
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

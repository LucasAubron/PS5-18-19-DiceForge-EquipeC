package diceForge;

//TODO remplacer tous les for par des foreach
public class Main {
    public static void main(String[] args) {
        try {
            Plateau plateau = new Plateau();
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}

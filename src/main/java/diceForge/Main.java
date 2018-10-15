package diceForge;
//TODO CHANGER TOUTES LES LISTES EN ARRAYLIST SAUF DANS LES CONSTRUCTEURS
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
            System.out.println("Une erreur est apparue dans le déroulement du programme, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}

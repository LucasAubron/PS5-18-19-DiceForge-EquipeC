package diceForge;
public class Main {
    public static void main(String[] args) {
        try {
            boolean modeVerbeux = true; //true si on veut une seule partie détaillée, false si on veut 1000 parties avec stats
            Coordinateur coordinateur = new Coordinateur(modeVerbeux, new Joueur.Bot[]{Joueur.Bot.EasyBot, Joueur.Bot.AubronBot});//Le coordinateur, qui comprend toute la partie temporelle du jeu, en échange constant avec les joueurs, il leur envoie les choix disponiblesà chaque foisqu'une prise de décision est nécessaire
            System.out.println(coordinateur.getAffichage());
        }
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
        }
    }
}
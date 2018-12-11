package diceForge.Main;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Coordinateur;
import diceForge.Structure.DiceForgeException;

public class Main {
    public static void main(String[] args) {
        try {
            //insérer dans le tableau (en deuxième argument lors de l'initialisation) entre 2 et 4 Enum de bot
            //Bot dispo dans l'ordre de force: randomBot, EasyBot, NidoBot, NidoBotV2, AubronBotV2
            boolean modeVerbeux = false; //true si on veut une seule partie détaillée, false si on veut 1000 parties avec stats
            Coordinateur coordinateur = new Coordinateur(modeVerbeux, new Joueur.Bot[]{Joueur.Bot.NidoBotV2, Joueur.Bot.EasyBot});//Le coordinateur,
            System.out.println(coordinateur.getAffichage());        // qui comprend toute la partie temporelle du jeu, en échange constant avec les joueurs,

        }                                                           // il leur envoie les choix disponibles à chaque fois qu'une prise de décision est nécessaire
        catch (DiceForgeException e){
            System.out.println("Une erreur est apparue dans "+e.getLocalisation()+".java, message d'erreur : ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
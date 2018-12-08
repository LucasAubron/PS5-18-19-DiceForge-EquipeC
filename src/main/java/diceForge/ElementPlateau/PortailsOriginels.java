package diceForge.ElementPlateau;

import bot.AubotV2.src.AubotV2;
import bot.NidoBot.NidoBot;
import bot.NidoBot.NidoBotV2;
import bot.ResteDesBot.EasyBot;
import bot.ResteDesBot.RandomBot;
import diceForge.OutilJoueur.Joueur;
import diceForge.Structure.Afficheur;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;


/**
 * Les portails originels sont la ou les joueurs commence et la
 * ou ils viennent lorsqu'il sont chassés
 */
public class PortailsOriginels {
    //Attribut ---------------------
    private List<Joueur> joueurs;

    //Constructeur ----------------------------------------------------------------------------------
    //Long car il faut identifier chaque type de bot, et aubotV2 utilise 10 bot pour ses tournois (qui ont tous un fichier source différents)
    public PortailsOriginels(Joueur.Bot[] typeJoueurs, Afficheur afficheur, Plateau plateau) {
        if (typeJoueurs.length < 2 || typeJoueurs.length > 4)
            throw new DiceForgeException("PortailsOriginels", "Le nombre de joueur est invalide. Min : 2, max : 4, actuel : " + typeJoueurs.length);
        this.joueurs = new ArrayList<>();
        for (int identifiant = 1; identifiant <= typeJoueurs.length; identifiant++) {//On copie les joueurs, pour éviter de garder
            if (typeJoueurs[identifiant - 1] == Joueur.Bot.RandomBot)               // les mêmes joueurs sur des plateaux différents
                this.joueurs.add(new RandomBot(identifiant, afficheur, plateau));   // dans le cas où on itère plusieurs parties

            else if (typeJoueurs[identifiant-1] == Joueur.Bot.EasyBot)
                this.joueurs.add(new EasyBot(identifiant, afficheur, plateau));

            else if (typeJoueurs[identifiant-1] == Joueur.Bot.NidoBot)
                this.joueurs.add(new NidoBot(identifiant, afficheur, plateau));

            else if (typeJoueurs[identifiant-1] == Joueur.Bot.NidoBotV2)
                this.joueurs.add(new NidoBotV2(identifiant, afficheur, plateau));

//            else if (typeJoueurs[identifiant-1] == Joueur.Bot.PlanteBot)
//                this.joueurs.add(new MLGBot(identifiant, afficheur, plateau));

//            else if (typeJoueurs[identifiant-1] == Joueur.Bot.RomanetBot)
//                this.joueurs.add(new LataBotch(identifiant, afficheur, plateau));

        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A1)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot1/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A2)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot2/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A3)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot3/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A4)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot4/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A5)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot5/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A6)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot6/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A7)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot7/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A8)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot8/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A9)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot9/"));
        else if(typeJoueurs[identifiant-1] == Joueur.Bot.A10)
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/Bot10/"));
        else if (typeJoueurs[identifiant-1] == Joueur.Bot.AubronBotV2)
                this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/BestBot"));
        else
            throw new DiceForgeException("PortailsOriginels", "Le type du bot n'est pas supporté");

        }
    }

    /**
     * Cette méthode permet de retirer un joueur d'un des portails originels
     * Elle est a utiliser à chaque fois qu'un joueur va sur une ile prendre une carte
     */
    public Joueur retirerJoueur(int identifiantJoueur){
        Joueur x = null;
        for (Joueur joueur:joueurs){//On cherche le joueur à retirer
            if (joueur.getIdentifiant() == identifiantJoueur) {
                x = joueur;
                break;
            }
        }
        if (x == null)
            throw new DiceForgeException("PortailsOriginels","Le joueur qui posséde l'indice n°"+identifiantJoueur+" n'existe pas.");
        joueurs.remove(x);//On retire le joueur
        return x;
    }

    /**
     * Méthode à utiliser lorsqu'un joueur se fait chasser et revient aux portails originels
     * Si cette méthode renvoie l'erreur, il y a un gros problème quelque part
     */
    public void ajouterJoueur(Joueur joueur){
        for(Joueur x:joueurs)
            if (x.getIdentifiant() == joueur.getIdentifiant())
                throw new DiceForgeException("PortailsOriginels", "Le joueur est déjà dans un portail originel. Identifiant du joueur : "+joueur.getIdentifiant());
        joueurs.add(joueur);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
}

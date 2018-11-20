package diceForge;

import bot.*;
import bot.AubotV2.AubotV2;
import bot.NidoBot.NidoBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Les portails originels sont la ou les joueurs commence et la
 * ou ils viennent lorsqu'il sont chassés
 */
public class PortailsOriginels {
    private List<Joueur> joueurs;

    public PortailsOriginels(Joueur.Bot[] typeJoueurs, Afficheur afficheur, Plateau plateau){
        if (typeJoueurs.length < 2 || typeJoueurs.length > 4)
            throw new DiceForgeException("PortailsOriginels","Le nombre de joueur est invalide. Min : 2, max : 4, actuel : "+typeJoueurs.length);
        this.joueurs = new ArrayList<>();
        for (int identifiant = 1; identifiant<=typeJoueurs.length; identifiant++) {//On copie les joueurs, pour éviter de garder les mêmes joueurs sur des plateaux différents (dans le cas où on itère plusieurs parties)
            if (typeJoueurs[identifiant-1] == Joueur.Bot.RandomBot)
                this.joueurs.add(new RandomBot(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.EasyBot)
                this.joueurs.add(new EasyBot(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.PlanteBot)
                this.joueurs.add(new MLGBot(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.AubronBot)
                this.joueurs.add(new AubotLeGrand(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.RomanetBot)
                this.joueurs.add(new LataBotch(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.NidoBot)
                this.joueurs.add(new NidoBot(identifiant, afficheur, plateau));
            else if (typeJoueurs[identifiant-1] == Joueur.Bot.AubronBotV2)
                if (typeJoueurs.length == 2)
                    this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/1V1/BestBot"));
                else if (typeJoueurs.length == 3)
                    this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/1V1V1/BestBot"));
                else if (typeJoueurs.length == 3)
                    this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, "src/main/java/bot/AubotV2/1V1V1V1/BestBot"));
            else throw new DiceForgeException("PortailsOriginels", "Le type du bot n'est pas supporté");
        }
    }

    public PortailsOriginels() {
    }

    /**
     * Cette méthode permet de retirer un joueur d'un des portails originels
     * Elle est a utiliser à chaque fois qu'un joueur va sur une ile prendre une carte
     */
    Joueur retirerJoueur(int identifiantJoueur){
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
    void ajouterJoueur(Joueur joueur){
        for(Joueur x:joueurs)
            if (x.getIdentifiant() == joueur.getIdentifiant())
                throw new DiceForgeException("PortailsOriginels", "Le joueur est déjà dans un portail originel. Identifiant du joueur : "+joueur.getIdentifiant());
        joueurs.add(joueur);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
}

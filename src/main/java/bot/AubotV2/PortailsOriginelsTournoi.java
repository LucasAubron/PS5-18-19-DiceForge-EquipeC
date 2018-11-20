package bot.AubotV2;

import bot.*;
import bot.NidoBot.NidoBot;
import diceForge.*;

import java.util.ArrayList;
import java.util.List;

public class PortailsOriginelsTournoi extends PortailsOriginels {
    private List<Joueur> joueurs;
    private Afficheur afficheur = new Afficheur(false);

    PortailsOriginelsTournoi(String[] filePath, Plateau plateau){
        if (filePath.length < 2 || filePath.length > 4)
            throw new DiceForgeException("PortailsOriginels","Le nombre de joueur est invalide. Min : 2, max : 4, actuel : "+filePath.length);
        this.joueurs = new ArrayList<>();
        for (int identifiant = 1; identifiant<=filePath.length; identifiant++) {
            this.joueurs.add(new AubotV2(identifiant, afficheur, plateau, filePath[identifiant-1]));
        }
    }

    @Override
    public List getJoueurs(){
        return joueurs;
    }

    @Override
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

    @Override
    public void ajouterJoueur(Joueur joueur){
        for(Joueur x:joueurs)
            if (x.getIdentifiant() == joueur.getIdentifiant())
                throw new DiceForgeException("PortailsOriginels", "Le joueur est déjà dans un portail originel. Identifiant du joueur : "+joueur.getIdentifiant());
        joueurs.add(joueur);
    }
}
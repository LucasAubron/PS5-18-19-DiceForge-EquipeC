package bot.AubotV2;

import diceForge.*;

import java.util.Random;

public class PlateauTournoi extends Plateau{
    private PortailsOriginels portail;
    private Temple temple;
    private Ile[] iles;//La ou il y a les cartes

    PlateauTournoi(String[] filePath) {
        portail = new PortailsOriginelsTournoi(filePath, this);//La ou les joueurs sont de base
        temple = new Temple(filePath.length);//La classe temple s'occupe de toute la partie forge de dé
        Random random = new Random();

        Carte ours = new Carte(new Ressource[]{new Lune(2)}, 2, Carte.Noms.Ours);
        Carte biche = new Carte(new Ressource[]{new Lune(2)}, 2, Carte.Noms.Biche);
        Carte sanglier = new CarteSanglier(portail.getJoueurs());
        Carte satyres = new Satyres(portail.getJoueurs());
        Carte cerbere = new Carte(new Ressource[]{new Lune(4)}, 6, Carte.Noms.Cerbere);
        Carte passeur = new Carte(new Ressource[]{new Lune(4)}, 12, Carte.Noms.Passeur);

        Carte hibou = new Carte(new Ressource[]{new Soleil(2)}, 4, Carte.Noms.Hibou);
        Carte bateauCeleste = new CarteBateauCeleste(this);
        Carte minautore = new Minautore(portail.getJoueurs());
        Carte bouclier = new CarteBouclier(this);

        Carte meduse = new Carte(new Ressource[]{new Soleil(4)}, 14, Carte.Noms.Meduse);
        Carte triton = new Carte(new Ressource[]{new Soleil(4)}, 8, Carte.Noms.Triton);

        Carte[][] ileFond = new Carte[3][filePath.length];
        int[] ra = new int[]{random.nextInt(2), random.nextInt(2), random.nextInt(2)};
        for (int i = 0; i != filePath.length; ++i){
            if (ra[0] == 0)
                ileFond[0][i] = new Carte(new Ressource[]{new Soleil(5), new Lune(5)}, 16, Carte.Noms.Typhon);
            else
                ileFond[0][i] = new Carte(new Ressource[]{new Soleil(5), new Lune(5)}, 26, Carte.Noms.Hydre);
            if (ra[1] == 0)
                ileFond[1][i] = new Carte(new Ressource[]{new Lune(6)}, 6, Carte.Noms.Sentinelle);
            else
                ileFond[1][i] = new Carte(new Ressource[]{new Lune(6)}, 8, Carte.Noms.Cancer);
            if (ra[2] == 0)
                ileFond[2][i] = new Carte(new Ressource[]{new Soleil(6)}, 10, Carte.Noms.Sphinx);
            else
                ileFond[2][i] = new Carte(new Ressource[]{new Soleil(6)}, 8, Carte.Noms.Cyclope);
        }

        iles = new Ile[]{new Ile(new Marteau(),
                new Carte(new Ressource[]{new Lune(1)}, 2, Carte.Noms.Coffre), filePath.length),
                new Ile(new Carte(new Ressource[]{new Soleil(1)}, 0, Carte.Noms.Ancien),
                        new Carte(new Ressource[]{new Soleil(1)}, 2, Carte.Noms.HerbesFolles), filePath.length),
                new Ile(random.nextInt(2) == 1 ? ours : biche,
                        random.nextInt(2) == 1 ? sanglier : satyres, filePath.length),
                new Ile(random.nextInt(2) == 1 ? hibou : bateauCeleste,
                        random.nextInt(2) == 1 ? minautore : bouclier, filePath.length),
                new Ile(random.nextInt(2) == 1 ? cerbere : passeur,
                        new Carte(new Ressource[]{new Lune(5)}, 4, Carte.Noms.CasqueDinvisibilite),
                        filePath.length),
                new Ile(random.nextInt(2) == 1 ? meduse : triton,
                        new CarteMiroirAbyssal(portail.getJoueurs()),
                        filePath.length),
                new Ile(ileFond)};
    }
}

package bot.NidoBot;

import diceForge.*;

import java.util.List;

class NidoFunctions {
    public static Stats getNbFaces(int numDe, De[] jeuDes, Ressource uneRess){
        Stats count = new Stats();
        for (Face face : jeuDes[numDe].getFaces())
            if (face.getRessource().length == 1 && face.getRessource()[0][0] instanceof Soleil &&
                    uneRess.getClass().getName().equals("diceForge.Soleil"))
                count.incrementNbSoleils();
            else if(face.getRessource().length == 1 && face.getRessource()[0][0] instanceof Lune &&
                    uneRess.getClass().getName().equals("diceForge.Lune"))
                count.incrementNbLunes();
        return count;
    }

    public static int getPosFaceQteMin(int numDe, De[] jeuDes, Ressource uneRess){ //recherche de min classique
        int min = 10000;
        int res = -1;
        Face[] faces = jeuDes[numDe].getFaces();
        for (int i = 0; i < faces.length; i++)
            if (faces[i].getRessource().length == 1)
                if (faces[i].getRessource()[0][0] instanceof Or
                        && uneRess.getClass().getName().equals("diceForge.Or")
                        && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource()[0][0] instanceof Lune
                        && uneRess.getClass().getName().equals("diceForge.Lune")
                        && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource()[0][0] instanceof Soleil
                        && uneRess.getClass().getName().equals("diceForge.Soleil")
                        && faces[i].getRessource()[0][0].getQuantite() < min) {
                    min = faces[i].getRessource()[0][0].getQuantite();
                    res = i;
                }
        return res;
    }

    public static int getNbCarteType(List<Carte> cartes, Carte.Noms nom){
        return (int) cartes.stream()
                .filter(carte -> carte.getNom() == nom)
                .count();
    }
    public static boolean haveSoleilsOuLunesBassins(Bassin[] sanctuaire){
        boolean have = false;
        int i = 0;
        while (!have && i < sanctuaire.length){
            if (!sanctuaire[i].getFaces().isEmpty() && (
                    sanctuaire[i].getFaces().get(0).getRessource()[0][0] instanceof Soleil ||
                            sanctuaire[i].getFaces().get(0).getRessource()[0][0] instanceof Lune
            ))
                have = true;
            i++;
        }
        return have;
    }

}

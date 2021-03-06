package bot.NidoBot;

import diceForge.Cartes.Carte;
import diceForge.ElementPlateau.Bassin;
import diceForge.ElementPlateau.Ile;
import diceForge.ElementPlateau.Plateau;
import diceForge.Faces.Face;
import diceForge.OutilJoueur.De;
import diceForge.OutilJoueur.Joueur;
import diceForge.OutilJoueur.Ressource;
import diceForge.Structure.DiceForgeException;

import java.util.ArrayList;
import java.util.List;

import static diceForge.Faces.Face.typeFace.SIMPLE;
import static diceForge.OutilJoueur.Ressource.type.LUNE;
import static diceForge.OutilJoueur.Ressource.type.SOLEIL;

public class NidoFunctions {
    public static Stats getNbFaces(int numDe, De[] jeuDes, Ressource uneRess){
        Stats count = new Stats();
        for (Face face : jeuDes[numDe].getFaces())
            if (face.getTypeFace() == SIMPLE && face.getRessource().getType() == SOLEIL &&
                    uneRess.getType() == SOLEIL)
                count.incrementNbSoleils();
            else if(face.getTypeFace() == SIMPLE && face.getRessource().getType() == Ressource.type.LUNE &&
                    uneRess.getType() == Ressource.type.LUNE)
                count.incrementNbLunes();
        return count;
    }

    public static int getPosFaceQteMin(int numDe, De[] jeuDes, Ressource uneRess){ //recherche de min classique
        int min = 10000;
        int res = -1;
        Face[] faces = jeuDes[numDe].getFaces();
        for (int i = 0; i < faces.length; i++)
            if (faces[i].getTypeFace() == SIMPLE)
                if (faces[i].getRessource().getType() == Ressource.type.OR
                        && uneRess.getType() == Ressource.type.OR
                        && faces[i].getRessource().getQuantite() < min) {
                    min = faces[i].getRessource().getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource().getType() == Ressource.type.LUNE
                        && uneRess.getType() == Ressource.type.LUNE
                        && faces[i].getRessource().getQuantite() < min) {
                    min = faces[i].getRessource().getQuantite();
                    res = i;
                }
                else if (faces[i].getRessource().getType() == SOLEIL
                        && uneRess.getType() == SOLEIL
                        && faces[i].getRessource().getQuantite() < min) {
                    min = faces[i].getRessource().getQuantite();
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
            if (!sanctuaire[i].getFaces().isEmpty() && sanctuaire[i].getFace(0).getTypeFace() == SIMPLE && (
                    sanctuaire[i].getFace(0).getRessource().getType() == SOLEIL ||
                            sanctuaire[i].getFace(0).getRessource().getType() == LUNE
            ))
                have = true;
            i++;
        }
        return have;
    }
    public static boolean haveFaceType(De unDe, Ressource uneRess){
        boolean have = false;
        int i = 0;
        while (!have && i < unDe.getFaces().length){
//            System.out.println("i == " + i);
//            System.out.println(unDe.getFace(i).toString());
            if (uneRess.getType() == LUNE &&
                     unDe.getFace(i).getTypeFace() == SIMPLE &&
                    unDe.getFace(i).getRessource().getType() == LUNE)
                have = true;
            else if (uneRess.getType() == SOLEIL &&
                    unDe.getFace(i).getTypeFace() == SIMPLE &&
                    unDe.getFace(i).getRessource().getType() == SOLEIL)
                have = true;
            i++;
        }
        return have;
    }

    static List cartesAbordables(Joueur joueur, Plateau plateau) {
        List<Carte> cartesAbordables = new ArrayList<>();//Notre liste qui va contenir les cartes abordables par le joueur
        for (Ile ile : plateau.getIles()) {//On parcours les iles
            for (List<Carte> paquet : ile.getCartes()) {//Et les paquets
                for (Carte carte : paquet) {//Et les cartes
                    int prixSoleil = 0, prixLune = 0;
                    for (Ressource prix : carte.getCout()) {//Convertisseur object -> int des ressources
                        if (prix.getType() == Ressource.type.SOLEIL)
                            prixSoleil += prix.getQuantite();
                        else if (prix.getType() == Ressource.type.LUNE)
                            prixLune += prix.getQuantite();
                        else//Cela ne devrait jamais arriver
                            throw new DiceForgeException("Coordinateur", "Une carte doit couter soit des lunes soit des soleils !");
                    }
                    if (prixSoleil <= joueur.getSoleil() && prixLune <= joueur.getLune())//Si le joueur peut l'acheter on l'ajoute
                        cartesAbordables.add(carte);
                }
            }
        }
        return cartesAbordables;
    }

}

package bot.AubotV2;

class MainAubotV2{
    public static void main(String[]args){
        int nombreDeJoueur = 2;
        int nombreDePartieParMatch = 1000;
        int nombreDePopulationMax = 30;
        int uneChanceSurDeMuter = 40;
        Evolution ev = new Evolution(nombreDeJoueur, nombreDePartieParMatch, nombreDePopulationMax, uneChanceSurDeMuter);
        System.out.println("Programme terminé sans encombre");
        }
}

package bot.AubotV2.src;

class MainAubotV2{
    public static void main(String[]args){
        int nombreDeJoueur = 2;
        int nombreDePartieParMatch = 1000; //par tranche de 1000 uniquement
        int nombreDePopulationMax = 50000;
        int uneChanceSurDeMuter = 20;
        new Evolution(nombreDeJoueur, nombreDePartieParMatch, nombreDePopulationMax, uneChanceSurDeMuter);
        System.out.println("Programme terminé");
        }
}

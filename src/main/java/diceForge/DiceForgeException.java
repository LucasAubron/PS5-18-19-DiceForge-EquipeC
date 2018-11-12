package diceForge;

public class DiceForgeException extends RuntimeException {
    private String localisation;
    public DiceForgeException(String localisation, String messageErreur){
        super(messageErreur);
        this.localisation = localisation;
    }

    String getLocalisation() {
        return localisation;
    }
}

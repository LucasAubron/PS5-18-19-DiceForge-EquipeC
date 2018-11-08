package diceForge;

class DiceForgeException extends RuntimeException {
    private String localisation;
    DiceForgeException(String localisation, String messageErreur){
        super(messageErreur);
        this.localisation = localisation;
    }

    String getLocalisation() {
        return localisation;
    }
}

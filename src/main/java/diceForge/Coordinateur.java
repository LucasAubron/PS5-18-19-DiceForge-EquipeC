package diceForge;

public class Coordinateur {
    private Temple temple;
    private Ile[] iles;
    private PortailsOriginels portailsOriginels;

    public Coordinateur(Temple temple, Ile[] iles, PortailsOriginels portailsOriginels){
        this.temple = temple;
        this.iles = iles;
        this.portailsOriginels = portailsOriginels;
    }
}

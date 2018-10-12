package diceForge;

import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        try {
            Plateau plateau = new Plateau();
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}

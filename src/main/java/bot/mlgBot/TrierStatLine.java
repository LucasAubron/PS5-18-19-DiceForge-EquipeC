package bot.mlgBot;

import java.util.Comparator;

public class TrierStatLine implements Comparator<StatLine> {
    public int compare(StatLine s1, StatLine s2){
        return s2.getPdg() - s1.getPdg();
    }
}

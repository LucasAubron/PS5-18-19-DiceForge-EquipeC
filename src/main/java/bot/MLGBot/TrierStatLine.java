package bot.mlgBot;

import java.util.Comparator;

public class TrierStatLine implements Comparator<bot.mlgBot.StatLine> {
    public int compare(bot.mlgBot.StatLine s1, bot.mlgBot.StatLine s2){
        return s2.getPdg() - s1.getPdg();
    }
}

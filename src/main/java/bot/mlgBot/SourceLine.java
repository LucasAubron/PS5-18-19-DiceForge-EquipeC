package bot.mlgBot;

import java.util.List;

public class SourceLine {
    private List<Byte> ligne;
    public SourceLine(List<StatLine> statLines){
    }

    public byte[] getLigne() {
        byte[] bytes = new byte[ligne.size()];
        for (int i = 0; i != ligne.size(); ++i)
            bytes[i] = ligne.get(i);
        return bytes;
    }
}

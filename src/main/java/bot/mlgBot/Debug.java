package bot.mlgBot;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Debug {
    public static void main(String[] args) {
        try{
            int gen = 247;
            int nbrJoueur = 2;
            RandomAccessFile file = new RandomAccessFile("src\\main\\java\\bot\\mlgBot\\MLGBot" + nbrJoueur + "JGen" + gen, "rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int)file.length());
            int x = channel.read(buf);
            for (int i = 0; i != x; ++i){
                byte b = buf.get(i);
                if (b < 40)
                    System.out.print((int)b);
                else if (b == "@".getBytes()[0])
                    System.out.println("@");
                else if (b >= 48 && b < 58)
                    System.out.print(b-"0".getBytes()[0]);
                else {
                    if (b == ",".getBytes()[0])
                    System.out.println(",");
                    else if (b == ";".getBytes()[0])
                        System.out.println("\n;");
                    else if (b == "?".getBytes()[0])
                        System.out.print("?");
                    else if (b == ":".getBytes()[0])
                        System.out.print(":");
                    //if (b == 0)
                        //System.out.println("sldfhlsdkfhlkdhf");
                }
                System.out.print(" ");
            }
            file.close();
        } catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }
}

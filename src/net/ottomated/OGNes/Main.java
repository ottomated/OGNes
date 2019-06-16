package net.ottomated.OGNes;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {
        Nes nes = new Nes("tank.nes");

        while (true) {
            long now = System.currentTimeMillis();
            long target = (long) (now + 1000.0 / 60.0);
            nes.frame();
            long diff = target - System.currentTimeMillis();
            if (diff > 0) {
                Thread.sleep(diff);
            } else {
                System.out.println(diff);
            }
        }
    }
}

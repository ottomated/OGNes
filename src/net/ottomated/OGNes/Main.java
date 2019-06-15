package net.ottomated.OGNes;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Nes nes = new Nes("zelda.nes");

        long last = System.currentTimeMillis();
        while (true) {
            long now = System.currentTimeMillis();
            long target = (long) (now + 1000.0 / 60.0);
            nes.frame();
            long diff = target - System.currentTimeMillis();
            if (diff > 0)
                Thread.sleep(diff);
        }
    }
}

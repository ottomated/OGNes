package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;

public class Test {

    public static void main(String[] args) throws Exception {
        Main.settings = new Settings();
        Nes nes = new Nes();
        nes.loadRom("/home/otto/Projects/Java/OGNes/mario.nes");
        nes.playTAS("/home/otto/Projects/Java/OGNes/happylee-supermariobros,warped.fm2");
        while(true) {
            nes.frame();
        }
    }

}

package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;

public class Test {

    public static void main(String[] args) throws IOException, LineUnavailableException {
        Nes nes = new Nes("tank.nes");
    }

}

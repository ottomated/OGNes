package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import java.io.File;
import java.io.IOException;

public class Nes {
    public Cpu cpu;
    public Ppu ppu;
    public Mapper mapper;
    public Rom rom;

    Nes(String romPath) throws IOException {
        File program = new File(romPath);
        rom = new Rom(program);
        mapper = rom.mapper;

        cpu = new Cpu();
        cpu.reset();
        cpu.loadRom(rom);
    }

    public void start() {

    }
}
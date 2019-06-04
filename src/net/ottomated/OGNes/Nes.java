package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import java.io.File;
import java.io.IOException;

public class Nes {
    public Cpu cpu;
    public Ppu ppu;
    public Mapper mapper;
    public Rom rom;
    public Graphics graphics;

    Nes(String romPath) throws IOException {
        File program = new File(romPath);
        rom = new Rom(program);
        mapper = rom.mapper;

        cpu = new Cpu();
        cpu.reset();
        cpu.loadRom(rom);

        ppu = new Ppu(this);

        graphics = new Graphics();
    }

    private void frame() {

    }
}

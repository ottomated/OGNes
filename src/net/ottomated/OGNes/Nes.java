package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import java.io.File;
import java.io.IOException;

public class Nes {
    public static final int SAMPLE_RATE = 44100;
    public static final int PREFERRED_FRAME_RATE = 60;
    public Cpu cpu;
    public Ppu ppu;
    public Apu apu;
    public Mapper mapper;
    public Rom rom;
    public Graphics graphics;
    public Controller controller;

    Nes(String romPath) throws IOException {
        File program = new File(romPath);
        rom = new Rom(program);
        mapper = rom.mapper;

        mapper.nes = this;
        cpu = new Cpu();
        cpu.loadRom(rom);
        cpu.reset();

        ppu = new Ppu(this);

        apu = new Apu(this);

        graphics = new Graphics();
        controller = new Controller();
        graphics.addKeyListener(controller);
        reset();
    }

    private void reset() {
        cpu.reset();
        ppu.reset();
        apu.reset();
    }


    private void frame() {
        int cycles = 0;
        for (;;) {
            if (cpu.cyclesToHalt == 0) {
                cycles = cpu.cycle();
                cycles *= 3;
            } else {
                if (cpu.cyclesToHalt > 8) {
                    cycles = 24;
                    cpu.cyclesToHalt -= 8;
                } else {
                    cycles = cpu.cyclesToHalt * 3;
                    cpu.cyclesToHalt = 0
                }
            }
            for (; cycles > 0; cycles--) {
                if(ppu.)
            }
        }
    }
}

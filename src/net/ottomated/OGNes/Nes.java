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

        graphics = new Graphics();
        controller = new Controller();
        graphics.addKeyListener(controller);
        start();
    }

    private void start() {
        int count = 341 * 262 / 3;
        for (int i = 0; i < count; i++) {
            cpu.cycle();
            ppu.run();
            ppu.run();
            ppu.run();
        }
        start();
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

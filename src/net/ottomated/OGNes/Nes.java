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

        //apu = new Apu(this);

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
        boolean break_frameLoop = false;
        for (; ; ) {
            if (cpu.cyclesToHalt == 0) {
                cycles = cpu.cycle();
                cycles *= 3;
            } else {
                if (cpu.cyclesToHalt > 8) {
                    cycles = 24;
                    cpu.cyclesToHalt -= 8;
                } else {
                    cycles = cpu.cyclesToHalt * 3;
                    cpu.cyclesToHalt = 0;
                }
            }
            for (; cycles > 0; cycles--) {
                if (ppu.curX == ppu.spr0HitX && ppu.f_spVisibility == 1 && ppu.scanline - 21 == ppu.spr0HitY) {
                    ppu.setStatusFlag(Ppu.STATUS_SPRITE0HIT, true);
                }
                if (ppu.requestEndFrame) {
                    ppu.nmiCounter--;
                    if (ppu.nmiCounter == 0) {
                        ppu.requestEndFrame = false;
                        ppu.startVBlank();
                        break_frameLoop = true;
                        break;
                    }
                }
                ppu.curX++;
                if (ppu.curX == 341) {
                    ppu.curX = 0;
                    ppu.endScanline();
                }
            }
            if (break_frameLoop) break;
        }
    }
}

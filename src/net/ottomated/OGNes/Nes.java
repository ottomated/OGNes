package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        cpu = new Cpu(this);
        ppu = new Ppu(this);
        apu = new Apu(this);
        controller = new Controller();

        rom = new Rom(new File(romPath));
        reset();
        mapper = rom.mapper;
        mapper.nes = this;
        mapper.loadROM();
        ppu.setMirroring(rom.getMirroring());

        graphics = new Graphics();
        graphics.addKeyListener(controller);
        while (true) {
            frame();
        }
        //System.out.println(ppu.palTable);
        //       frame();
        //     frame();
        //   frame();
    }

    private void reset() {
        if (mapper != null)
            mapper.reset();
        cpu.reset();
        ppu.reset();
        apu.reset();
    }


    private void frame() {
        int cycles = 0;
        boolean sound = true;
        boolean break_frameLoop = false;
        for (; ; ) {
            if (cpu.cyclesToHalt == 0) {
                cycles = cpu.cycle();
                if (sound) {
                    apu.clockFrameCounter(cycles);
                }
                cycles *= 3;
            } else {
                if (cpu.cyclesToHalt > 8) {
                    cycles = 24;
                    if (sound) {
                        apu.clockFrameCounter(cycles);
                    }
                    cpu.cyclesToHalt -= 8;
                } else {
                    cycles = cpu.cyclesToHalt * 3;
                    if (sound) {
                        apu.clockFrameCounter(cpu.cyclesToHalt);
                    }
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

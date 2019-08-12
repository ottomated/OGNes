package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Nes {
    public static final int SAMPLE_RATE = 44100;
    public static final int PREFERRED_FRAME_RATE = 60;
    public Cpu cpu;
    public Ppu ppu;
    public Apu apu;
    public Mapper mapper;
    public Rom rom;
    public Graphics graphics;
    public Controller[] controllers;
    private ControllerMaster controllerMaster;
    public AudioOut speakers;
    private boolean TAS = false;
    private List<boolean[][]> video;
    private int videoIndex;
    volatile boolean ready = false;
    volatile boolean inFrame = false;
    volatile boolean sound = true;
    public File romFile;
    int frameCount;

    Nes() {
        graphics = new Graphics(this);
        controllerMaster = new ControllerMaster(graphics, new Controller[2]);
        graphics.addKeyListener(controllerMaster);
    }

    void loadRom(String path, boolean setReadyWhenDone) throws IOException, LineUnavailableException {

        ready = false;
        frameCount = 0;
        cpu = new Cpu(this);
        ppu = new Ppu(this);
        apu = new Apu(this);
        controllers = new Controller[2];

        controllers[0] = new Controller(Main.settings.controller0);
        controllers[1] = new Controller(Main.settings.controller1);
        controllerMaster.controllers = controllers;

        romFile = new File(path);
        rom = new Rom(romFile);
        reset();
        mapper = rom.mapper;
        mapper.nes = this;
        mapper.loadROM();
        ppu.setMirroring(rom.getMirroring());

        speakers = new AudioOut();
        for (int i = 0; i < ppu.spriteMem.length; i++) {
            //System.out.println(ppu.spriteMem[i]);
        }
        ready = setReadyWhenDone;
    }

    void loadRom(String path) throws IOException, LineUnavailableException {
        loadRom(path, true);
    }

    public void reset() {
        if (mapper != null)
            mapper.reset();
        cpu.reset();
        ppu.reset();
        apu.reset();
    }

    void frame() throws Exception {
        if (!ready) return;
        inFrame = true;
        ppu.startFrame();
        int cycles;
        boolean break_frameLoop = false;
        if (TAS) {
            controllers[0].state = video.get(videoIndex)[0];
            controllers[1].state = video.get(videoIndex)[1];
            videoIndex++;
        }
        do {
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
                        apu.clockFrameCounter(8);
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
            //System.out.println(frameCount + "  " + cycles + " " + cpu.memory[0x2002] + "  " + ppu.curX + ", " + ppu.scanline);
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
        } while (!break_frameLoop);
        frameCount++;
        inFrame = false;
    }

    void playAudio(float left, float right) {
        speakers.play(left, right);
    }

    void playTAS(String path) throws IOException, LineUnavailableException {
        loadRom(romFile.getPath(), false);

        TAS = true;
        video = new ArrayList<>();
        videoIndex = 0;
        Scanner fScan = new Scanner(new File(path));
        while (fScan.hasNextLine()) {
            String line = fScan.nextLine();
            if (!line.startsWith("|")) continue;
            Scanner lScan = new Scanner(line);
            lScan.useDelimiter("\\|");
            lScan.nextInt(); // command
            boolean[] input0 = new boolean[8];
            boolean[] input1 = new boolean[8];

            String s = lScan.next();
            for (char c : s.toCharArray()) {
                int i = getButton(c);
                if (i >= 0) {
                    input0[i] = true;
                }
            }
            s = lScan.next();
            for (char c : s.toCharArray()) {
                int i = getButton(c);
                if (i >= 0) {
                    input1[i] = true;
                }
            }
            video.add(new boolean[][]{input0, input1});
        }
        for (boolean[][] frame : video) {
            //System.out.println(Arrays.deepToString(frame));
        }
        graphics.removeKeyListener(controllerMaster);
        ready = true;
    }

    private int getButton(char c) {
        int i;
        switch (c) {
            case 'A':
                i = Controller.Button.A;
                break;
            case 'B':
                i = Controller.Button.B;
                break;
            case 'S':
                i = Controller.Button.SELECT;
                break;
            case 'T':
                i = Controller.Button.START;
                break;
            case 'U':
                i = Controller.Button.UP;
                break;
            case 'D':
                i = Controller.Button.DOWN;
                break;
            case 'L':
                i = Controller.Button.LEFT;
                break;
            case 'R':
                i = Controller.Button.RIGHT;
                break;
            default:
                i = -1;
        }
        return i;
    }
}

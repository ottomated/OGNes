package net.ottomated.OGNes;

import net.ottomated.OGNes.mappers.Mapper;

import javax.sound.sampled.LineUnavailableException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private List<int[][]> video;
    private int videoIndex;
    volatile boolean ready = false;
    volatile boolean inFrame = false;
    volatile boolean sound = true;
    public File romFile;

    Nes() {
        graphics = new Graphics(this);
    }

    void loadRom(String path) throws IOException, LineUnavailableException {
        ready = false;
        cpu = new Cpu(this);
        ppu = new Ppu(this);
        apu = new Apu(this);
        controllers = new Controller[2];
        controllers[0] = new Controller(new int[]{
                KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_T, KeyEvent.VK_Y
        });

        controllers[1] = new Controller(new int[]{
                KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_SHIFT, KeyEvent.VK_ENTER, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD
        });
        ControllerMaster controllerMaster = new ControllerMaster(controllers);

        romFile = new File(path);
        rom = new Rom(romFile);
        reset();
        mapper = rom.mapper;
        mapper.nes = this;
        mapper.loadROM();
        ppu.setMirroring(rom.getMirroring());

        speakers = new AudioOut();
        graphics.removeKeyListener(controllerMaster);
        graphics.addKeyListener(controllerMaster);
        ready = true;
    }

    public void reset() {
        if (mapper != null)
            mapper.reset();
        cpu.reset();
        ppu.reset();
        apu.reset();
    }
    public void softReset() {
        ready = false;
        reset();
        ready = true;
    }

    void frame() throws Exception {
        inFrame = true;
        ppu.startFrame();
        int cycles = 0;
        boolean break_frameLoop = false;
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
        } while (!break_frameLoop);
        if (TAS) {
            controllers[0].state = video.get(videoIndex)[0];
            controllers[1].state = video.get(videoIndex)[1];
            videoIndex++;
        }
        inFrame = false;
    }

    void playAudio(float left, float right) {
        speakers.play(left, right);
    }

    void playTAS(String path) throws IOException {
        TAS = true;
        video = new ArrayList<>();
        videoIndex = 0;
        Scanner fScan = new Scanner(new File(path));
        while (fScan.hasNextLine()) {
            String line = fScan.nextLine();
            if (!line.startsWith("|")) continue;
            Scanner lScan = new Scanner(line);
            lScan.useDelimiter("\\|");
            lScan.next(); // command
            int[] input0 = new int[8];
            int[] input1 = new int[8];
            for (int i = 0; i < 8; i++) {
                input0[i] = 0x40;
                input1[i] = 0x40;
            }
            String s = lScan.next();
            for (char c : s.toCharArray()) {
                int i = getButton(c);
                if (i >= 0) {
                    input0[i] = 0x41;
                }
            }
            s = lScan.next();
            for (char c : s.toCharArray()) {
                int i = getButton(c);
                if (i >= 0) {
                    input1[i] = 0x41;
                }
            }
            video.add(new int[][]{input0, input1});
        }
        graphics.removeKeyListener(controllerMaster);
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

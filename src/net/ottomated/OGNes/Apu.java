package net.ottomated.OGNes;

import net.ottomated.OGNes.apu.DMChannel;
import net.ottomated.OGNes.apu.NoiseChannel;
import net.ottomated.OGNes.apu.SquareChannel;
import net.ottomated.OGNes.apu.TriangleChannel;

public class Apu {
    public static final double CPU_FREQ_NTSC = 1789772.5;
    public Nes nes;
    SquareChannel square1;
    SquareChannel square2;
    TriangleChannel triangle;
    NoiseChannel noise;
    DMChannel dmc;
    int frameIrqCounter;
    int frameIrqCounterMax = 4;
    int initCounter = 2048;
    int channelEnableValue;

    int sampleRate = 44100;

    int[] square_table;
    int[] tnd_table;

    int[] lengthLookup = new int[]{
            0x0A, 0xFE,
            0x14, 0x02,
            0x28, 0x04,
            0x50, 0x06,
            0xA0, 0x08,
            0x3C, 0x0A,
            0x0E, 0x0C,
            0x1A, 0x0E,
            0x0C, 0x10,
            0x18, 0x12,
            0x30, 0x14,
            0x60, 0x16,
            0xC0, 0x18,
            0x48, 0x1A,
            0x10, 0x1C,
            0x20, 0x1E
    };
    int[] dmcFreqLookup = new int[]{
            0xd60, 0xbe0, 0xaa0, 0xa00, 0x8f0, 0x7f0, 0x710, 0x6b0, 0x5f0, 0x500, 0x470, 0x400, 0x350, 0x2a0, 0x240, 0x1b0
    };
    int[] noiseWaveLengthLookup = new int[]{
            0x004, 0x008, 0x10, 0x020, 0x040, 0x060, 0x080, 0x0a0, 0x0ca, 0x0fe, 0x17c, 0x1fc, 0x2fa, 0x3f8, 0x7f2, 0xfe4
    };
    int squareTable;
    int tndTable;

    boolean frameIrqEnabled = false;
    boolean frameIrqActive;
    boolean frameClockNow;
    boolean startedPlaying = false;
    boolean recordOutput = false;
    boolean initingHardware = false;

    int masterFrameCounter;
    int derivedFrameCounter;
    int countSequence;
    int sampleTimer;
    int frameTime;
    int sampleTimerMax;
    int sampleCount;
    int triValue = 0;

    int smpSquare1;
    int smpSquare2;
    int smpTriangle;
    int smpDmc;
    int accCount;

    int prevSampleL;
    int prevSampleR;
    int smpAccumL;
    int smpAccumR;
    int dacRange = 0;
    int dcValue = 0;

    int masterVolume = 256;

    int stereoPosLSquare1;
    int stereoPosLSquare2;
    int stereoPosLTriangle;
    int stereoPosLNoise;
    int stereoPosLDMC;
    int stereoPosRSquare1;
    int stereoPosRSquare2;
    int stereoPosRTriangle;
    int stereoPosRNoise;
    int stereoPosRDMC;

    int extraCycles;
    int maxSample;
    int minSample;

    int[] panning;

    Apu(Nes nes) {
        this.nes = nes;
        square1 = new SquareChannel(this, true);
        square2 = new SquareChannel(this, false);
        triangle = new TriangleChannel(this);
        noise = new NoiseChannel(this);
        dmc = new DMChannel(this);

        prevSampleL = 0;
        prevSampleR = 0;
        smpAccumL = 0;
        smpAccumR = 0;

        panning = new int[]{80, 170, 100, 150, 128};
        setPanning(panning);
        initDACtables();
        for (int i = 0; i < 0x14; i++) {
            if (i == 0x10) {
                writeReg(0x4010, 0x10);
            } else {
                writeReg(0x4000 + i, 0);
            }
        }
        reset();
    }

    void reset() {
        sampleRate = Nes.SAMPLE_RATE;
        sampleTimerMax = (int) ((1024.0 * CPU_FREQ_NTSC * Nes.PREFERRED_FRAME_RATE) / (sampleRate * 60.0));
        frameTime = (int) (14915.0 * Nes.PREFERRED_FRAME_RATE / 60.0);
        sampleTimer = 0;
        updateChannelEnable(0);
        masterFrameCounter = 0;
        derivedFrameCounter = 0;
        countSequence = 0;
        sampleCount = 0;
        initCounter = 2048;
        frameIrqEnabled = false;
        initingHardware = false;

        resetCounter();

        square1.reset();
        square2.reset();
        triangle.reset();
        noise.reset();
        dmc.reset();

        accCount = 0;
        smpSquare1 = 0;
        smpSquare2 = 0;
        smpTriangle = 0;
        smpDmc = 0;

        frameIrqCounterMax = 4;

        channelEnableValue = 0xff;
        startedPlaying = false;
        prevSampleL = 0;
        prevSampleR = 0;
        smpAccumL = 0;
        smpAccumR = 0;
        maxSample = -500000;
        minSample = 500000;
    }

    public int readReg(int address) {

        int tmp = 0;
        tmp |= square1.getLengthStatus();
        tmp |= square2.getLengthStatus() << 1;
        tmp |= triangle.getLengthStatus() << 2;
        tmp |= noise.getLengthStatus() << 3;
        tmp |= dmc.getLengthStatus() << 4;
        tmp |= (frameIrqActive && frameIrqEnabled ? 1 : 0) << 6;
        tmp |= dmc.getIrqStatus() << 7;

        frameIrqActive = false;
        dmc.irqGenerated = false;

        return tmp & 0xffff;
    }

    public void writeReg(int address, int value) {
        if (address >= 0x4000 && address < 0x4004) {
            // Square Wave 1 Control
            square1.writeReg(address, value);
            // console.log("Square Write");
        } else if (address >= 0x4004 && address < 0x4008) {
            // Square 2 Control
            square2.writeReg(address, value);
        } else if (address >= 0x4008 && address < 0x400c) {
            // Triangle Control
            triangle.writeReg(address, value);
        } else if (address >= 0x400c && address <= 0x400f) {
            // Noise Control
            noise.writeReg(address, value);
        } else if (address == 0x4010) {
            // DMC Play mode & DMA frequency
            dmc.writeReg(address, value);
        } else if (address == 0x4011) {
            // DMC Delta Counter
            dmc.writeReg(address, value);
        } else if (address == 0x4012) {
            // DMC Play code starting address
            dmc.writeReg(address, value);
        } else if (address == 0x4013) {
            // DMC Play code length
            dmc.writeReg(address, value);
        } else if (address == 0x4015) {
            // Channel enable
            updateChannelEnable(value);

            if (value != 0 && initCounter > 0) {
                // Start hardware initialization
                initingHardware = true;
            }

            // DMC/IRQ Status
            dmc.writeReg(address, value);
        } else if (address == 0x4017) {
            // Frame counter control
            countSequence = (value >> 7) & 1;
            masterFrameCounter = 0;
            frameIrqActive = false;

            frameIrqEnabled = ((value >> 6) & 0x1) == 0;

            if (countSequence == 0) {
                // NTSC:
                frameIrqCounterMax = 4;
                derivedFrameCounter = 4;
            } else {
                // PAL:
                frameIrqCounterMax = 5;
                derivedFrameCounter = 0;
                frameCounterTick();
            }
        }
    }

    void resetCounter() {
        if (countSequence == 0) {
            derivedFrameCounter = 4;
        } else {
            derivedFrameCounter = 0;
        }
    }

    void updateChannelEnable(int value) {
        channelEnableValue = value & 0xffff;
        square1.setEnabled((value & 1) != 0);
        square2.setEnabled((value & 2) != 0);
        triangle.setEnabled((value & 4) != 0);
        noise.setEnabled((value & 8) != 0);
        dmc.setEnabled((value & 16) != 0);
    }

    public void clockFrameCounter(int nCycles) {
        if (initCounter > 0) {
            if (initingHardware) {
                initCounter -= nCycles;
                if (initCounter <= 0) {
                    initingHardware = false;
                }
                return;
            }
        }

        // Don't process ticks beyond next sampling:
        nCycles += extraCycles;
        int maxCycles = sampleTimerMax - sampleTimer;
        if (nCycles << 10 > maxCycles) {
            extraCycles = ((nCycles << 10) - maxCycles) >> 10;
            nCycles -= extraCycles;
        } else {
            extraCycles = 0;
        }

        // Clock DMC:
        if (dmc.isEnabled) {
            dmc.shiftCounter -= nCycles << 3;
            while (dmc.shiftCounter <= 0 && dmc.dmaFrequency > 0) {
                dmc.shiftCounter += dmc.dmaFrequency;
                dmc.clockDmc();
            }
        }

        // Clock Triangle channel Prog timer:
        if (triangle.progTimerMax > 0) {
            triangle.progTimerCount -= nCycles;
            while (triangle.progTimerCount <= 0) {
                triangle.progTimerCount += triangle.progTimerMax + 1;
                if (triangle.linearCounter > 0 && triangle.lengthCounter > 0) {
                    triangle.triangleCounter++;
                    triangle.triangleCounter &= 0x1f;

                    if (triangle.isEnabled) {
                        if (triangle.triangleCounter >= 0x10) {
                            // Normal value.
                            triangle.sampleValue = triangle.triangleCounter & 0xf;
                        } else {
                            // Inverted value.
                            triangle.sampleValue = 0xf - (triangle.triangleCounter & 0xf);
                        }
                        triangle.sampleValue <<= 4;
                    }
                }
            }
        }

        // Clock Square channel 1 Prog timer:
        square1.progTimerCount -= nCycles;
        if (square1.progTimerCount <= 0) {
            square1.progTimerCount += (square1.progTimerMax + 1) << 1;

            square1.squareCounter++;
            square1.squareCounter &= 0x7;
            square1.updateSampleValue();
        }

        // Clock Square channel 2 Prog timer:
        square2.progTimerCount -= nCycles;
        if (square2.progTimerCount <= 0) {
            square2.progTimerCount += (square2.progTimerMax + 1) << 1;

            square2.squareCounter++;
            square2.squareCounter &= 0x7;
            square2.updateSampleValue();
        }

        // Clock noise channel Prog timer:
        int acc_c = nCycles;
        if (noise.progTimerCount - acc_c > 0) {
            // Do all cycles at once:
            noise.progTimerCount -= acc_c;
            noise.accCount += acc_c;
            noise.accValue += acc_c * noise.sampleValue;
        } else {
            // Slow-step:
            while (acc_c-- > 0) {
                if (--noise.progTimerCount <= 0 && noise.progTimerMax > 0) {
                    // Update noise shift register:
                    noise.shiftReg <<= 1;
                    noise.tmp =
                            ((noise.shiftReg << (noise.randomMode == 0 ? 1 : 6)) ^
                                    noise.shiftReg) &
                                    0x8000;
                    if (noise.tmp != 0) {
                        // Sample value must be 0.
                        noise.shiftReg |= 0x01;
                        noise.randomBit = 0;
                        noise.sampleValue = 0;
                    } else {
                        // Find sample value:
                        noise.randomBit = 1;
                        if (noise.isEnabled && noise.lengthCounter > 0) {
                            noise.sampleValue = noise.masterVolume;
                        } else {
                            noise.sampleValue = 0;
                        }
                    }

                    noise.progTimerCount += noise.progTimerMax;
                }

                noise.accValue += noise.sampleValue;
                noise.accCount++;
            }
        }

        // Frame IRQ handling:
        if (frameIrqEnabled && frameIrqActive) {
            nes.cpu.requestIrq(Cpu.Interrupt.IRQ);
        }

        // Clock frame counter at double CPU speed:
        masterFrameCounter += nCycles << 1;
        if (masterFrameCounter >= frameTime) {
            // 240Hz tick:
            masterFrameCounter -= frameTime;
            frameCounterTick();
        }

        // Accumulate sample value:
        accSample(nCycles);

        // Clock sample timer:
        sampleTimer += nCycles << 10;
        if (sampleTimer >= sampleTimerMax) {
            // Sample channels:
            sample();
            sampleTimer -= sampleTimerMax;
        }
    }

    void accSample(int cycles) {
        // Special treatment for triangle channel - need to interpolate.
        if (triangle.sampleCondition) {
            triValue = (triangle.progTimerCount << 4) / (triangle.progTimerMax + 1)
            ;
            if (triValue > 16) {
                triValue = 16;
            }
            if (triangle.triangleCounter >= 16) {
                triValue = 16 - triValue;
            }

            // Add non-interpolated sample value:
            triValue += triangle.sampleValue;
        }

        // Now sample normally:
        if (cycles == 2) {
            smpTriangle += triValue << 1;
            smpDmc += dmc.sample << 1;
            smpSquare1 += square1.sampleValue << 1;
            smpSquare2 += square2.sampleValue << 1;
            accCount += 2;
        } else if (cycles == 4) {
            smpTriangle += triValue << 2;
            smpDmc += dmc.sample << 2;
            smpSquare1 += square1.sampleValue << 2;
            smpSquare2 += square2.sampleValue << 2;
            accCount += 4;
        } else {
            smpTriangle += cycles * triValue;
            smpDmc += cycles * dmc.sample;
            smpSquare1 += cycles * square1.sampleValue;
            smpSquare2 += cycles * square2.sampleValue;
            accCount += cycles;
        }
    }

    void frameCounterTick() {
        derivedFrameCounter++;
        if (derivedFrameCounter >= frameIrqCounterMax) {
            derivedFrameCounter = 0;
        }

        if (derivedFrameCounter == 1 || derivedFrameCounter == 3) {
            // Clock length & sweep:
            triangle.clockLengthCounter();
            square1.clockLengthCounter();
            square2.clockLengthCounter();
            noise.clockLengthCounter();
            square1.clockSweep();
            square2.clockSweep();
        }

        if (derivedFrameCounter >= 0 && derivedFrameCounter < 4) {
            // Clock linear & decay:
            square1.clockEnvDecay();
            square2.clockEnvDecay();
            noise.clockEnvDecay();
            triangle.clockLinearCounter();
        }

        if (derivedFrameCounter == 3 && countSequence == 0) {
            // Enable IRQ:
            frameIrqActive = true;
        }

        // End of 240Hz tick
    }

    void sample() {
        int sq_index, tnd_index;

        if (accCount > 0) {
            smpSquare1 <<= 4;
            smpSquare1 = smpSquare1 / accCount;

            smpSquare2 <<= 4;
            smpSquare2 = smpSquare2 / accCount;

            smpTriangle = smpTriangle / accCount;

            smpDmc <<= 4;
            smpDmc = smpDmc / accCount;

            accCount = 0;
        } else {
            smpSquare1 = square1.sampleValue << 4;
            smpSquare2 = square2.sampleValue << 4;
            smpTriangle = triangle.sampleValue;
            smpDmc = dmc.sample << 4;
        }

        int smpNoise = (noise.accValue << 4) / noise.accCount;
        noise.accValue = smpNoise >> 4;
        noise.accCount = 1;

        // Stereo sound.

        // Left channel:
        sq_index =
                (smpSquare1 * stereoPosLSquare1 +
                        smpSquare2 * stereoPosLSquare2) >>
                        8;
        tnd_index =
                (3 * smpTriangle * stereoPosLTriangle +
                        (smpNoise << 1) * stereoPosLNoise +
                        smpDmc * stereoPosLDMC) >>
                        8;
        if (sq_index >= square_table.length) {
            sq_index = square_table.length - 1;
        }
        if (tnd_index >= tnd_table.length) {
            tnd_index = tnd_table.length - 1;
        }
        int sampleValueL =
                square_table[sq_index] + tnd_table[tnd_index] - dcValue;

        // Right channel:
        sq_index =
                (smpSquare1 * stereoPosRSquare1 +
                        smpSquare2 * stereoPosRSquare2) >>
                        8;
        tnd_index =
                (3 * smpTriangle * stereoPosRTriangle +
                        (smpNoise << 1) * stereoPosRNoise +
                        smpDmc * stereoPosRDMC) >>
                        8;
        if (sq_index >= square_table.length) {
            sq_index = square_table.length - 1;
        }
        if (tnd_index >= tnd_table.length) {
            tnd_index = tnd_table.length - 1;
        }
        int sampleValueR =
                square_table[sq_index] + tnd_table[tnd_index] - dcValue;

        // Remove DC from left channel:
        int smpDiffL = sampleValueL - prevSampleL;
        prevSampleL += smpDiffL;
        smpAccumL += smpDiffL - (smpAccumL >> 10);
        sampleValueL = smpAccumL;

        // Remove DC from right channel:
        int smpDiffR = sampleValueR - prevSampleR;
        prevSampleR += smpDiffR;
        smpAccumR += smpDiffR - (smpAccumR >> 10);
        sampleValueR = smpAccumR;

        // Write:
        if (sampleValueL > maxSample) {
            maxSample = sampleValueL;
        }
        if (sampleValueL < minSample) {
            minSample = sampleValueL;
        }


        nes.playAudio(sampleValueL / 32768.0f, sampleValueR / 32768.0f);


        // Reset sampled values:
        smpSquare1 = 0;
        smpSquare2 = 0;
        smpTriangle = 0;
        smpDmc = 0;
    }

    public int getLengthMax(int value) {
        return lengthLookup[value >> 3];
    }

    public int getDmcFrequency(int value) {
        if (value >= 0 && value < 0x10) {
            return dmcFreqLookup[value];
        }
        return 0;
    }

    public int getNoiseWaveLength(int value) {
        if (value >= 0 && value < 0x10) {
            return noiseWaveLengthLookup[value];
        }
        return 0;
    }

    void setPanning(int[] pos) {
        System.arraycopy(pos, 0, panning, 0, 5);
        updateStereoPos();
    }

    void setMasterVolume(int value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 256) {
            value = 256;
        }
        masterVolume = value;
        updateStereoPos();
    }

    void updateStereoPos() {
        stereoPosLSquare1 = (panning[0] * masterVolume) >> 8;
        stereoPosLSquare2 = (panning[1] * masterVolume) >> 8;
        stereoPosLTriangle = (panning[2] * masterVolume) >> 8;
        stereoPosLNoise = (panning[3] * masterVolume) >> 8;
        stereoPosLDMC = (panning[4] * masterVolume) >> 8;

        stereoPosRSquare1 = masterVolume - stereoPosLSquare1;
        stereoPosRSquare2 = masterVolume - stereoPosLSquare2;
        stereoPosRTriangle = masterVolume - stereoPosLTriangle;
        stereoPosRNoise = masterVolume - stereoPosLNoise;
        stereoPosRDMC = masterVolume - stereoPosLDMC;
    }

    void initDACtables() {
        double value;
        int ival, i;
        int max_sqr = 0;
        int max_tnd = 0;

        square_table = new int[32 * 16];
        tnd_table = new int[204 * 16];

        for (i = 0; i < 32 * 16; i++) {
            value = 95.52 / (8128.0 / (i / 16.0) + 100.0);
            value *= 0.98411;
            value *= 50000.0;
            ival = (int) value;

            square_table[i] = ival;
            if (ival > max_sqr) {
                max_sqr = ival;
            }
        }

        for (i = 0; i < 204 * 16; i++) {
            value = 163.67 / (24329.0 / (i / 16.0) + 100.0);
            value *= 0.98411;
            value *= 50000.0;
            ival = (int) value;

            tnd_table[i] = ival;
            if (ival > max_tnd) {
                max_tnd = ival;
            }
        }

        dacRange = max_sqr + max_tnd;
        dcValue = dacRange / 2;
    }
}

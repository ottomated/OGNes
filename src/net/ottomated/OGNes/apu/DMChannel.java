package net.ottomated.OGNes.apu;

import net.ottomated.OGNes.Apu;
import net.ottomated.OGNes.Cpu;

public class DMChannel {
    public enum Mode {
        NORMAL, LOOP, IRQ
    }

    private Apu apu;
    public boolean isEnabled;
    private boolean hasSample;
    public boolean irqGenerated;
    private Mode playMode;
    public int dmaFrequency;
    private int dmaCounter;
    private int deltaCounter;
    private int playStartAddress;
    private int playAddress;
    private int playLength;
    private int playLengthCounter;
    public int sample;
    private int dacLsb;
    public int shiftCounter;
    private int reg4012;
    private int reg4013;
    private int data;

    public DMChannel(Apu apu) {
        this.apu = apu;
        reset();
    }

    public void reset() {
        isEnabled = false;
        irqGenerated = false;
        playMode = Mode.NORMAL;
        dmaFrequency = 0;
        dmaCounter = 0;
        deltaCounter = 0;
        playStartAddress = 0;
        playAddress = 0;
        playLength = 0;
        playLengthCounter = 0;
        sample = 0;
        dacLsb = 0;
        shiftCounter = 0;
        reg4012 = 0;
        reg4013 = 0;
        data = 0;
    }

    public void clockDmc() {
        // Only alter DAC value if the sample buffer has data:
        if (hasSample) {
            if ((data & 1) == 0) {
                // Decrement delta:
                if (deltaCounter > 0) {
                    deltaCounter--;
                }
            } else {
                // Increment delta:
                if (deltaCounter < 63) {
                    deltaCounter++;
                }
            }

            // Update sample value:
            sample = isEnabled ? (deltaCounter << 1) + dacLsb : 0;

            // Update shift register:
            data >>= 1;
        }

        dmaCounter--;
        if (dmaCounter <= 0) {
            // No more sample bits.
            hasSample = false;
            endOfSample();
            dmaCounter = 8;
        }

        if (this.irqGenerated) {
            apu.nes.cpu.requestIrq(Cpu.Interrupt.IRQ);
        }
    }

    private void endOfSample() {
        if (playLengthCounter == 0 && playMode == Mode.LOOP) {
            // Start from beginning of sample:
            playAddress = playStartAddress;
            playLengthCounter = playLength;
        }

        if (playLengthCounter > 0) {
            // Fetch next sample:
            nextSample();

            if (playLengthCounter == 0) {
                // Last byte of sample fetched, generate IRQ:
                if (playMode == Mode.IRQ) {
                    // Generate IRQ:
                    irqGenerated = true;
                }
            }
        }
    }

    private void nextSample() {
        // Fetch byte:
        data = apu.nes.mapper.read(playAddress);
        apu.nes.cpu.cyclesToHalt += 4;

        playLengthCounter--;
        playAddress++;
        if (playAddress > 0xffff) {
            playAddress = 0x8000;
        }

        hasSample = true;
    }

    public void writeReg(int address, int value) {
        if (address == 0x4010) {
            // Play mode, DMA Frequency
            if (value >> 6 == 0) {
                playMode = Mode.NORMAL;
            } else if (((value >> 6) & 1) == 1) {
                playMode = Mode.LOOP;
            } else if (value >> 6 == 2) {
                playMode = Mode.IRQ;
            }

            if ((value & 0x80) == 0) {
                irqGenerated = false;
            }

            dmaFrequency = apu.getDmcFrequency(value & 0xf);
        } else if (address == 0x4011) {
            // Delta counter load register:
            deltaCounter = (value >> 1) & 63;
            dacLsb = value & 1;
            sample = (deltaCounter << 1) + dacLsb; // update sample value
        } else if (address ==0x4012) {
            // DMA address load register
            playStartAddress = (value << 6) | 0x0c000;
            playAddress = playStartAddress;
            reg4012 = value;
        } else if (address == 0x4013) {
            // Length of play code
            playLength = (value << 4) + 1;
            playLengthCounter = playLength;
            reg4013 = value;
        } else if (address == 0x4015) {
            // DMC/IRQ Status
            if (((value >> 4) & 1) == 0) {
                // Disable:
                playLengthCounter = 0;
            } else {
                // Restart:
                playAddress = playStartAddress;
                playLengthCounter = playLength;
            }
            irqGenerated = false;
        }
    }
    public void setEnabled(boolean value) {
        if (!isEnabled && value) {
            playLengthCounter = playLength;
        }
        isEnabled = value;
    }

    public int getIrqStatus() {
        return irqGenerated ? 1 : 0;
    }

    public int getLengthStatus() {
        return playLengthCounter == 0 || !isEnabled ? 0 : 1;
    }
}

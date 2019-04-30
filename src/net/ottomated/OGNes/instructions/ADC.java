package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ADC extends Instruction {
    ADC(Cpu cpu, Cpu.AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        switch (mode) {
            case IMMEDIATE:
                length = 2;
                break;
            case ZERO_PAGE:
                length = 3;
                break;
            case INDEXED_ABSOLUTE_X:
            case INDEXED_ABSOLUTE_Y:
            case INDEXED_ZERO_PAGE_X:
            case ABSOLUTE:
                length = 4;
                break;
            case INDIRECT_INDEXED:
                length = 5;
                break;
            case INDEXED_INDIRECT:
                length = 6;
                break;
        }
    }

    @Override
    public void cycle() {
        if (length == c) {
            done = true;
            return;
        }
    }
}

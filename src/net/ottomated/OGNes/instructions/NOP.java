package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class NOP extends ReadInstruction {

    void finalStep() {
    }

    NOP(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}
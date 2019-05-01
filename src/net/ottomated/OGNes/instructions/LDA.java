package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class LDA extends ReadInstruction {

    void finalStep() {
        cpu.a = m;

        cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(cpu.a == 0);
    }

    LDA(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}
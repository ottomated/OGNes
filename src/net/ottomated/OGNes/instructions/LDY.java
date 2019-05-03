package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class LDY extends ReadInstruction {

    void finalStep() {
        
        cpu.setNegative(((m >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(m == 0);
        cpu.y = m; // Don't overflow
    }

    LDY(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}
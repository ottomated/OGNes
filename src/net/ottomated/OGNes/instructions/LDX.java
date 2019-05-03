package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class LDX extends ReadInstruction {

    void finalStep() {
        
        cpu.setNegative(((m >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(m == 0);
        cpu.x = m; // Don't overflow
    }

    LDX(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}
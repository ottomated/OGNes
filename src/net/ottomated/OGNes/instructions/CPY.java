package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CPY extends ReadInstruction {

    void finalStep() {
        int res = cpu.y - cpu.m;
        cpu.setCarry(cpu.y >= cpu.m);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(cpu.y == cpu.m);
    }

    CPY(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}
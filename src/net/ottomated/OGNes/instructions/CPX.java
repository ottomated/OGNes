package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CPX extends ReadInstruction {

    void finalStep() {
        int res = cpu.x - cpu.m;
        cpu.setCarry(cpu.x >= cpu.m);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(cpu.x == cpu.m);
    }

    CPX(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}
package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BNE extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getZero();
    }

    BNE(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BEQ extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getZero();
    }

    BEQ(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

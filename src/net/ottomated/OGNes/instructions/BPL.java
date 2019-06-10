package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BPL extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getNegative();
    }

    BPL(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BVS extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getOverflow();
    }

    BVS(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

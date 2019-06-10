package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BCC extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getCarry();
    }

    BCC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

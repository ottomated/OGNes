package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BCS extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getCarry();
    }

    BCS(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

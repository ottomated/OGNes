package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BMI extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getNegative();
    }

    BMI(Cpu cpu, AddressingMode mode, int size, int cycles) {
        super(cpu, mode, size, cycles);
    }
}

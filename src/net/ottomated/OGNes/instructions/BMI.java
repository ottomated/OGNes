package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BMI extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getNegative();
    }

    BMI(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

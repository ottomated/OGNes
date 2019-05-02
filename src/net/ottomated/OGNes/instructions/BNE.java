package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BNE extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getZero();
    }

    BNE(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BEQ extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getZero();
    }

    BEQ(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

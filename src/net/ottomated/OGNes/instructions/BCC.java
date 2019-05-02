package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BCC extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getCarry();
    }

    BCC(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

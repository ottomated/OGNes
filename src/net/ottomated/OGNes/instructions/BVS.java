package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BVS extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getOverflow();
    }

    BVS(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

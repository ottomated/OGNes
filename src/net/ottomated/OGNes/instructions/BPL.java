package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BPL extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getNegative();
    }

    BPL(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

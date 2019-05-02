package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BVC extends BranchInstruction {

    boolean shouldBranch() {
        return !cpu.getOverflow();
    }

    BVC(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

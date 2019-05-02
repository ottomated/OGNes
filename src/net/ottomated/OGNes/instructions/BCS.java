package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BCS extends BranchInstruction {

    boolean shouldBranch() {
        return cpu.getCarry();
    }

    BCS(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

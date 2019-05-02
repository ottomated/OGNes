package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

abstract class BranchInstruction extends Instruction {

    private byte displacement; // Amount to add to PC
    private int oldPC; // used to determine page crossing

    abstract boolean shouldBranch();

    BranchInstruction(AddressingMode mode) {
        assert mode == AddressingMode.RELATIVE;
        this.length = 2;

        steps = new Step[]{
                () -> {
                    if (!shouldBranch()) {
                        c++; // skip next cycle if not branching
                        return;
                    }
                    displacement = (byte) cpu.pop(); // This will convert to a signed byte
                    oldPC = cpu.pc;
                    cpu.pc += displacement;
                },
                () -> {
                    // Emulate page crossing
                    if ((oldPC & 0xff00) != (cpu.pc & 0xff00)) {
                        oldPC = cpu.pc;
                        c--;
                    }

                },
        };

    }
}

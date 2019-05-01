package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class BCC extends Instruction {

    private byte displacement; // Amount to add to PC
    private int oldPC; // used to determine page crossing

    BCC(Cpu cpu, AddressingMode mode) {
        assert mode == AddressingMode.RELATIVE;
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        this.length = 2;

        steps = new Step[]{
                () -> {
                    if (!cpu.getCarry()) {
                        c++; // skip next cycle if not branching
                        return;
                    }
                    displacement = (byte) cpu.pop(); // This will convert to a signed byte
                    oldPC = cpu.pc;
                    cpu.pc += displacement;
                },
                () -> {
                    if ((oldPC & 0xff00) != (cpu.pc & 0xff00)) {
                        oldPC = cpu.pc;
                        c--;
                    }

                },
        };

    }
}

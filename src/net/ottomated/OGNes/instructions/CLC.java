package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CLC extends Instruction {

    CLC(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[] {
                () -> cpu.setCarry(false)
        };
    }
}


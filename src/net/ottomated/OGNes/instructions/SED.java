package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class SED extends Instruction {

    SED(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[] {
                () -> cpu.setDecimalMode(true)
        };
    }
}


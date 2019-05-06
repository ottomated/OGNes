package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BRK extends Instruction {

    BRK(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 6;

        steps = new Step[] {
                () -> {
                    
                }
        };
    }
}


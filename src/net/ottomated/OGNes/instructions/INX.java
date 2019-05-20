package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class INX extends Instruction {

    INX(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[] {
                () -> {
                  cpu.setZero(res == 0);
                  cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
                  cpu.x++;
                }
        
        };
    }
}
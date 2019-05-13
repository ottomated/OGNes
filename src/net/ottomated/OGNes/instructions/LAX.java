package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class LAX extends ReadInstruction {

    void finalStep() {
        //yo mama
    }

    LAX(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}
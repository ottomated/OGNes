package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STX extends WriteInstruction {
    @Override
    void finalStep() {
        cpu.set(loc, cpu.x);
    }

    STX(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

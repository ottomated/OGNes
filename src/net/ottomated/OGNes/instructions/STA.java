package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STA extends WriteInstruction {
    @Override
    void finalStep() {
        cpu.set(loc, cpu.a);
    }

    STA(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class STY extends WriteInstruction {
    @Override
    void finalStep() {
        cpu.set(loc, cpu.y);
    }

    STY(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

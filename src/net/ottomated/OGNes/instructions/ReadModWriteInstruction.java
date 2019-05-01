package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

abstract class ReadModWriteInstruction extends Instruction {

    int m; // Value to and with A
    int loc; // Memory location of M
    int high; // High byte of addressing
    int pointer; // Used for indexed indirect, pointer in zero-page to memory location

    abstract void finalStep();
    abstract void finalAccumulatorStep();

    ReadModWriteInstruction(AddressingMode mode) {

        switch (mode) {
            case ACCUMULATOR:
                length = 1;
                steps = new Step[]{
                        this::finalAccumulatorStep
                };
                break;
            case ZERO_PAGE:
                length = 4;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> m = cpu.peek(loc),
                        () -> {},
                        this::finalStep,
                };
                break;
            case INDEXED_ZERO_PAGE_X:
                length = 5;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            loc += cpu.x;
                            loc &= 0xff;
                        },
                        () -> m = cpu.peek(loc),
                        () -> {},
                        this::finalStep
                };
                break;
            case ABSOLUTE:
                length = 5;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> high = cpu.pop(),
                        () -> m = cpu.peek(loc + high * 256),
                        () -> {},
                        this::finalStep
                };
                break;
            case INDEXED_ABSOLUTE_X:
                length = 6;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            high = cpu.pop();
                            loc += cpu.x;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                c--;
                                loc -= 256;
                                high++;
                                return;
                            }
                            m = cpu.peek(loc + high * 256);
                        },
                        () -> {},
                        this::finalStep
                };
                break;
        }
    }
}

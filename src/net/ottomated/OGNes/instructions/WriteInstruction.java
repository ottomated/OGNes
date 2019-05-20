package net.ottomated.OGNes.instructions;

abstract class WriteInstruction extends Instruction {

    int loc; // Memory location of M
    int high; // High byte of addressing
    int pointer; // Used for indexed indirect, pointer in zero-page to memory location

    abstract void finalStep();

    WriteInstruction(AddressingMode mode) {
        switch (mode) {
            case ZERO_PAGE:
                length = 2;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        this::finalStep,
                };
                break;
            case INDEXED_ZERO_PAGE_X:
                length = 3;
                steps = new Step[]{
                        () -> loc = cpu.pop(),

                        () -> {
                            loc += cpu.x;
                            loc &= 0xff;
                        },
                        this::finalStep,
                };
                break;
            case ABSOLUTE:
                length = 3;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> high = cpu.pop(),
                        () -> {
                            loc += high * 256;
                            finalStep();
                        }
                };
                break;
            case INDEXED_ABSOLUTE_X:
                length = 4;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            high = cpu.pop();
                            loc += cpu.x;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                loc -= 256;
                                high++;
                            }
                        },
                        this::finalStep
                };
                break;
            case INDEXED_ABSOLUTE_Y:
                length = 4;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            high = cpu.pop();
                            loc += cpu.y;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                loc -= 256;
                                high++;
                            }
                        },
                        this::finalStep
                };
                break;
            case INDEXED_INDIRECT:
                length = 5;
                steps = new Step[]{
                        () -> pointer = cpu.pop(),
                        () -> {
                            pointer += cpu.x;
                            pointer &= 0xff;
                        },
                        () -> loc = cpu.peek(pointer),
                        () -> high = cpu.peek(pointer + 1),
                        () -> {
                            loc += high * 256;
                            finalStep();
                        }
                };
                break;
            case INDIRECT_INDEXED:
                length = 5;
                steps = new Step[]{
                        () -> pointer = cpu.pop(),
                        () -> loc = cpu.peek(pointer),
                        () -> {
                            high = cpu.peek(pointer + 1);
                            loc += cpu.y;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                loc -= 256;
                                high++;
                            }
                            loc += high * 256;
                        },
                        this::finalStep
                };
                break;
        }
    }
}

package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ADC extends Instruction {

    private int m; // Value to add to A + C
    private int loc; // Memory location of M
    private int high; // High byte of addressing
    private int pointer; // Used for indexed indirect, pointer in zero-page to memory location


    private void finalStep() {
        int res = cpu.a + m + (cpu.getCarry() ? 1 : 0);

        cpu.setOverflow(((cpu.a ^ m) & 0x80) == 0 && // If the sign bit isn't set but should be
                ((cpu.a ^ res & 0x80) != 0));
        cpu.setCarry(res > 255);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.a = res & 255; // Don't overflow
    }

    ADC(Cpu cpu, Cpu.AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        switch (mode) {
            case IMMEDIATE:
                length = 1;
                steps = new Step[]{
                        () -> {
                            m = cpu.pop();
                            finalStep();
                        },
                };
                break;
            case ZERO_PAGE:
                length = 2;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            m = cpu.peek(loc);
                            finalStep();
                        },
                };
                break;
            case INDEXED_ZERO_PAGE_X:
                length = 3;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            loc += cpu.x;
                            loc %= 0x100;
                        },
                        () -> {
                            m = cpu.peek(loc);
                            finalStep();
                        },
                };
                break;
            case ABSOLUTE:
                length = 3;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> high = cpu.pop(),
                        () -> {
                            m = cpu.peek(loc + high * 256);
                            finalStep();
                        }
                };
                break;
            case INDEXED_ABSOLUTE_X:
                length = 3;
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
                            finalStep();
                        }
                };
                break;
            case INDEXED_ABSOLUTE_Y:
                length = 3;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> {
                            high = cpu.pop();
                            loc += cpu.y;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                c--;
                                loc -= 256;
                                high++;
                                return;
                            }
                            m = cpu.peek(loc + high * 256);
                            finalStep();
                        }
                };
                break;
            case INDEXED_INDIRECT:
                length = 5;
                steps = new Step[]{
                        () -> pointer = cpu.pop(),
                        () -> {
                            pointer += cpu.x;
                            pointer %= 256;
                        },
                        () -> {
                            loc = cpu.peek(pointer);
                        },
                        () -> {
                            high = cpu.peek(pointer + 1);
                        },
                        () -> {
                            m = cpu.peek(loc + high * 256);
                            finalStep();
                        }
                };
                break;
            case INDIRECT_INDEXED:
                length = 4;
                steps = new Step[]{
                        () -> pointer = cpu.pop(),
                        () -> loc = cpu.peek(pointer),
                        () -> {
                            high = cpu.peek(pointer + 1);
                            loc += cpu.y;
                        },
                        () -> {
                            if (loc > 255) { // Emulate fixing address
                                c--;
                                loc -= 256;
                                high++;
                                return;
                            }
                            m = cpu.peek(loc + high * 256);
                            finalStep();
                        }
                };
                break;
        }


    }

    @Override
    public void cycle() {
        steps[c].go();
        c++;
        if (length == c)
            done = true;

    }
}

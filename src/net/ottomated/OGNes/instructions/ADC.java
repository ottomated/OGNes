package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ADC extends Instruction {

    private int m; // Value to add to A + C
    private int loc; // Memory location of M

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
                            int res = cpu.a + m + (cpu.carry ? 1 : 0);

                            cpu.overflow = ((cpu.a ^ m) & 0x80) == 0 && // If the sign bit isn't set but should be
                                    ((cpu.a ^ res & 0x80) != 0);
                            cpu.carry = res > 255;
                            cpu.negative = ((res >> 7) & 1) == 1; // If the 7th bit is 1
                            cpu.zero = res == 0;
                            cpu.a = res & 255; // Don't overflow
                        },
                };
                break;
            case ZERO_PAGE:
                length = 2;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> cpu.a = cpu.a + cpu.peek(loc) + (cpu.carry ? 1 : 0),
                };
                break;
            case INDEXED_ABSOLUTE_X:
                length = 4;
                steps = new Step[]{
                        () -> loc = cpu.pop(),
                        () -> loc += cpu.pop() * 256,
                        () -> loc += cpu.x,
                        () -> cpu.a = cpu.a + m + (cpu.carry ? 1 : 0),
                };
                break;

            case INDEXED_ABSOLUTE_Y:
            case INDEXED_ZERO_PAGE_X:
            case ABSOLUTE:
                length = 4;
                break;
            case INDIRECT_INDEXED:
                length = 5;
                break;
            case INDEXED_INDIRECT:
                length = 6;
                break;
        }


    }

    @Override
    public void cycle() {
        if (length == c) {
            done = true;
            return;
        }
        steps[c].go();
        c++;
    }
}

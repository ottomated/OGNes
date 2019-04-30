package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public abstract class Instruction {
    public int length;
    public boolean done;
    public int c;
    public AddressingMode mode;
    Cpu cpu;


    enum AddressingMode {
        ZERO_PAGE,
        INDEXED_ZERO_PAGE_X,
        INDEXED_ZERO_PAGE_Y,
        ABSOLUTE,
        INDEXED_ABSOLUTE_X,
        INDEXED_ABSOLUTE_Y,
        IMPLIED,
        ACCUMULATOR,
        IMMEDIATE,
        RELATIVE,
        INDEXED_INDIRECT,
        INDIRECT_INDEXED,
        INDIRECT
    }

    interface Step {
        void go();
    }

    Step[] steps;

    public abstract void cycle();

    public static Instruction parse(Cpu cpu) {
        int opcode = cpu.pop();
        switch (opcode) {
            case 0x69:
                return new ADC(cpu, AddressingMode.IMMEDIATE);
            case 0x65:
                return new ADC(cpu, AddressingMode.ZERO_PAGE);
            case 0x75:
                return new ADC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x6D:
                return new ADC(cpu, AddressingMode.ABSOLUTE);
            case 0x7D:
                return new ADC(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x79:
                return new ADC(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x61:
                return new ADC(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0x71:
                return new ADC(cpu, AddressingMode.INDIRECT_INDEXED);
            case 0x29:
                return new AND(cpu, AddressingMode.IMMEDIATE);
            case 0x25:
                return new AND(cpu, AddressingMode.ZERO_PAGE);
            case 0x35:
                return new AND(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x2D:
                return new AND(cpu, AddressingMode.ABSOLUTE);
            case 0x3D:
                return new AND(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x39:
                return new AND(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x21:
                return new AND(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0x31:
                return new AND(cpu, AddressingMode.INDIRECT_INDEXED);
            default:
                System.out.println("Unimplemented opcode " + Integer.toHexString(opcode));
                return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + c + "/" + length + "]";
    }
}

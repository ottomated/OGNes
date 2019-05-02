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

    public void cycle() {
        steps[c].go();
        c++;
        if (length == c)
            done = true;
    }

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
            case 0x0a:
                return new ASL(cpu, AddressingMode.ACCUMULATOR);
            case 0x06:
                return new ASL(cpu, AddressingMode.ZERO_PAGE);
            case 0x16:
                return new ASL(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x0e:
                return new ASL(cpu, AddressingMode.ABSOLUTE);
            case 0x1e:
                return new ASL(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x90:
                return new BCC(cpu, AddressingMode.RELATIVE);
            case 0xB0:
                return new BCS(cpu, AddressingMode.RELATIVE);
            case 0xF0:
                return new BEQ(cpu, AddressingMode.RELATIVE);
            case 0x30:
                return new BMI(cpu, AddressingMode.RELATIVE);
            case 0xD0:
                return new BNE(cpu, AddressingMode.RELATIVE);
            case 0x10:
                return new BPL(cpu, AddressingMode.RELATIVE);
            case 0x50:
                return new BVC(cpu, AddressingMode.RELATIVE);
            case 0x70:
                return new BVS(cpu, AddressingMode.RELATIVE);
            case 0x49:
                return new EOR(cpu, AddressingMode.IMMEDIATE);
            case 0x45:
                return new EOR(cpu, AddressingMode.ZERO_PAGE);
            case 0x55:
                return new EOR(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x4D:
                return new EOR(cpu, AddressingMode.ABSOLUTE);
            case 0x5D:
                return new EOR(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x59:
                return new EOR(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x41:
                return new EOR(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0x51:
                return new EOR(cpu, AddressingMode.INDIRECT_INDEXED);
            case 0xA9:
                return new LDA(cpu, AddressingMode.IMMEDIATE);
            case 0xA5:
                return new LDA(cpu, AddressingMode.ZERO_PAGE);
            case 0xB5:
                return new LDA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xAD:
                return new LDA(cpu, AddressingMode.ABSOLUTE);
            case 0xBD:
                return new LDA(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xB9:
                return new LDA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0xA1:
                return new LDA(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0xB1:
                return new LDA(cpu, AddressingMode.INDIRECT_INDEXED);
            case 0x09:
                return new ORA(cpu, AddressingMode.IMMEDIATE);
            case 0x05:
                return new ORA(cpu, AddressingMode.ZERO_PAGE);
            case 0x15:
                return new ORA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x0D:
                return new ORA(cpu, AddressingMode.ABSOLUTE);
            case 0x1D:
                return new ORA(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x19:
                return new ORA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x01:
                return new ORA(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0x11:
                return new ORA(cpu, AddressingMode.INDIRECT_INDEXED);
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

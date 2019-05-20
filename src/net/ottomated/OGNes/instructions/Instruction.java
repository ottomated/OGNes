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
            case 0xE9:
                return new SBC(cpu, AddressingMode.IMMEDIATE);
            case 0xE5:
                return new SBC(cpu, AddressingMode.ZERO_PAGE);
            case 0xF5:
                return new SBC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xED:
                return new SBC(cpu, AddressingMode.ABSOLUTE);
            case 0xFD:
                return new SBC(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xF9:
                return new SBC(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0xE1:
                return new SBC(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0xF1:
                return new SBC(cpu, AddressingMode.INDIRECT_INDEXED);
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
            case 0xC9:
                return new CMP(cpu, AddressingMode.IMMEDIATE);
            case 0xC5:
                return new CMP(cpu, AddressingMode.ZERO_PAGE);
            case 0xD5:
                return new CMP(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xCD:
                return new CMP(cpu, AddressingMode.ABSOLUTE);
            case 0xDD:
                return new CMP(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xD9:
                return new CMP(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0xC1:
                return new CMP(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0xD1:
                return new CMP(cpu, AddressingMode.INDIRECT_INDEXED);
            case 0xC0:
                return new CPY(cpu, AddressingMode.IMMEDIATE);
            case 0xC4:
                return new CPY(cpu, AddressingMode.ZERO_PAGE);
            case 0xCC:
                return new CPY(cpu, AddressingMode.ABSOLUTE);
            case 0xE0:
                return new CPX(cpu, AddressingMode.IMMEDIATE);
            case 0xE4:
                return new CPX(cpu, AddressingMode.ZERO_PAGE);
            case 0xEC:
                return new CPX(cpu, AddressingMode.ABSOLUTE);
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
            case 0xA2:
                return new LDX(cpu, AddressingMode.IMMEDIATE);
            case 0xA6:
                return new LDX(cpu, AddressingMode.ZERO_PAGE);
            case 0xB6:
                return new LDX(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y);
            case 0xAE:
                return new LDX(cpu, AddressingMode.ABSOLUTE);
            case 0xBE:
                return new LDX(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0xA0:
                return new LDY(cpu, AddressingMode.IMMEDIATE);
            case 0xA4:
                return new LDY(cpu, AddressingMode.ZERO_PAGE);
            case 0xB4:
                return new LDY(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xAC:
                return new LDY(cpu, AddressingMode.ABSOLUTE);
            case 0xBC:
                return new LDY(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
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
            case 0x24:
                return new BIT(cpu, AddressingMode.ZERO_PAGE);
            case 0x2C:
                return new BIT(cpu, AddressingMode.ABSOLUTE);
            case 0x00:
                return new BRK(cpu, AddressingMode.IMPLIED);
            case 0x18:
                return new CLC(cpu, AddressingMode.IMPLIED);
            case 0xD8:
                return new CLD(cpu, AddressingMode.IMPLIED);
            case 0x58:
                return new CLI(cpu, AddressingMode.IMPLIED);
            case 0xB8:
                return new CLV(cpu, AddressingMode.IMPLIED);
            case 0x2A:
                return new ROL(cpu, AddressingMode.ACCUMULATOR);
            case 0x26:
                return new ROL(cpu, AddressingMode.ZERO_PAGE);
            case 0x36:
                return new ROL(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x2E:
                return new ROL(cpu, AddressingMode.ABSOLUTE);
            case 0x3E:
                return new ROL(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x6A:
                return new ROR(cpu, AddressingMode.ACCUMULATOR);
            case 0x66:
                return new ROR(cpu, AddressingMode.ZERO_PAGE);
            case 0x76:
                return new ROR(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x6E:
                return new ROR(cpu, AddressingMode.ABSOLUTE);
            case 0x7E:
                return new ROR(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xC6:
                return new DEC(cpu, AddressingMode.ZERO_PAGE);
            case 0xD6:
                return new DEC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xCE:
                return new DEC(cpu, AddressingMode.ABSOLUTE);
            case 0xDE:
                return new DEC(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xE6:
                return new INC(cpu, AddressingMode.ZERO_PAGE);
            case 0xF6:
                return new INC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0xEE:
                return new INC(cpu, AddressingMode.ABSOLUTE);
            case 0xFE:
                return new INC(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0xE8:
                return new INX(cpu, AddressingMode.IMPLIED);
            case 0xC8:
                return new INY(cpu, AddressingMode.IMPLIED);
            case 0x38:
                return new SEC(cpu, AddressingMode.IMPLIED);
            case 0xF8:
                return new SED(cpu, AddressingMode.IMPLIED);
            case 0x78:
                return new SEI(cpu, AddressingMode.IMPLIED);
            case 0xEA:
                return new NOP(cpu, AddressingMode.IMPLIED);
            case 0xCA:
                return new DEX(cpu, AddressingMode.IMPLIED);
            case 0x88:
                return new DEY(cpu, AddressingMode.IMPLIED);
            case 0xAA:
                return new TAX(cpu, AddressingMode.IMPLIED);
            case 0xA8:
                return new TAY(cpu, AddressingMode.IMPLIED);
            case 0x85:
                return new STA(cpu, AddressingMode.ZERO_PAGE);
            case 0x95:
                return new STA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x8D:
                return new STA(cpu, AddressingMode.ABSOLUTE);
            case 0x9D:
                return new STA(cpu, AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x99:
                return new STA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x81:
                return new STA(cpu, AddressingMode.INDEXED_INDIRECT);
            case 0x91:
                return new STA(cpu, AddressingMode.INDIRECT_INDEXED);

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

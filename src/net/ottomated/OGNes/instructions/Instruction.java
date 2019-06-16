package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public abstract class Instruction {
    public int cycles;
    public int size; // in bytes
    public AddressingMode mode;
    Cpu cpu;

    public abstract int run(int addr, int cycleAdd); // returns the number of cycles taken

    public enum AddressingMode {
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

    public static Instruction parse(Cpu cpu, int opcode) {
        switch (opcode) {
            case 0x69:
                return new ADC(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x65:
                return new ADC(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x75:
                return new ADC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0x6D:
                return new ADC(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x7D:
                return new ADC(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0x79:
                return new ADC(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0x61:
                return new ADC(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x71:
                return new ADC(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0x29:
                return new AND(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x25:
                return new AND(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x35:
                return new AND(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0x2D:
                return new AND(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x3D:
                return new AND(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0x39:
                return new AND(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0x21:
                return new AND(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x31:
                return new AND(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);


            case 0x0a:
                return new ASL(cpu, AddressingMode.ACCUMULATOR, 1, 2);
            case 0x06:
                return new ASL(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x16:
                return new ASL(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x0e:
                return new ASL(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x1e:
                return new ASL(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x90:
                return new BCC(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0xB0:
                return new BCS(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0xF0:
                return new BEQ(cpu, AddressingMode.RELATIVE, 2, 2);

            case 0x24:
                return new BIT(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x2C:
                return new BIT(cpu, AddressingMode.ABSOLUTE, 3, 4);

            case 0x30:
                return new BMI(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0xD0:
                return new BNE(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0x10:
                return new BPL(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0x00:
                return new BRK(cpu, AddressingMode.IMPLIED, 1, 7);
            case 0x50:
                return new BVC(cpu, AddressingMode.RELATIVE, 2, 2);
            case 0x70:
                return new BVS(cpu, AddressingMode.RELATIVE, 2, 2);

            case 0x18:
                return new CLC(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0xD8:
                return new CLD(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0x58:
                return new CLI(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0xB8:
                return new CLV(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0xC9:
                return new CMP(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xC5:
                return new CMP(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xD5:
                return new CMP(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0xCD:
                return new CMP(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xDD:
                return new CMP(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0xD9:
                return new CMP(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0xC1:
                return new CMP(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0xD1:
                return new CMP(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0xE0:
                return new CPX(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xE4:
                return new CPX(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xEC:
                return new CPX(cpu, AddressingMode.ABSOLUTE, 3, 4);

            case 0xC0:
                return new CPY(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xC4:
                return new CPY(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xCC:
                return new CPY(cpu, AddressingMode.ABSOLUTE, 3, 4);

            case 0xC6:
                return new DEC(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0xD6:
                return new DEC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0xCE:
                return new DEC(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0xDE:
                return new DEC(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0xCA:
                return new DEX(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x88:
                return new DEY(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x49:
                return new EOR(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x45:
                return new EOR(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x55:
                return new EOR(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0x4D:
                return new EOR(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x5D:
                return new EOR(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0x59:
                return new EOR(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0x41:
                return new EOR(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x51:
                return new EOR(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0xE6:
                return new INC(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0xF6:
                return new INC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0xEE:
                return new INC(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0xFE:
                return new INC(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0xE8:
                return new INX(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0xC8:
                return new INY(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x4C:
                return new JMP(cpu, AddressingMode.ABSOLUTE, 3, 3);
            case 0x6C:
                return new JMP(cpu, AddressingMode.INDIRECT, 3, 5);

            case 0x20:
                return new JSR(cpu, AddressingMode.ABSOLUTE, 3, 6);

            case 0xA9:
                return new LDA(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xA5:
                return new LDA(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xB5:
                return new LDA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0xAD:
                return new LDA(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xBD:
                return new LDA(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0xB9:
                return new LDA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0xA1:
                return new LDA(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0xB1:
                return new LDA(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0xA2:
                return new LDX(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xA6:
                return new LDX(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xB6:
                return new LDX(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y, 2, 4);
            case 0xAE:
                return new LDX(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xBE:
                return new LDX(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);

            case 0xA0:
                return new LDY(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xA4:
                return new LDY(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xB4:
                return new LDY(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0xAC:
                return new LDY(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xBC:
                return new LDY(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);

            case 0x4A:
                return new LSR(cpu, AddressingMode.ACCUMULATOR, 1, 2);
            case 0x46:
                return new LSR(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x56:
                return new LSR(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x4e:
                return new LSR(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x5e:
                return new LSR(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x1A:
            case 0x3A:
            case 0x5A:
            case 0x7A:
            case 0xDA:
            case 0xEA:
            case 0xFA:
                return new NOP(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x09:
                return new ORA(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x05:
                return new ORA(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x15:
                return new ORA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0x0D:
                return new ORA(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x1D:
                return new ORA(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0x19:
                return new ORA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0x01:
                return new ORA(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x11:
                return new ORA(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0x48:
                return new PHA(cpu, AddressingMode.IMPLIED, 1, 3);
            case 0x08:
                return new PHP(cpu, AddressingMode.IMPLIED, 1, 3);
            case 0x68:
                return new PLA(cpu, AddressingMode.IMPLIED, 1, 4);
            case 0x28:
                return new PLP(cpu, AddressingMode.IMPLIED, 1, 4);

            case 0x2A:
                return new ROL(cpu, AddressingMode.ACCUMULATOR, 1, 2);
            case 0x26:
                return new ROL(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x36:
                return new ROL(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x2E:
                return new ROL(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x3E:
                return new ROL(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x6A:
                return new ROR(cpu, AddressingMode.ACCUMULATOR, 1, 2);
            case 0x66:
                return new ROR(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x76:
                return new ROR(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x6E:
                return new ROR(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x7E:
                return new ROR(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x40:
                return new RTI(cpu, AddressingMode.IMPLIED, 1, 6);

            case 0x60:
                return new RTS(cpu, AddressingMode.IMPLIED, 1, 6);

            case 0xE9:
                return new SBC(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xE5:
                return new SBC(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xF5:
                return new SBC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0xED:
                return new SBC(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xFD:
                return new SBC(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0xF9:
                return new SBC(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);
            case 0xE1:
                return new SBC(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0xF1:
                return new SBC(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);

            case 0x38:
                return new SEC(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0xF8:
                return new SED(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0x78:
                return new SEI(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x85:
                return new STA(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x95:
                return new STA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);
            case 0x8D:
                return new STA(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x9D:
                return new STA(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 5);
            case 0x99:
                return new STA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 5);
            case 0x81:
                return new STA(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x91:
                return new STA(cpu, AddressingMode.INDIRECT_INDEXED, 2, 6);

            case 0x86:
                return new STX(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x96:
                return new STX(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y, 2, 4);
            case 0x8e:
                return new STX(cpu, AddressingMode.ABSOLUTE, 3, 4);

            case 0x84:
                return new STY(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x94:
                return new STY(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y, 2, 4);
            case 0x8c:
                return new STY(cpu, AddressingMode.ABSOLUTE, 3, 4);

            case 0xAA:
                return new TAX(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0xA8:
                return new TAY(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0xBA:
                return new TSX(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0x8A:
                return new TXA(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0x9A:
                return new TXS(cpu, AddressingMode.IMPLIED, 1, 2);
            case 0x98:
                return new TYA(cpu, AddressingMode.IMPLIED, 1, 2);

            case 0x4B:
                return new ALR(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x0B:
            case 0x2B:
                return new ANC(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0x6B:
                return new ARR(cpu, AddressingMode.IMMEDIATE, 2, 2);
            case 0xCB:
                return new AXS(cpu, AddressingMode.IMMEDIATE, 2, 2);

            case 0xA3:
                return new LAX(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0xA7:
                return new LAX(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0xAF:
                return new LAX(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0xB3:
                return new LAX(cpu, AddressingMode.INDIRECT_INDEXED, 2, 5);
            case 0xB7:
                return new LAX(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y, 2, 4);
            case 0xBF:
                return new LAX(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 4);

            case 0x83:
                return new SAX(cpu, AddressingMode.INDEXED_INDIRECT, 2, 6);
            case 0x87:
                return new SAX(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x8F:
                return new SAX(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x97:
                return new SAX(cpu, AddressingMode.INDEXED_ZERO_PAGE_Y, 2, 4);

            case 0xE3:
                return new ISC(cpu, AddressingMode.INDEXED_INDIRECT, 2, 8);
            case 0xE7:
                return new ISC(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0xEF:
                return new ISC(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0xF3:
                return new ISC(cpu, AddressingMode.INDIRECT_INDEXED, 2, 8);
            case 0xF7:
                return new ISC(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0xFB:
                return new ISC(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 7);
            case 0xFF:
                return new ISC(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x23:
                return new RLA(cpu, AddressingMode.INDEXED_INDIRECT, 2, 8);
            case 0x27:
                return new RLA(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x2F:
                return new RLA(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x33:
                return new RLA(cpu, AddressingMode.INDIRECT_INDEXED, 2, 8);
            case 0x37:
                return new RLA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x3B:
                return new RLA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 7);
            case 0x3F:
                return new RLA(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x63:
                return new RRA(cpu, AddressingMode.INDEXED_INDIRECT, 2, 8);
            case 0x67:
                return new RRA(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x6F:
                return new RRA(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x73:
                return new RRA(cpu, AddressingMode.INDIRECT_INDEXED, 2, 8);
            case 0x77:
                return new RRA(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x7B:
                return new RRA(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 7);
            case 0x7F:
                return new RRA(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x03:
                return new SLO(cpu, AddressingMode.INDEXED_INDIRECT, 2, 8);
            case 0x07:
                return new SLO(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x0F:
                return new SLO(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x13:
                return new SLO(cpu, AddressingMode.INDIRECT_INDEXED, 2, 8);
            case 0x17:
                return new SLO(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x1B:
                return new SLO(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 7);
            case 0x1F:
                return new SLO(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x43:
                return new SRE(cpu, AddressingMode.INDEXED_INDIRECT, 2, 8);
            case 0x47:
                return new SRE(cpu, AddressingMode.ZERO_PAGE, 2, 5);
            case 0x4F:
                return new SRE(cpu, AddressingMode.ABSOLUTE, 3, 6);
            case 0x53:
                return new SRE(cpu, AddressingMode.INDIRECT_INDEXED, 2, 8);
            case 0x57:
                return new SRE(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 6);
            case 0x5B:
                return new SRE(cpu, AddressingMode.INDEXED_ABSOLUTE_Y, 3, 7);
            case 0x5F:
                return new SRE(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 7);

            case 0x80:
            case 0x82:
            case 0x89:
            case 0xC2:
            case 0xE2:
                return new SKB(cpu, AddressingMode.IMMEDIATE, 2, 2);

            case 0x0C:
                return new IGN(cpu, AddressingMode.ABSOLUTE, 3, 4);
            case 0x1C:
            case 0x3C:
            case 0x5C:
            case 0x7C:
            case 0xDC:
            case 0xFC:
                return new IGN(cpu, AddressingMode.INDEXED_ABSOLUTE_X, 3, 4);
            case 0x04:
            case 0x44:
            case 0x64:
                return new IGN(cpu, AddressingMode.ZERO_PAGE, 2, 3);
            case 0x14:
            case 0x34:
            case 0x54:
            case 0x74:
            case 0xD4:
            case 0xF4:
                return new IGN(cpu, AddressingMode.INDEXED_ZERO_PAGE_X, 2, 4);

            default:
                System.out.println("Unimplemented opcode $" + Integer.toHexString(opcode));
                return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + mode;
    }
}

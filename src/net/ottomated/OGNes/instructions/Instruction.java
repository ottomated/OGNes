package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public abstract class Instruction {
    public int length;
    public boolean done;
    public int c;
    public Cpu.AddressingMode mode;
    Cpu cpu;


     interface Step {
        void go();
    }
    Step[] steps;

    public abstract void cycle();

    public static Instruction parse(Cpu cpu) {
        int opcode = cpu.pop();
        switch(opcode) {
            case 0x69:
                return new ADC(cpu, Cpu.AddressingMode.IMMEDIATE);
            case 0x65:
                return new ADC(cpu, Cpu.AddressingMode.ZERO_PAGE);
            case 0x75:
                return new ADC(cpu, Cpu.AddressingMode.INDEXED_ZERO_PAGE_X);
            case 0x6D:
                return new ADC(cpu, Cpu.AddressingMode.ABSOLUTE);
            case 0x7D:
                return new ADC(cpu, Cpu.AddressingMode.INDEXED_ABSOLUTE_X);
            case 0x79:
                return new ADC(cpu, Cpu.AddressingMode.INDEXED_ABSOLUTE_Y);
            case 0x61:
                return new ADC(cpu, Cpu.AddressingMode.INDEXED_INDIRECT);
            case 0x71:
                return new ADC(cpu, Cpu.AddressingMode.INDIRECT_INDEXED);
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

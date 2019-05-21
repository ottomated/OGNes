package net.ottomated.OGNes;

import java.io.*;

public class Test {

    public static void main(String[] args) throws IOException {
        Cpu cpu = new Cpu();
        cpu.reset();
        File program = new File("/home/otto/Downloads/19.05.21/instr_timing/instr_timing.nes");

        cpu.loadINES(program);

        System.out.println("Success!");
    }

    private static class Expect {
        static class X {
            static void toBe(Cpu c, int x) {
                assert c.x == x : "Register X should be " + x + ", was " + c.x;
            }
        }

        static class Y {
            static void toBe(Cpu c, int y) {
                assert c.y == y : "Register Y should be " + y + ", was " + c.y;
            }
        }

        static class A {
            static void toBe(Cpu c, int a) {
                assert c.a == a : "Accumulator should be " + a + ", was " + c.a;
            }
        }

        static class PC {
            static void toBe(Cpu c, int pc) {
                assert c.pc == pc : "Program Counter should be " + pc + ", was " + c.pc;
            }
        }

        static class SP {
            static void toBe(Cpu c, int sp) {
                assert c.sp == sp : "Stack Pointer should be " + sp + ", was " + c.sp;
            }
        }
    }

}

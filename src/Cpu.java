class Cpu {

    @CpuInstruction(name="AND", description = "Performs a logical AND")
    int and(int a, int b) {
        return a & b;
    }
}

import java.lang.reflect.Method;

public class Test {
    public static void main(String[] args) {
        for (Method m : Cpu.class.getDeclaredMethods()) {
            CpuInstruction desc = m.getAnnotation(CpuInstruction.class);
            if (desc != null) {
                System.out.println(desc.name() + " " + desc.description());
            }
        }
    }
}

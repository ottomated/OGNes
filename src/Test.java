import java.lang.reflect.Method;

public class Test {

    private Cpu cpu;

    public static void main(String[] args) {
        Test t = new Test();
        t.run();
    }

    private Test() {
        cpu = new Cpu();
    }

    private void run() {

        // For each method of the cpu
        for (Method m : cpu.getClass().getDeclaredMethods()) {
            CpuInstruction desc = m.getAnnotation(CpuInstruction.class);
            // If the method is annotated
            if (desc != null) {
                try {
                    // Get the corresponding test method
                    Method testMethod = Test.class.getMethod("test" + desc.name(), Cpu.class, Method.class);

                    System.out.println("Testing CPU instruction \"" + desc.name() + "\"");
                    System.out.println("> " + desc.description());

                    // Call the test method on our cpu object
                    testMethod.invoke(this, cpu, m);
                } catch (NoSuchMethodException e) {
                    System.out.println("No test defined for instruction " + desc.name());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private <T> boolean expectResult(Method m, T expected, Object... params) {
        try {
            T actual = (T) m.invoke(cpu, params);
            return actual.equals(expected);
        } catch (Exception e) {
            return false;
        }
    }

    public void testAND(Method original) {
        System.out.println(expectResult(original, 0b111001, 0b111101, 0b111011));
    }
}

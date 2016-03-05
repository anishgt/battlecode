package team031.precomputation;

/**
 * Created by jdshen on 1/8/16.
 */
public class RubbleClearingLookups {
    public static void main(String[] args) {
        int[] clears = new int[2000];

        for (int i = 50; i < 2000; i++) {
            clears[i] = clears[i * 95 / 100 - 10] + 1;
        }

        printSwitchCase(clears);

    }

    public static void printSwitchCase(int[] clears) {
        System.out.println("switch (rubble) {");
        for (int i = 0; i < clears.length; i++) {
            System.out.println("case " +  i + ":");
            System.out.println("return " + clears[i] + ";");
        }
        System.out.println("}");
        System.out.println("return 0;");
    }
}

package lec.study.samples;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SupplierExample {

    private static String getExpensiveValue(Integer val) {
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Big Value " + String.valueOf(val);
    }

    private static String stringSupplier(int num, Supplier<String> supplier) {
        if (num >= 3) {
            return supplier.get();
        }
        else {
            return null;
        }
    }

    public static void main(String[] args) {

        final Supplier<String> helloSupplier = () -> "hello ";
        System.out.println("        Example: " + helloSupplier.get() + "world");

        long start = System.currentTimeMillis();
        getExpensiveValue(1);
        getExpensiveValue(2);
        getExpensiveValue(3);
        System.out.println(
                "   Elapsed time: " + (System.currentTimeMillis() - start) / 1000);

        long begin = System.currentTimeMillis();
        stringSupplier(1, () -> getExpensiveValue(1));
        stringSupplier(2, () -> getExpensiveValue(2));
        stringSupplier(3, () -> getExpensiveValue(3));
        System.out.println(
                " 2 Elapsed time: " + (System.currentTimeMillis() - begin) / 1000);

        // stringSupplier(1, stringSupplier(1));
        // stringSupplier(2, stringSupplier(2));
        // stringSupplier(3, stringSupplier(3));

        // Arrays.asList(1,2,3,4,5).stream().map(i -> {
        // return stringSupplier(i, longTimeJob(i));
        // }).count();
    }
}

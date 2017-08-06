package lec.study.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PredicateExample {

    static private <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        final List<T> result = new ArrayList<>();
        for (T v : list) {
            if (predicate.test(v)) {
                result.add(v);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        final List<Integer> result = filter(list, v -> v > 2);
        System.out.println("        result: " + result);
        final List<Integer> result2 = filter(result, v -> v < 7);
        System.out.println("       result2: " + result2);
        final Predicate<Integer> GE2 = n -> n > 2;
        final Predicate<Integer> LE5 = n -> n < 5;
        final List<Integer> result3 = filter(list, GE2.and(LE5));
        System.out.println("       result3: " + result3);

    }
}
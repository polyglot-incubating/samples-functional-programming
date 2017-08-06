package lec.study.samples;


import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionExample {

    public static void main(String[] args) {
        Function<String, Integer> toInteger = n -> Integer.parseInt(n);
        System.out.println("        toInteger: " +
                toInteger.apply("100")
        );

        Function<Integer, Integer> addOne = i -> i + 1;
        System.out.println("           addOne: " +
                addOne.apply(100)
        );

        Function<Integer, Integer> doubleValue = i -> i * 2;
        System.out.println("      doubleValue: " +
                doubleValue.apply(100)
        );

        Function<Integer, Integer> squareValue = i -> i * i;
        System.out.println("      squareValue: " +
                squareValue.apply(100)
        );

        Predicate<Integer> lessThan5 = v -> v <= 5;
        System.out.println("        lessThan5: " +
                lessThan5.test(5)
        );

        Predicate<Integer> greaterThan100 = v -> v >= 100;
        System.out.println("   greaterThan100: " +
                greaterThan100.test(99)
        );

        System.out.println("   doubleValue.compose(addOne).compose(addOne): " +
                doubleValue.compose(addOne).compose(addOne).apply(10)
        );

//        Function<Integer, Integer> mul3 = new Function<Integer, Integer>() {
//            @Override
//            public Integer apply(Integer integer) {
//                return integer * 3;
//            }
//        };

        Function<Integer, Function<Integer, Function<Integer, Integer>>> fnCurry = i1-> i2 -> i3 -> i1 + i2 + i3;
        System.out.println("f.apply(1).apply(2).apply(3): " +
                fnCurry.apply(1).apply(2).apply(3)
        );

        Function<Integer, Function<Integer, Integer>> plus10 = fnCurry.apply(10);
        System.out.println(
                "plus10.apply(1).apply(2): " +
                plus10.apply(1).apply(2)
        );
       
    }
}

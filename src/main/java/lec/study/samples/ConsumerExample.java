package lec.study.samples;

import java.util.function.Consumer;

public class ConsumerExample {

    public static void main(String[] args) {
        

        final Consumer<String> printLambda = v -> System.out.println(v);
        final Consumer<String> printMrefs = System.out::println;
        

        printLambda.accept("Hello Lambda");
        printMrefs.accept("Hello Method Reference");
        
        
    }
}

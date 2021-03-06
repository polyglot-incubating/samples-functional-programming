## Functor and monad examples in plain Java

원문: http://www.nurkiewicz.com/2016/06/functor-and-monad-examples-in-plain-java.html  
작성자: Tomasz Nurkiewicz  

이 글은 우리가 쓴 책, ("http://shop.oreilly.com/product/0636920042228.do" 'Reactive Programming with RxJava') 의 부록이었다. Reactive programming과 관련이 깊은 주제긴 하지만 모나드를 소개한다는 게 책과 썩 어울리지는 않았다. 그래서 나는 따로 블로그에 올리기로 했다. 프로그래밍을 다루는 블로그에서 *"반은 맞고 반은 틀릴 지 모르는 나만의 모나드 설명"*이란 것이 새로운 *"Hello World"*라는 점을 나도 잘 안다. 하지만 이 글은 펑터(functor)와 모나드(monad)를 자바 자료 구조와 라이브러리라는 각도에서 바라보고 있으며, 이는 공유할 정도의 가치는 있을거라 생각했다.

xJava는 펑터, 모노이드(monoid), 모나드라는 기본 개념 위에 설계되고 만들어졌다. Rx가 처음에 명령형(Imperative) 언어인 C#을 위해 만들어졌고 우리는 배우는 RxJava 역시 비슷한 명령형 언어 위에서 돌아가는 것이기는 하지만 이 라이브러리는 분명 함수형 프로그래밍에 뿌리를 두고 있다. 여러분은 RxJava의 API가 얼마나 컴팩트한지 알게 되면 놀랄지도 모른다. 라이브러리는 핵심 클래스 몇 개(주로 불변 타입이다)와 순수 함수들 위주로 이뤄져 있다.

함수형 프로그래밍 혹은 함수형 스타일이 인기를 얻으면서 모나드를 많이 이야기하게 됐다. (대개는 스칼라나 클로져같은 언어로 표현된다.) 먼저 모나드와 관련하여 떠도는 얘기들을 보자.

모나드는 엔도펑터(endofunctor) 범주에서의 모노이드야. 뭐가 문제지? -- James Iry

당신이 만약 모나드를 이해하고 나면, 그걸 다른 사람에게 설명할 수 있는 능력을 잃어버리게 된다. 이것이 모나드의 저주다. -- Douglas Crokford
대부분의 프로그래머들은, 특히 함수형 프로그래밍을 모르는 이들은, 모나드가 어떤 신비로운 컴퓨터 사이언스 개념이고, 너무 이론적인 내용일 뿐이라 자신들의 프로그래머 커리어에는 도움될 리가 없다고 넘겨버리곤 한다. 이런 부정적 시각은 많은 아티클과 블로그 포트스가 너무나 추상적이거나 너무 협소한 측면만 다루었기 때문이기도 하다. 하지만 모나드는 우리 주위에서 흔히 볼 수 있으며, 심지어 자바 표준 라이브러리에서도 볼 수 있다. (JDK 8에서 볼 수 있으며, 이후 버전에서는 더 많이 보게 될 것이다.) 정말 멋진 것은, 여러분이 일단 모나드가 무엇인지 한번만 이해하고 나면 갑자기 제각각 다른 목적을 가진 서로 무관한 클래스나 추상화 개념들이 익숙하게 느껴진다는 점이다.

모나드는 여러가지 개별적인 개념들을 일반화하기 때문에 추가로 새로운 형태를 익히는 것은 거의 노력이 들지 않는다. 예를 들어 Java 8의 CompletableFuture가 모나드라는 것을 파악하고 나면 어떻게 동작하는지 따로 익히지 않더라도 여러분은 이 타입이 어떻게 동작할지, 어떤 시맨틱을 가지는지 알게 된다. 이것은 RxJava에 대해서도 마찬가지다. Observable이 모나드이기 때문에 따로 보탤 설명이 없다. 여러분이 모르고 지나쳤을 뿐, 모나드 예제는 매우 많다. 여러분이 실제로 RxJava를 사용할 수 없다 할지라도 여기서 다루는 내용은 유용할 것이다.


### 펑터(Functor)
모나드를 설명하기에 앞서 더 간단한 형태의 *펑터(functor)*라는 것을 살펴보자. 펑터는 어떤 값을 캡슐화하는 타입 인자를 가지는 자료구조이다. 표면적으로만 보자면 펑터는 다음의 API를 가지는 컨테이너를 말한다.
~~~
import java.util.function.Function;

interface Functor<T> {
    <R> Functor<R> map(Function<T,R> f);
}
~~~
문법적 형태만 봐서는 펑터가 무엇인지 이해하기 힘들다. 펑터가 제공하는 유일한 연산이 map()이며, 인자로 함수 f를 받는다. 
인자 함수는 컨테이너 혹은 박스에 담긴 값을 받아서 이를 변형하고, map은 그 결과를 다시 새로운 펑터로 포장한다. 주의해서 읽어야 한다. Functor<T>는 항상 불변형의 컨테이너이다. 따라서 map은 원래의 객체를 절대 변경하지 않는다. 대신 인자 함수로 변형한 결과값을 완전히 새로운 펑터 객체에 감싸서 반환한다.(결과 타입 R은 다른 타입일 수도 있다.) 추가로 펑터는 identity 함수가 전달되었을 때, 즉 map(x -> x)가 호출되었을 때 어떤 다른 동작도 취해서는 안된다. (역주: side-effect가 없음을 말한다) 이런 패턴의 경우 항상 같은 펑터나 같은 인스턴스를 반환해야 한다.

Functor<T>를 T 타입의 인스턴스를 가지는 박스에 비유할 때가 많다. 이 박스에 담긴 T 타입의 인스턴스를 이용할 수 있는 유일한 방법은 그 값을 변형시키는 것 뿐이다. 펑터에서 값을 꺼내는 방법에 대해서는 일반적인 방법이 정의되어 있지 않다. 값이 펑터라는 컨텍스트 속에 항상 머무르는 것이다. 펑터는 어디에 유용할까? 펑터는 컬렉션, Promise, Optional 등의 타입들을 일반화하여 일관된 API를 제공한다. 여러분이 이 API와 친숙해질 수 있게 한 두가지 펑터를 소개하겠다.

~~~
interface Functor<T, F extends Functor<?,?>> {
    <R> F map(Function<T,R> f);
}

class Identity<T> implements Functor<T, Identity<?>> {
    private final T value;
  
    Identity(T value) { this.value = value; }
  
    public <R> Identity<R> map(Function<T,R> f) {
        final R result = f.apply(value);
        return new Identity<>(result);
    }
}
~~~

Identity가 컴파일되도록 타입인자에 F를 추가했다. 이 예제는 펑터 중에서도 가장 간단한 형태이며, 단지 값 하나를 가지고 있을 뿐이다. 여러분이 할 수 있는 건 map 메쏘드를 이용하여 그 값을 변형하는 것 뿐이며, 그 값을 다시 꺼낼 수도 없다. 값을 꺼내는 것은 순수한 펑터의 개념을 벗어나는 일이다. 펑터를 이용하는 유일한 방법은 타입 안전성을 유지하면서 값을 변형해 나가는 것 뿐이다.

~~~
Identity<String> idString = new Identity<>("abc");
Identity<Integer> idInt = idString.map(String::length);
~~~
함수를 합성하듯 좀더 유연하게 표현할 수도 있다.
~~~
Identity<byte[]> idBytes = new Identity<>(customer)
    .map(Customer::getAddress)
    .map(Address::street)
    .map((String s) -> s.substring(0,3))
    .map(String::toLowerCase)
    .map(String::getBytes);
~~~

이렇게 보면 펑터를 이용하여 값을 매핑해 나가는 것이 단순히 메쏘드 체이닝하는 것과 다를 바 없어 보인다.
~~~
byte[] bytes = customer
    .getAddress()
    .street()
    .substring(0,3)
    .toLowerCase()
    .getBytes();
~~~

별로 얻는 것도 없이, 심지어 값을 빼낼 수도 없는데 군더더기 같은 펑터를 신경써야 하는 이유가 뭘까? 펑터 추상화를 이용하면 여러가지 개념들을 모델링할 수 있다. 예를 들어 Java 8부터 추가된 java.util.Optional<T>는 map() 메쏘드를 가진 펑터다. 직접 구현해보자.
~~~
class FOptional<T> implements Functor<T,FOptional<?>> {

    private final T valueOrNull;

    private FOptional(T valueOrNull) {
        this.valueOrNull = valueOrNull;
    }

    public <R> FOptional<R> map(Function<T,R> f) {
        if (valueOrNull == null)
            return empty();
        else
            return of(f.apply(valueOrNull));
    }

    public static <T> FOptional<T> of(T a) {
        return new FOptional<T>(a);
    }

    public static <T> FOptional<T> empty() {
        return new FOptional<T>(null);
    }

}
~~~

이제 조금 재밌어진다. FOptional<T> 펑터는 값을 가지고 있을 수도 있고, 비어 있을 수도 있다. 타입안전성을 유지하면서 null을 인코딩하는 방법인 셈이다. FOptional 객체를 만들 수 있는 방법은 두 가지다. 값을 가지고 생성하는 방법과 empty()로 생성하는 것이다. 어떤 방법으로 만들든 Identity와 마찬가지로 일단 생성되고 나면 FOptional 객체는 변경되지 않으며 다만 그 내부의 값을 이용할 수 있을 뿐이다. FOptional은 비어있는 상태에서는 함수 f를 사용하지 않는다는 점이 다르다. 펑터가 꼭 하나의 T 값을 캡슐화하는 것은 아니라는 것을 알 수 있다. 펑터는 여러 값을 포장할 수도 있다. List 펑터처럼.
~~~
import com.google.common.collect.ImmutableList;

class FList<T> implements Functor<T, FList<?>> {

    private final ImmutableList<T> list;

    FList(Iterable<T> value) {
        this.list = ImmutableList.copyOf(value);
    }

    @Override
    public <R> FList<?> map(Function<T, R> f) {
        ArrayList<R> result = new ArrayList<R>(list.size());
        for (T t : list) {
            result.add(f.apply(t));
        }
        return new FList<>(result);
    }
}
~~~

API는 다르지 않다. T -> R 변형이 가능한 펑터일 뿐이다. 하지만 동작은 전혀 다르다. 이제 FList 안의 모든 값들을 변형할 수 있다. 만약 customers 리스트가 있고 전체에 대해 주소 중 도로명을 알고 싶다면 다음처럼 간단히 얻을 수 있다.
~~~
import static java.util.Arrays.asList;

FList<Customer> customers = new FList<>(asList(cust1, cust2));

FList<String> streets = customers
        .map(Customer::getAddress)
        .map(Address::street);
~~~

이제 customers.getAddress().street()처럼 할 수가 없다. getAddress()를 컬렉션에 대해 호출할 수는 없고, 개별 Customer에 대해 호출해야 하며 그 결과를 다시 컬렉션에 넣어야 한다. 그루비(Groovy) 언어에서는 자주 나타나는 이런 패턴을 직접 지원하기도 한다. customer*.getAddress()*.stree()처럼. "spread-dot"이라고 하는 이 연산자는 사실 map에 가면을 씌워놓은 것이다. list를 직접 순회하는 대신 Java 8의 Stream을 사용할 수 있지 않느냐고 궁금해 할 수도 있다. list.stream().map(f).collect(toList())처럼. 뭔가 떠오르지 않는가? 자바의 java.util.stream.Stream<T> 역시 펑터라고 말한다면 어떤가? 그리고 모나드이기도 하다.

이제 여러분은 펑터의 첫번째 효용성을 알게 되었을 것이다. 펑터는 여러 자료 구조를 추상화하여 내부 구현을 감추고, 일관되고 사용하기 쉬운 API를 제공한다. 펑터의 마지막 예제는 Future와 비슷한 promise 펑터다. Promise는 나중에 사용가능한 어떤 값을 "약속"한다. 아직 값이 준비되지 않았다면 그건 백그라운드 연산이 진행 중이거나 외부로부터의 이벤트를 기다리는 중이기 때문이다. 어쨌거나 나중에라도 그 값이 준비될 것이다. Promise<T>가 값을 어떻게 준비하는지 그 속사정은 모르더라도 펑터이기 때문에 다음처럼 사용할 수 있다.
~~~
Promise<Customer> customer = // ...
Promise<byte[]> bytes = customer
        .map(Customer::getAddress)
        .map(Address::street)
        .map((String s) -> s.substring(0, 3))
        .map(String::toLowerCase)
        .map(String::getBytes);
~~~
익숙한가? 그 점이 중요하다! Promise 펑터의 구현은 이 글의 범위를 벗어나고 그다지 중요하지도 않다. Java 8의 CompletableFuture를 구헌하는 것과 매우 비슷할 것이라는 것만 알면 충분하다. 그리고 방금 우리는 RxJava의 Observable을 발견한 것이나 마찬가지다. 일단 펑터부터 마무리하자. Promise<Customer>는 아직 Customer 값을 가지고 있지 않다. 단지 나중에 준비될 것이라고 약속할 뿐이다. 하지만 우리는 여전히 이 펑터에 대해 map을 적용할 수 있다. FOptional이나 FList에서 했던 것과 마찬가지로. 문법적으로나 의미적으로 완전히 똑같다. 동작은 펑터마다 다르다. customer.map(Customer::getAddress)는 Promise<Address>를 내놓는다. 이는 map이 논블로킹이라는 걸 의미한다. customer.map()은 원래의 customer Promise가 값이 준비될 때까지 기다리지 않는다. 대신 다른 타입의 새로운 Promise를 반환한다. 나중에 원래의 Promise가 값이 준비되면 새 Promise는 map()의 인자로 전달된 함수를 적용하여 그 결과를 전달한다. 이 펑터는 비동기 연산의 논블로킹 파이프라인을 가능하게 한다. 하지만 여러분은 이런 부분까지 이해하거나 익힐 필요가 없다. Promise가 펑터이기 때문에 여러분이 이미 알고 있는 문법과 법칙을 따를 것이다.

이 밖에도 훌륭한 펑터 예제가 많지만 (예를 들면 성공한 값이나 실패한 이유를 함께 나타내는 펑터) 이제는 모나드로 넘어가도 좋을 것 같다.

### 펑터에서 모나드로
이제 펑터가 무엇인지, 그리고 어떤 점에서 유용한 추상화인지 이해했을 것이다. 하지만 펑터는 기대만큼 범용적이지는 않다. 만약 변형 함수(map()의 인자)가 단순히 어떤 값을 반환하는 대신 펑터를 반환하면 어떻게 될까? 펑터 역시 값이기 때문에 아주 나쁜 일이 생기지는 않는다. 반환값이 무엇이든 펑터로 감싸는 건 그대로다. 하지만 String을 파싱하는 다음의 메쏘드를 살펴보자.

~~~
FOptional<Integer> tryParse(String s) {
    try {
        final int i = Integer.parseInt(s);
        return FOptional.of(i);
    } catch (NumberFormatException e) {
        return FOptional.empty();
    }
}
~~~

예외는 타입 시스템과 함수형 프로그래밍의 purity를 약화시키는 사이드이펙트다. 순수 함수형 언어에는 예외가 없다. 적어도 수학 시간에 예외 발생과 같은 이야기를 들어본 적은 없지 않나. 오류나 부적절한 조건은 값으로 명시된다. 예를 들어 tryParse()는 String을 받아서 단순히 int를 반환하거나 런타임 예외를 던지지 않는다. 우리는 타입시스템을 이용하여 tryParse()가 실패할 수 있음을 명확히 나타낸다. 문자열에 문제가 있다고 예외가 발생한다거나 하지 않는다. 이러한 실패 가능성은 Optional로 표현된다. 원래 Java는 checked exception이 있어서 사이드이펙트를 숨기지 않게 하는 기능이 있다. 그래서 어떤 의미로는 Java가 순수한 측면이 있다. 하지만 checked exception의 사용을 지양하자는 것이 일반적이다. 다시 tryParse()를 살펴보자. 이미 FOptional에 담겨있는 String에 대해 tryParse를 적용하는 것도 생각해 볼 수 있다.
~~~
FOptional<String> str = FOptional.of("42");
FOptional<FOptional<Integer>> num = str.map(this::tryParse);
~~~

위 코드에 이상한 점은 없다. 만약 tryParse()가 int를 반환한다면 FOptional<Integer> num이 되었을 것이다. 하지만 map() 인자 함수가 FOptional<Integer>를 반환하기 때문에 두번 감싸져서 FOptional<FOptional<Integer>>처럼 이상한 모양이 되었다. 타입을 잘 살펴보고, 왜 FOptional이 두번 감싸져 있는지 이해할 수 있어야 한다. 지저분하게 보이는 걸 떠나서 펑터가 펑터에 감싸진 상황은 합성과 체이닝을 저해한다.

~~~
FOptional<Integer> num1 = // ...
FOptional<FOptional<Integer>> num2 = // ...

FOptional<Date> date1 = num1.map(t -> new Date(t));

// 컴파일 안됨
FOptional<Date> date2 = num2.map(t -> new Date(t));
~~~

FOptional의 내용을 매핑하여 int를 Date로 바꾸려고 한다. int -> Date 함수만 있으면 Functor<Integer>를 Functor<Date>로 쉽게 바꿀 수 있다. 하지만 num2의 경우엔 문제가 복잡해졌다. num2.map()의 인자 함수가 넘겨받는 값은 이제 int가 아니라 FOptional<Integer>이며 당연히 java.util.Date 생성자 중에는 이를 처리할 수 있는 것이 없다. 펑터를 두번 감싸는 것으로 펑터가 제 기능을 못하게 되었다. 하지만 반환값이 펑터인 함수(tryParse()같은)는 너무 일반적이어서 이런 한계을 그냥 무시할 수가 없다. 한 가지 방법은 join()이라는 특별한 메쏘드를 도입하여 중첩된 펑터를 "납작하게" 만드는 것이다.

~~~
FOptional<Integer> num3 = num2.join();
~~~

이제 문제는 해결되었다. 하지만 이러한 패턴이 너무나 일반적이기 때문에 flatMap()이라는 특별한 메쏘드가 도입되었다. flatMap()은 map과 매우 비슷하지만 인자로 받는 함수가 펑터(정확히는 모나드)를 반환한다.

~~~
interface Monad<T,M extends Monad<?,?>> extends Functor<T,M> {
    M flatMap(Function<T,M> f);
}
~~~
flatMap이 합성을 쉽게 하기 위한 문법 설탕 정도라고 이야기했지만, 사실 flatMap 메쏘드(하스켈에서는 bind혹은 >>=라고 함)로 인해 복잡한 변형 과정을 순수한 함수형 스타일로 합성할 수 있게 된다. FOptional이 모나드라면 파싱하는 코드가 간결해진다.
~~~
FOptional<String> num = FOptional.of("42");
FOptional<Integer> answer = num.flatMap(this::tryParse);
~~~

모나드는 map을 구현할 필요가 없다. flatMap()으로 쉽게 구현할 수 있다. 사실 flatMap은 온갖 새로운 변형을 가능하게 만드는 핵심 연산자이다. 펑터와 마찬가지로 flatMap()만 있다고 하여 어떤 클래스를 모나드라고 부를 수는 없고, flatMap() 함수가 "모나드 법칙"이라는 것을 만족해야 한다. 모나드 법칙은 flatMap()과 관련하여 항등원을 가지며 결합법칙을 따라야 한다는 것으로 매우 직관적이다. 예를 들어 항등원을 만드는 m()이 있을 때, 어떤 x와 f에 대해서도 m(x).flatMap(f)가 f(x)와 같은 값을 가져야 한다. 여기서 모나드 이론을 너무 깊이 다루기 보다는 실용적인 측면에 집중하려 한다. 모나드는 내부 구조가 복잡할 때 더 빛을 발한다. 예를 들어 Promise 모나드는 미래의 어떤 값을 가진다. 다음 프로그램에서 타입만 살펴보고 어떻게 동작할지 예상해보라. 각각의 메쏘드들은 반환값 Promise의 값을 완료하는데 얼마간의 시간이 걸린다.
~~~
Promise<Customer> loadCustomer(int id) {
    //...
}

Promise<Basket> readBasket(Customer customer) {
    //...
}

Promise<BigDecimal> calculateDiscount(Basket basket, DayOfWeek dow) {
    //...
}
~~~

모나드 연산자를 이용하면 마치 블로킹 호출처럼 이 함수들을 합성할 수 있다.
~~~
Promise<BigDecimal> discount = 
    loadCustomer(42)
        .flatMap(this::readBasket)
        .flatMap(b -> calculateDiscount(b, DayOfWeek.FRIDAY));
~~~

흥미롭지 않은가? flatMap()은 모나드 타입을 유지해야 하므로 메쏘드 체인의 중간 결과물들은 모두 Promise들이다. 단지 타입만 맞춰주는 것이 아니다. 앞의 프로그램은 완전히 비동기적으로 동작한다. loadCustomer()는 블로킹하지 않고 Promise를 반환한다. readBasket()은 앞의 Promise가 가진, 혹은 가지게 될 값에 적용되어 새로운 Promise를 반환한다. 배경에서 한 스텝이 끝나면 다음 스텝을 시작하는 연산의 비동기 파이프라인을 만든 셈이다.

### flatMap() 더 알아보기
두 개의 모나드 값이 있고, 각각이 감싸고 있는 값들을 합쳐야 하는 상황은 매우 일반적이다. 하지만 펑터나 모나드는 내부에 감싸진 값을 직접 접근하는 방법을 제공하지 않는다. 모나드를 벗어나지 않으면서 변형을 적용할 수 있다. 두 모나드 값을 합쳐야 하는 상황을 살펴보자.

~~~
import java.time.LocalDate;
import java.time.Month;

Monad<Month> month = // ...
Monad<Integer> dayOfMonth = // ...

Monad<LocalDate> date = month.flatMap((Month m) ->
        dayOfMonth.map((int d) -> LocalDate.of(2016, m, d)));
~~~

앞의 의사 코드를 찬찬히 살펴보길 바란다. 핵심 개념에 집중하기 위해 Promise나 List 같은 특정 모나드 구현을 사용하지 않았다. LocalDate를 만들기 위하여 두 모나드에 접근하는 중첩 변형이 적용되었다. 타입을 따라가면서 flatMap과 map이 각각의 자리에서 사용된 이유를 분명히 이해해야 한다. 만약 Monad<Year>가 있다면 어떤식으로 코드를 작성해야 할 지 생각해보라. 인자를 두 개 취하는 함수(여기서 m과 d를 취하는)는 매우 일반적이어서 하스켈의 경우에는 liftM2라고 하는 도움 함수도 제공된다. 물론 이 함수는 map과 flatMap으로 구현되었다. Java 의사코드로는 아마 다음과 같이 구현할 수 있을 것이다.
~~~
Monad<R> liftM2(Monad<T1> t1, Monad<T2> t2, BiFunction<T1, T2, R> fun) {
    return t1.flatMap((T1 tv1) ->
            t2.map((T2 tv2) -> fun.apply(tv1, tv2))
    );
}
~~~

모나드마다 이 메쏘드를 구현할 필요는 없다. 단지 flatMap()만 구현되어 있으면 모든 모나드에 대해 잘 동작할 것으로 기대할 수 있다. liftM2는 다양한 모나드에 대해 사용가능하기 때문에 꽤 유용하다. 예를 들어 liftM2(list1, list2, function)는 list1과 list2의 모든 조합에 대해 function을 적용한다. FOptional의 경우에는 두 옵셔널이 모두 값을 가지고 있을 때에만 함수를 적용한다. Promise 모나드에 있어서는 비동기적으로 두 Promise가 모두 완료되었을 때 함수가 적용된다. 이는 두 개의 비동기 스텝에 대한 간단한 동기화 메커니즘을 만든 것이나 마찬가지다. (Fork-Join 알고리즘의 join()과 같은)

flatMap()으로 만들 수 있는 유용한 연산자의 또다른 예는 filter(Predicate<T>)이다. 이 함수는 모나드 내부의 값을 받아서 어떤 조건을 만족하지 않으면 전체를 취소시켜버린다. map과 비슷하지만 1대1 매핑이 아니라 1대0/1인 셈이다. filter() 역시 모든 모나드에 대해 동일한 의미를 가지지만 실제 사용하는 모나드에 따라 동작이 달라진다. 리스트의 경우에는 특정 요소들을 제거하는 것이 당연해 보인다.
~~~
FList<Customer> vips =
    customers.filter(c -> c.totalOrders > 1_000);
~~~

예를 들어 옵셔널에 있어서는 값이 특정 조건을 만족하지 않을 때 그 옵셔널을 빈 것으로 바꿀 수 있다. 원래 비어 있는 옵셔널은 상관없다.

### 모나드의 리스트를 리스트의 모나드로
flatMap()으로부터 만들 수 있는 또다른 유용한 연산자가 sequence()다. 다음 타입 시그너쳐를 보면 어떤 동작을 할 지 예측할 수 있다.
~~~
Monad<Iterable<T>> sequence(Iterable<Monad<T>> monads)
~~~
같은 타입의 모나드가 여러 개 있고 여러분은 이를 해당 타입의 리스트를 포함하는 하나의 모나드로 바꾸고 싶은 경우가 가끔 있다. 너무 추상적으로 들릴 수 있겠지만 실은 놀라울 정도로 유용하다. 여러 ID에 대해 각각 loadCustomer(id) 메쏘드를 호출하여 동시에 여러 고객 정보를 데이터베이스에서 읽어들이는 경우를 보자. loadCustomer()는 Promise<Customer>를 반환한다. 따라서 여러분은 Promise의 리스트를 가지게 된다. 하지만 여러분이 원하는 건 고객의 리스트이다. sequence() 연산자(RxJava에서는 sequence() 대신 concat()이나 merge()가 있다)는 이러한 경우에 딱 들어맞는다.

~~~
FList<Promise<Customer>> custPromises = FList
    .of(1, 2, 3)
    .map(database::loadCustomer);

Promise<FList<Customer>> customers = custPromises.sequence();

customers.map((FList<Customer> c) -> ...);
~~~

FList<Integer>에 고객 ID가 있고, 여기에 map을 적용하여 각 ID마다 database.loadCustomer(id)를 호출하였다. (FList가 펑터여서 다행이다) 그 결과는 다소 불편한 Promise들의 리스트이다. sequence()가 있어서 문제가 해결되었다. 이 경우에도 단순히 문법설탕으로만 작용하는 것이 아니다. 앞의 코드는 완전히 논블로킹으로 동작한다. 다른 모나드에 대해서도 sequence()가 의미를 가진다. 하지만 연산 방법은 제각각 다르다. 예를 들어 FList<FOptional<T>>는 FOptional<FList<T>>로 바꿀 수 있다. 그리고 여러분은 flatMap()을 이용하면 sequence()를 구현할 수 있다.

여기까지 살펴본 내용은 flatMap()이나 모나드의 유용성을 보여주는 빙산의 일각일 뿐이다. 다소 어려운 주제인 범주론(category theory)에서 유래한 것이긴 하지만 모나드는 이미 매우 유용한 추상화라는 것이 증명되었다. 심지어 Java같은 객체지향 언어에서조차. 모나드를 반환하는 함수들을 합성할 수 있다라는 점이 너무나 유용하여 모나드 방식을 따르는 클래스들이 많다.

게다가 일단 데이터를 모나드로 포장하고 나면 쉽게 꺼낼 수 없다. 이렇게 값을 꺼내는 연산은 모나드 행위에 포함되는 것이 아니며 부자연스러운 코드가 되곤 한다. Promise<T>의 Promise.get()은 T 값을 반환할 수 있지만 블로킹 호출이 된다. flatMap()으로 만들어진 다른 연산자들이 논블로킹으로 동작하는 것과 비교된다. FOptionals.get()은 FOptional이 비어있는 경우에는 실패할 수도 있다. 리스트에서 특정 값을 꺼내는 FList.get(idx)도 for 루프 대신 map()을 쓸 수 있는 경우가 많다는 것을 알고 나면 이상하게 보인다.

이 정도면 요즘 들어 모나드를 떠드는 사람이 왜 많은지 이해할 수 있을 것이다. 심지어 Java같은 객체 지향 언어에서도 모나드는 꽤나 유용한 추상화이다.


https://gist.github.com/jooyunghan/e14f426839454063d98454581b204452
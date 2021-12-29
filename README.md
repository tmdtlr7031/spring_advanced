# spring_advanced
스프링핵심원리 - 고급편

### 개발환경
    - JDK 11
    - Spring Boot 2.6.2

---

### 쓰레드로컬(ThreadLocal)
- 동시성 문제 발생을 방지하기 위함
  - 동시성 문제
    - 여러 쓰레드가 동시에 같으 인스턴스필드 또는 static 같은 공용 필드에 접근할 때 발생 (스프링 빈 처러 싱글톤 상황에서 자주 발생)
    - 지역 변수에서는 발생하지 않고 값을 조회만하면 상관없지만 어딘가에서 변경이 이뤄지기 때문에 발생한다.

  - 이용 시 주의 사항
    - 쓰레드로컬을 모두 사용하고 난 이후엔 `remove()`를 반드시 호출해야한다. -> 메모리 누수 문제 + WAS의 쓰레드풀로 인해 사용이 완료된 쓰레드가 제거되지 않아 다른 사용자에게 정보노출 가능성
    - `ThreadLocal.remove()`를 소스단에서 호출하던지, Filter나 인터셉터 등 공용으로 처리하던지 최종 사용 이후 반드시 호출해야한다.

- QnA모음집
  - https://www.inflearn.com/questions/376512
    - WAS가 2대 이상이 경우도 ThreadLocal로 커버 가능한가? -> Yes

  - https://www.inflearn.com/questions/342785
    - remove와 set(null)의 차이
      - ThreadLocal은 ThreadLocalMap을 참조하고 있기 때문에 remove하면 Map에서 지우지만, set(null)하면 key값이 있기 때문에 null인 상태로 메모리에 남아있게 된다. (-> 메모리 누수)

  - https://www.inflearn.com/questions/367019
    - 싱글톤 빈이 아니라 Bean Scope를 request로 사용하면 ThreadLocal의 문제 해결 가능? 실제로 이상 없었다. 
      - 웹 소켓의 경우 요청/응답이 하나의 쓰레드로 묶이는게 보장되지 않고, 하나의 Request에서 여러개 Thread를 돌릴 수 있기 때문에 이럴 경우 ThreadLocal이 사용되므로 Bean Scope로 완전 커버 불가능.
      - ***단, 빈 스코프를 `Request`로 하면 HTTP 요청 하나가 들어오고 나갈 때 까지 유지되며, HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리되기 때문에 문제가 없었을 것이다.***

  - https://www.inflearn.com/questions/347336
    - ConcurrentHashMap 등 동기화된 컬랙션을 쓰면 해결 가능? 
      - 동기화 된 컬렉션은 데이터를 입력할 때와 조회할 때만 동시성이 보장되므로 해당 예제에서는 더 넓은 범위의 동시성 문제이므로 불가능.
        - 아마 단순히 값을 변경하는 케이스(운영자A,운영자B가 제품A의 수량을 동시에 변경)가 아니라 사용자 별로 어플리케이션에 접근한 로그를 추적해야하는 예제라 각 쓰레드 별로 구분이 되야하기 때문에 `더 넓은 범위의 동시성문제`라고 한듯 
        - 아래 코드에서 보면, `nameStore="userA"`를 저장하는데 1초가 걸리는 데(-> 비즈니스 로직 또는 DB커넥션 문제 등이 원인이 될 수 있겠지..) 그 1초 사이에 다른 쓰레드가 `nameStore="userB"`를 실행해버리는 상황이므로 동시에 자원에 접근해 변경이 이뤄지는 상황이 아니기 때문에 동기화 된 컬렉션으로도 해결이 안 된다고 한 것으로 판단된다.
        ```java
            public String logic(String name) {
                log.info("저장 name={} -> nameStore={}", name, nameStore);
                nameStore = name;
                sleep(1000); // 저장하는데 걸리는 시간
                log.info("조회 nameStore={}", nameStore);
                return nameStore;
            }
        ```
 
 - 참고
    - http://dveamer.github.io/backend/JavaConcurrentCollections.html (쓰레드세이프 컬렉션)
    - https://codechacha.com/ko/java-atomic-integer/ (AtomicInteger 사용 방법)
    - https://jronin.tistory.com/110 (자바 동기화 처리 - volatile 와 synchronized)
    - https://stackoverflow.com/questions/10357295/using-threadlocal-vs-atomic (Using ThreadLocal vs Atomic)
      - **각 쓰레드간 공유 자원을 동기화 해야하는 경우라면 동기화 된 컬렉션이나 Atomic을 사용하지만, ThreadLocal은  각 쓰레드마다 자원을 할당하기 때문에 그럴 필요가 없고 애초에 쓰레드간 공유를 위한 목적이 아니다.**


----



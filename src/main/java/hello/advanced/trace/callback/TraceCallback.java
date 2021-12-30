package hello.advanced.trace.callback;

public interface TraceCallback<T> {
    T call(); // 호출하는 곳에서 반환타입들이 다를 수 있기 때문
}

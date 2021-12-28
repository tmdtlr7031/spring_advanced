package hello.advanced.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TraceId {
    private String id;
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0,8); // 앞 8자리만 사용
    }

    // id는 같은데 레벨만 올리기 위해
    public TraceId createNextId() {
        return new TraceId(id, level+1);
    }

    public TraceId createPreviousId() {
        return new TraceId(id, level-1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }
}

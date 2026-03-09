package actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.actor.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.core.exception.UnidentifiableActorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadLocalActorContextHolderTest {
    private static final String MDC_KEY = "actor_context";
    private final ActorContextHolder holder = new ThreadLocalActorContextHolder();

    @AfterEach
    void runClear() {
        holder.clear();
        MDC.clear();
    }

    @Test
    @DisplayName("setActor 후 getActor로 같은 actor를 조회할 수 있다")
    void setActor_후_getActor로_같은_actor를_조회할_수_있다() {
        // given
        Actor actor = new SimpleActor("user-1", Set.of("seat:read"));

        // when
        holder.setActor(actor);

        // then
        Actor actual = holder.getActor();
        assertEquals(actor, actual);
    }

    @Test
    @DisplayName("actor가 설정되지 않은 경우 getActor는 예외를 던진다")
    void actor가_설정되지_않은_경우_getActor는_예외를_던진다() {
        // when & then
        var ex = assertThrows(
                UnidentifiableActorException.class,
                holder::getActor
        );
        assertEquals("Does not held any actor", ex.getMessage());
    }

    @Test
    @DisplayName("clear 후에는 actor를 조회할 수 없다")
    void clear_후에는_actor를_조회할_수_없다() {
        // given
        holder.setActor(new SimpleActor("user-1", Set.of("seat:read")));

        // when
        holder.clear();

        // then
        assertThrows(UnidentifiableActorException.class, holder::getActor);
    }

    @Test
    @DisplayName("setActor는 MDC에 actor subject를 기록한다")
    void setActor는_MDC에_actor_subject를_기록한다() {
        // given
        Actor actor = new SimpleActor("user-1", Set.of("seat:read"));

        // when
        holder.setActor(actor);

        // then
        assertEquals("user-1", MDC.get(MDC_KEY));
    }

    @Test
    @DisplayName("Actor 정보는 ThreadLocal로 스레드 간 격리된다")
    void Actor_정보는_ThreadLocal로_스레드_간_격리된다() throws InterruptedException {
        // given
        holder.setActor(new SimpleActor("main-user", Set.of("seat:read")));

        var childThreadActor = new AtomicReference<Actor>();
        var childThreadException = new AtomicReference<Throwable>();
        var latch = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            try {
                childThreadActor.set(holder.getActor());
            } catch (Throwable t) {
                childThreadException.set(t);
            } finally {
                latch.countDown();
            }
        });

        // when
        thread.start();
        latch.await();

        // then
        assertEquals("main-user", holder.getActor().subject());
        assertNull(childThreadActor.get());
        assertNotNull(childThreadException.get());
        assertInstanceOf(UnidentifiableActorException.class, childThreadException.get());
    }

    @Test
    @DisplayName("자식 스레드에서 설정한 actor는 해당 스레드에서만 보인다")
    void 자식_스레드에서_설정한_actor는_해당_스레드에서만_보인다() throws InterruptedException {
        // given
        holder.setActor(new SimpleActor("main-user", Set.of("seat:read")));

        var childSubject = new AtomicReference<String>();
        var mainSubjectAfterChild = new AtomicReference<String>();
        var latch = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            try {
                holder.setActor(new SimpleActor("child-user", Set.of("seat:write")));
                childSubject.set(holder.getActor().subject());
            } finally {
                holder.clear();
                latch.countDown();
            }
        });

        // when
        thread.start();
        latch.await();
        mainSubjectAfterChild.set(holder.getActor().subject());

        // then
        assertEquals("child-user", childSubject.get());
        assertEquals("main-user", mainSubjectAfterChild.get());
    }
}

package org.cvcoei.sistools.common.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Unit tests to design a Reactor flow that can poll for a value until a condition is met.
 */
@SuppressWarnings("ALL")
@TestInstance(PER_CLASS)
public class ReactorPollingTests {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void testPollingWithReactor() {
        final AtomicInteger iterations = new AtomicInteger();

        // Create a repeater rule to continually check the value
        Repeat repeatRule = Repeat
            .times(10)
            .fixedBackoff(Duration.ofSeconds(2));

        Mono.defer(() -> {
            iterations.getAndIncrement();
            return Mono.just("hello -> " + iterations.intValue());
        })
        .repeatWhen(repeatRule)
        .takeUntil(result -> {
            System.out.println(result);
            return result.equals("hello -> 5");
        })
        .blockLast();
    }

}

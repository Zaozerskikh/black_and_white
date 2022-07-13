package picture_sending_service.profiler;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple profiler for the spam service. It calculates the processing time of all requests.
 */
@Log
@Service
public class ProfilerImpl implements Profiler {
    @Value("${request_count}")
    private int requestCount;

    private Date start, end;
    private final AtomicInteger executed = new AtomicInteger(0);

    @Override
    public void startProfiler() {
        start = new Date();
    }

    @Override
    public void incrementExecuted() {
        if (executed.incrementAndGet() == requestCount) {
            end = new Date();
        }
    }

    @Scheduled(initialDelayString = "${test_time}", fixedDelay = Long.MAX_VALUE)
    private void logExecutionDetails() {
        int finallyExecuted = executed.get();
        if (requestCount != finallyExecuted) {
            end = new Date();
        }

        log.info("EXECUTED: " + finallyExecuted + "/" + requestCount + " REQUESTS; EXECUTION TIME: "
                + (end.getTime() - start.getTime()) / 1000.0 + " SECONDS");
    }

}

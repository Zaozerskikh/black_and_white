package picture_processing_service.services.metrics;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Simple service that every second logs ThreadPoolExecutor metrics:
 * count of active threads and count of enqueued requests.
 */
@Log
@Service
@EnableScheduling
@EnableAsync
public class MetricsServiceImpl implements MetricsService {
    private int currentThreadCount, currentEnqueuedRequestsCount;

    @Autowired
    private ThreadPoolExecutor pictureProcessorExecutor;

    @Async
    @Scheduled(fixedRate = 1000)
    @Override
    public void log() {
        int newThredCount = pictureProcessorExecutor.getActiveCount();
        int newEnqReqCount = pictureProcessorExecutor.getQueue().size();

        if ((newThredCount != 0 || newEnqReqCount != 0) &&
                (newThredCount != currentThreadCount || newEnqReqCount != currentEnqueuedRequestsCount)) {
            log.info("ENQUEUED - " + pictureProcessorExecutor.getQueue().size()
                    + "; WORKING - " + pictureProcessorExecutor.getActiveCount());
            currentEnqueuedRequestsCount = newEnqReqCount;
            currentThreadCount = newThredCount;
        }
    }
}

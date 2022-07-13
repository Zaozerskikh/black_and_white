package picture_sending_service.services.spam_service;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import picture_sending_service.services.picture_sending_service.PictureSendingService;
import picture_sending_service.profiler.Profiler;

/**
 * Dummy service that sends a lot of requests with specified interval.
 */
@Log
@Service
public class SpamServiceImpl implements SpamService {
    @Autowired
    private PictureSendingService pictureSendingService;

    @Autowired
    private Profiler profiler;

    @Value("${request_count}")
    private int requestCount;

    @Value("${interval_between_requests}")
    private int intervalBetweenRequests;

    @Override
    @SneakyThrows
    @SuppressWarnings("all")
    public void sendSpam() {
        profiler.startProfiler();
        for (int i = 0; i < requestCount; i++) {
            log.info("SENDING REQUEST...");
            pictureSendingService.sendSingle().subscribe(x -> profiler.incrementExecuted());
            if (intervalBetweenRequests > 0) {
                Thread.sleep(intervalBetweenRequests);
            }
        }
    }
}

package picture_processing_service.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picture_processing_service.utils.PictureServerException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple ThreadPoolExecutor config for async processing user's requests.
 */
@Configuration
public class PictureProcessingThreadPoolExecutorConfig {
    @Value("${core-pool-size}")
    private int corePoolSize;

    @Value("${task-queue-capacity}")
    private int taskQueueCapacity;

    @SneakyThrows
    private void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        throw new PictureServerException("Server is busy. Try again later.");
    }

    @Bean
    RejectedExecutionHandler rejectedExecutionHandler() {
        return this::rejectedExecution;
    }

    @Bean
    @Autowired
    public ThreadPoolExecutor pictureProcessorExecutor(RejectedExecutionHandler handler) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(taskQueueCapacity),
                SimpleThreadFactory.getBuilder()
                        .naming("pic_exec")
                        .priority(Thread.MAX_PRIORITY)
                        .daemon(true)
                        .build()
        );
        executor.setRejectedExecutionHandler(handler);
        executor.prestartAllCoreThreads();
        return executor;
    }
}

package picture_processing_service.config;

import org.springframework.lang.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple thread factory for picture processing thread pool.
 */
public class SimpleThreadFactory implements ThreadFactory {
    private final AtomicInteger threadCount = new AtomicInteger(0);
    private int priority;
    private boolean daemon;
    private String name;

    private SimpleThreadFactory() { }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name + "-" + threadCount.incrementAndGet());
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        return thread;
    }

    public static Builder getBuilder() {
        return new SimpleThreadFactory().new Builder();
    }

    /**
     * ThreadFactory builder.
     */
    public class Builder {
        private Builder() {
            name = "thread";
            daemon = false;
            priority = Thread.MIN_PRIORITY;
        }

        public Builder naming(String name) {
            SimpleThreadFactory.this.name = name;
            return this;
        }

        public Builder daemon(boolean daemon) {
            SimpleThreadFactory.this.daemon = daemon;
            return this;
        }

        public Builder priority(int priority) {
            SimpleThreadFactory.this.priority = priority == 10 ? Thread.MAX_PRIORITY : priority % 10;
            return this;
        }

        public ThreadFactory build() {
            return SimpleThreadFactory.this;
        }
    }
}

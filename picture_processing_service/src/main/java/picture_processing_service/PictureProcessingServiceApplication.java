package picture_processing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableJpaRepositories
public class PictureProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureProcessingServiceApplication.class, args);
    }

}

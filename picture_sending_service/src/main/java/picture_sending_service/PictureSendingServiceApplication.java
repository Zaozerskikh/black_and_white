package picture_sending_service;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import picture_sending_service.services.spam_service.SpamService;

@Log
@SpringBootApplication
@EnableScheduling
public class PictureSendingServiceApplication {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication
                .run(PictureSendingServiceApplication.class, args)
                .getBean(SpamService.class)
                .sendSpam();
    }

}

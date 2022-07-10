package picture_sending_service.services.picture_sending_service;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Dummy picture sender.
 */
@Log
@Service
public class PictureSendingServiceImpl implements PictureSendingService {
    @Value("${request_url}")
    private String url;

    @Value("${test_image}")
    private String testImage;

    @Autowired
    private WebClient senderWebClient;

    /**
     * Sends one http request with image in request body to specified url.
     * @return b&w image as byte array, or empty byte array (in error case).
     */
    @Override
    @SneakyThrows
    public Mono<byte[]> sendSingle() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("inputImage", new FileSystemResource(Objects.requireNonNull(
                this.getClass().getResource(testImage)).getPath())
        );

        return senderWebClient
                .post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        log.info("OK");
                        return clientResponse.bodyToMono(byte[].class);
                    }
                    log.info("ERROR: code = " + clientResponse.statusCode());
                    return Mono.just(new byte[0]);
                })
                .onErrorReturn(new byte[0]);
    }
}

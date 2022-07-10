package picture_sending_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class SenderWebClientConfig {
    @Bean
    public WebClient pictureSenderWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMinutes(Integer.MAX_VALUE - 4)))
                )
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(x -> x.defaultCodecs().maxInMemorySize(Integer.MAX_VALUE))
                        .build()
                ).build();
    }
}

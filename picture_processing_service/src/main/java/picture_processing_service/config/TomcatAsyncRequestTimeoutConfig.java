package picture_processing_service.config;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config, that removes default timeout for async requests in Tomcat.
 */
@Configuration
class TomcatAsyncRequestTimeoutConfig {
    @Bean
    public TomcatConnectorCustomizer asyncTimeoutCustomize() {
        return connector -> connector.setAsyncTimeout(Integer.MAX_VALUE);
    }
}
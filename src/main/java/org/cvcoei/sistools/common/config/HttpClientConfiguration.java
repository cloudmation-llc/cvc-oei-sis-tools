package org.cvcoei.sistools.common.config;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Configures Spring beans for HTTP requests.
 */
@Configuration
public class HttpClientConfiguration {

    @Bean
    OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build();
    }

}

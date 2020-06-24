package org.cvcoei.sistools.common.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Spring beans for JSON parsing and serialization.
 */
@Configuration
public class JsonConfiguration {

    @Bean
    Gson gson() {
        return new Gson();
    }

}

package ar.edu.unvime.apiblank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Configura el RestClient usado para consumir la API externa DummyJSON. */

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient dummyJsonRestClient() {
        return RestClient.builder()
                .baseUrl("https://dummyjson.com")
                .build();
    }
}
package ru.practicum.shareit.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${server.base-url}")
    private String serverUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        var httpClient = HttpClients.createDefault();

        var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
            .rootUri(serverUrl)
            .requestFactory(() -> requestFactory)
            .build();
    }


}

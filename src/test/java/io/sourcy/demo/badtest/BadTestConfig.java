package io.sourcy.demo.badtest;

import io.sourcy.demo.RemoteClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
public class BadTestConfig {

    @Bean
    @Primary
    public RemoteClient mySlowService() {
        return mock(RemoteClient.class);
    }
}

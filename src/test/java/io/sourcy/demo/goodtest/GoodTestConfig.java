package io.sourcy.demo.goodtest;


import io.sourcy.demo.RemoteClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
@EnableAspectJAutoProxy
public class GoodTestConfig {

    @Bean
    public JmsListenerJoiner jmsListenerJoiner() {
        return new JmsListenerJoiner();
    }

    @Bean
    @Primary
    public RemoteClient testRemoteClient() {
        return mock(RemoteClient.class);
    }
}

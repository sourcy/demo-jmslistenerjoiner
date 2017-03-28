package io.sourcy.demo;

import javaslang.control.Try;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.util.SocketUtils;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

import static java.lang.String.format;

@Slf4j
@Configuration
public class Config {

    @SneakyThrows
    @Bean(name = Conf.BROKER_NAME)
    // usually you would only enable the BrokerService for Integration Testing,
    // but i left this here so that starting the app doesn't result in an immediate error :)
    // @Profile(Profiles.INTEGRATION_TEST)
    public BrokerService brokerService() {
        final BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector(Conf.activeMqUri());
        return broker;
    }

    @Bean
    @DependsOn(Conf.BROKER_NAME)
    @Profile(Profiles.INTEGRATION_TEST)
    public ConnectionFactory testConnectionFactory() {
        return new ActiveMQConnectionFactory(Conf.activeMqUri());
    }

    @Bean
    @Profile("!" + Profiles.INTEGRATION_TEST)
    public ConnectionFactory productionConnectionFactory() {
        return new ActiveMQConnectionFactory(Conf.activeMqUri());
    }

    @Bean
    @SneakyThrows
    public MessageListenerContainer jmsContainer(final MessageListener messageListener,
                                                 final ConnectionFactory connectionFactory) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(Conf.QUEUE);
        container.setMessageListener(messageListener);
        return container;
    }

    @Bean
    public MessageListener messageListener(final MyService myService) {
        return new MyMessageListener(myService);
    }

    @Bean
    public MyService myService(final RemoteClient remoteCLient) {
        return new MyServiceImpl(remoteCLient);
    }

    @Bean
    public RemoteClient remoteClient() {
        return new RemoteClientImpl();
    }

    @Bean
    public JmsTemplate jmsTemplate(final ConnectionFactory connectionFactory) {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(Conf.QUEUE);
        return jmsTemplate;
    }

    @UtilityClass
    public static final class Profiles {
        public static final String INTEGRATION_TEST = "INTEGRATION_TEST";
    }

    @UtilityClass
    private static final class Conf {
        private static final Integer ACTIVEMQ_PORT = activeMqPort();
        private static final String BROKER_NAME = "broker";
        private static final String QUEUE = "some.queue";

        private static Integer activeMqPort() {
            return Try.of(() -> System.getProperty("activemq.port"))
                    .map(Integer::parseInt)
                    .onSuccess(port -> log.info("Using ActiveMQ port from System Properties:{}", port))
                    .onFailure(e -> log.info("ActiveMQ port not found in System Properties, using random available port: {}", e.getMessage()))
                    .getOrElse(SocketUtils::findAvailableTcpPort);
        }

        private static String activeMqUri() {
            return format("tcp://localhost:%s", Conf.ACTIVEMQ_PORT);
        }
    }
}

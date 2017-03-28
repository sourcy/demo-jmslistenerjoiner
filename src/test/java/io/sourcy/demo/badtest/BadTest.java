package io.sourcy.demo.badtest;

import io.sourcy.demo.Config;
import io.sourcy.demo.MyService;
import io.sourcy.demo.RemoteClient;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(Config.Profiles.INTEGRATION_TEST)
@ContextConfiguration(classes = {Config.class, BadTestConfig.class})
@DirtiesContext
public class BadTest {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RemoteClient remoteClient;

    @Test
    @SneakyThrows
    public void testThatMySlowServiceIsCalledTheBadWay() {
        jmsTemplate.send(session -> session.createTextMessage("hello!"));
        Thread.sleep(5000L);
        verify(remoteClient, times(1)).remoteCall("hello!");
    }
}

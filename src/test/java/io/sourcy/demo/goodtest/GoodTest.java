package io.sourcy.demo.goodtest;


import io.sourcy.demo.Config;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(Config.Profiles.INTEGRATION_TEST)
@ContextConfiguration(classes = {Config.class, GoodTestConfig.class})
@DirtiesContext
public class GoodTest {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsListenerJoiner jmsListenerJoiner;
    @Autowired
    private RemoteClient remoteClient;

    @Test(timeout = 30000L)
    @SneakyThrows
    public void testThatMySlowServiceIsCalledTheGoodWay() {
        jmsTemplate.send(session -> session.createTextMessage("hello!"));
        jmsListenerJoiner.await();
        verify(remoteClient, times(1)).remoteCall("hello!");
    }
}

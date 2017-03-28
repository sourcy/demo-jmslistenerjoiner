package io.sourcy.demo;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MyMessageListener implements MessageListener {
    MyService myService;

    @Override
    @SneakyThrows
    public void onMessage(final Message message) {
        final String messageText = ((TextMessage) message).getText();
        myService.doSomethingSlow(messageText);
    }
}

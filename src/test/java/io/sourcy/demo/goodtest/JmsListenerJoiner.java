package io.sourcy.demo.goodtest;


import lombok.SneakyThrows;
import net.jcip.annotations.NotThreadSafe;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

import java.util.concurrent.CountDownLatch;

@Aspect
@NotThreadSafe
public class JmsListenerJoiner implements Ordered {
    private CountDownLatch countDown;

    public JmsListenerJoiner() {
        reinitialize(1);
    }

    @Around("!within(is(FinalType)) && @annotation(org.springframework.jms.annotation.JmsListener)")
    public void decorateAnnotatedListener(final ProceedingJoinPoint pjp) {
        doCountdown(pjp);
    }

    @Around("!within(is(FinalType)) && within(javax.jms.MessageListener+) execution(* onMessage(..)) && args(javax.jms.Message)")
    public void decorateMessageListener(final ProceedingJoinPoint pjp) {
        doCountdown(pjp);
    }

    @SneakyThrows
    private void doCountdown(final ProceedingJoinPoint pjp) {
        pjp.proceed();
        countDown.countDown();
    }

    @SneakyThrows
    public void await() {
        countDown.await();
        reinitialize(1);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void reinitialize(final int count) {
        countDown = new CountDownLatch(count);
    }
}
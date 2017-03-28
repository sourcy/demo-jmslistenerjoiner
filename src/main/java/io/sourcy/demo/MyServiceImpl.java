package io.sourcy.demo;


import javaslang.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyServiceImpl implements MyService {
    RemoteClient remoteCLient;

    @Override
    public void doSomethingSlow(final String arg) {
        Try.run(() -> Thread.sleep(2000L))
                .onSuccess(x -> log.info("Did something really slow just now!"))
                .onSuccess(x -> log.info(format("Calling Remote Service with argument: %s", arg)))
                .map(x -> remoteCLient.remoteCall(arg))
                .onSuccess(str -> log.info(String.format("Remote Service returned: %s", str)))
                .get();
    }
}

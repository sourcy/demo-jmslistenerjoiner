package io.sourcy.demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteClientImpl implements RemoteClient {
    @Override
    public String remoteCall(final String arg) {
        return arg.toUpperCase();
    }
}

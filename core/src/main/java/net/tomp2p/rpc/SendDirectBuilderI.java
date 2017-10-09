package net.tomp2p.rpc;

import io.netty.buffer.ByteBuf;
import net.tomp2p.connection.ConnectionConfiguration;

import java.security.KeyPair;

public interface SendDirectBuilderI extends ConnectionConfiguration {

    boolean isRaw();

    boolean isSign();

    boolean isStreaming();

    ByteBuf dataBuffer();

    Object object();

    KeyPair keyPair();

}

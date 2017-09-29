package net.tomp2p.sctp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.tomp2p.sctp.listener.SctpConnectThread;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.NotFoundException;
import net.tomp2p.sctp.connection.SctpDispatcher;

public class UdpClientLink implements NetworkLink {

    private static final Logger LOG = LoggerFactory.getLogger(UdpClientLink.class);
    private final SctpDispatcher dispatcher;

    /**
     * Udp socket used for transport.
     */
    private final DatagramSocket udpSocket;

    /**
     * Creates new instance of <tt>UdpConnection</tt>. The default port used will be 9899.
     */
    public UdpClientLink(final SctpDispatcher dispatcher, final InetAddress local, final SctpDataCallback cb) throws SocketException {
        this(dispatcher, local, SctpPorts.SCTP_TUNNELING_PORT, cb);
    }

    /**
     * Creates new instance of <tt>UdpConnection</tt>.
     */
    public UdpClientLink(final SctpDispatcher dispatcher, final InetAddress localAddress, final int localPort, final SctpDataCallback cb) throws SocketException {
        this.dispatcher = dispatcher;
        this.udpSocket = new DatagramSocket(localPort, localAddress);

        SctpConfig.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                SctpFacade so = null;

                while (true) {
                    byte[] buff = new byte[2048];
                    DatagramPacket p = new DatagramPacket(buff, 2048);

                    try {
                        udpSocket.receive(p);

                        so = SctpDispatcher.locate(p.getAddress().getHostAddress(), p.getPort());

						/*
						 * If so is null it means that we don't know the other Sctp endpoint yet. Thus, we need to reply their handshake with INIT ACK.
						 * */
                        if (so == null) {
                            Promise<SctpFacade, Exception, Object> promise = replyHandshake(localAddress, localPort, p.getAddress(), p.getPort(), cb);
                            promise.done(new DoneCallback<SctpFacade>() {

                                @Override
                                public void onDone(final SctpFacade so) {
                                    UdpClientLink.this.dispatcher.register(new InetSocketAddress(p.getAddress(), p.getPort()), so);
                                    so.onConnIn(p.getData(), p.getOffset(), p.getLength());
                                }

                            });
                        }
                    } catch (IOException e) {
                        LOG.error("Error while receiving packet in UDPClientLink.class!", e);
                    }
                }
            }
        });
    }

    @Override
    public void onConnOut(SctpFacade s, byte[] packet) throws IOException, NotFoundException {

    }

    private Promise<SctpFacade, Exception, Object> replyHandshake(final InetAddress localAddress, final int localPort, final InetAddress remoteAddress, final int remotePort, final SctpDataCallback cb){

        //since there is no socket yet, we need to create one first
        SctpFacade so = new SctpSocketBuilder().
                networkLink(UdpClientLink.this).
                localAddress(localAddress).
                localPort(localPort).
                sctpDataCallBack(cb).
                remoteAddress(remoteAddress).
                remotePort(remotePort).
                build();
        Deferred<SctpFacade, Exception, Object> d = new DeferredObject<>();
        SctpConfig.getThreadPoolExecutor().execute(new SctpListenThread(so, d));
        return d.promise();
    }
}

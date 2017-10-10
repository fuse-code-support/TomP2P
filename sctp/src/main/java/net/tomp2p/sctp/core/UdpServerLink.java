package net.tomp2p.sctp.core;

import javassist.NotFoundException;
import net.tomp2p.sctp.connection.SctpDispatcher;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class UdpServerLink implements NetworkLink {

    private static final Logger LOG = LoggerFactory.getLogger(UdpServerLink.class);
    private final SctpDispatcher dispatcher;

    /**
     * Udp socket used for transport.
     */
    private final DatagramSocket udpSocket;

    /**
     * Creates new instance of <tt>UdpConnection</tt>. The default port used will be 9899.
     */
    public UdpServerLink(final SctpDispatcher dispatcher, final InetAddress local, final SctpDataCallback cb) throws SocketException {
        this(dispatcher, local, SctpPorts.SCTP_TUNNELING_PORT, cb);
    }

    /**
     * Creates new instance of <tt>UdpConnection</tt>.
     */
    public UdpServerLink(final SctpDispatcher dispatcher, final InetAddress localAddress, final int localPort, final SctpDataCallback cb) throws SocketException {
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

                        InetSocketAddress remote = new InetSocketAddress(p.getAddress(), p.getPort());
                        so = SctpDispatcher.locate(p.getAddress().getHostAddress(), p.getPort());

						/*
						 * If so is null it means that we don't know the other Sctp endpoint yet. Thus, we need to reply their handshake with INIT ACK.
						 * */
                        if (so == null) {
                            Promise<SctpFacade, Exception, Object> promise = replyHandshake(localAddress, localPort, p.getAddress(), p.getPort(), cb);
                            promise.done(new DoneCallback<SctpFacade>() {

                                @Override
                                public void onDone(final SctpFacade so) {
                                	dispatcher.register(remote, so);
                                    so.onConnIn(p.getData(), p.getOffset(), p.getLength());
                                }
                            });

                            promise.fail(new FailCallback<Exception>() {
                                @Override
                                public void onFail(Exception result) {
                                	dispatcher.unregister(remote);
                                    LOG.error("Unknown error: Incoming connection attempt could not be answered.", result);
                                }
                            });
                        } else {
                        	so.onConnIn(p.getData(), p.getOffset(), p.getLength());
                        }
                    } catch (IOException e) {
                        LOG.error("Error while receiving packet in UDPClientLink.class!", e);
                    }
                }
            }
        });
    }

    @Override
    public void onConnOut(SctpFacade so, byte[] data) throws IOException, NotFoundException {
    	DatagramPacket packet = new DatagramPacket(data, data.length, so.getRemote());
    	udpSocket.send(packet);
    }

    private Promise<SctpFacade, Exception, Object> replyHandshake(final InetAddress localAddress, final int localPort, final InetAddress remoteAddress, final int remotePort, final SctpDataCallback cb){
  	
    	Deferred<SctpFacade, Exception, Object> d = new DeferredObject<>();

        //since there is no socket yet, we need to create one first
        SctpFacade so = new SctpSocketBuilder().
                networkLink(UdpServerLink.this).
                localAddress(localAddress).
                localPort(localPort).
                localSctpPort(localPort).
                sctpDataCallBack(cb).
                remoteAddress(remoteAddress).
                remotePort(remotePort).
                dispatcher(dispatcher).
                build();
        
        SctpConfig.getThreadPoolExecutor().execute(new SctpListenThread(so, d));
        return d.promise();
    }
}

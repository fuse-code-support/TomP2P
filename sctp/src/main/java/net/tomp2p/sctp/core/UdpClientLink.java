package net.tomp2p.sctp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.NotFoundException;
import net.tomp2p.sctp.connection.SctpDispatcher;

public class UdpClientLink implements NetworkLink {

	private final static Logger LOG = LoggerFactory.getLogger(UdpClientLink.class);

	/**
	 * Udp socket used for transport.
	 */
	private final DatagramSocket udpSocket;
	
	/**
	 * Creates new instance of <tt>UdpConnection</tt>. The default port used will be 9899.
	 */
	public UdpClientLink(final InetAddress local, final SctpDataCallback cb) throws SocketException {
		this(local, SctpPorts.SCTP_TUNNELING_PORT, cb);
	}
	
	/**
	 * Creates new instance of <tt>UdpConnection</tt>.
	 */
	public UdpClientLink(final InetAddress localAddress, final int localPort, final SctpDataCallback cb) throws SocketException {
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
							so = new SctpSocketBuilder().builder().;
							
							promise.done(new DoneCallback<SctpFacade>() {
								
								@Override
								public void onDone(SctpFacade result) {
									// TODO Auto-generated method stub
									
								}
							});
						} 				
							
						
						

						so.onConnIn(p.getData(), p.getOffset(), p.getLength());
						
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
}

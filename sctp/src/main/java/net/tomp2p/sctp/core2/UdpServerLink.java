package net.tomp2p.sctp.core2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.NotFoundException;
import lombok.Getter;
import net.tomp2p.sctp.core.SctpConfig;

public class UdpServerLink implements NetworkLink {

	private final static Logger logger = LoggerFactory.getLogger(UdpServerLink.class);

	/**
	 * <tt>SctpFacade</tt> instance that is used in this connection.
	 */
	@Getter
	private final SctpFacade so;

	/**
	 * Udp socket used for transport.
	 */
	private final DatagramSocket udpSocket;

	/**
	 * Destination <tt>InetSocketAddress</tt>.
	 */
	@Getter
	private final InetSocketAddress remote;
	
	/**
	 * Creates new instance of <tt>UdpConnection</tt>.
	 */
	public UdpServerLink(InetSocketAddress local, InetSocketAddress remote, SctpFacade so) throws IOException {
		this.so = so;
		this.remote = remote;
		this.udpSocket = new DatagramSocket(remote.getPort(), remote.getAddress());

		// Listening thread
		SctpConfig.getThreadPoolExecutor().execute(new Runnable() {
			public void run() {
				try {
					byte[] buff = new byte[2048];
					DatagramPacket p = new DatagramPacket(buff, 2048);
					while (true) {
						udpSocket.receive(p);
						so.onConnIn(p.getData(), p.getOffset(), p.getLength());
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		});
	}
	
	@Override
	public void onConnOut(SctpFacade so, byte[] packetData) throws IOException, NotFoundException {
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, this.remote.getAddress(), this.remote.getPort());
		udpSocket.send(packet);
	}


}

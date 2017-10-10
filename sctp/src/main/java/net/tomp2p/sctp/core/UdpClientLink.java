package net.tomp2p.sctp.core;

import javassist.NotFoundException;
import lombok.Getter;
import net.tomp2p.sctp.connection.SctpConfig;
import net.tomp2p.sctp.connection.SctpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpClientLink implements NetworkLink {

	private final static Logger LOG = LoggerFactory.getLogger(UdpClientLink.class);

	/**
	 * <tt>SctpFacade</tt> instance that is used in this connection.
	 */
	@Getter
	private final SctpAdapter so;

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
	public UdpClientLink(InetSocketAddress local, InetSocketAddress remote, SctpAdapter so) throws IOException {
		this.so = so;
		this.so.setLink(this);
		this.remote = remote;
		this.udpSocket = new DatagramSocket(local.getPort(), local.getAddress());

		// Listening thread
		SctpUtils.getThreadPoolExecutor().execute(new Runnable() {
			public void run() {
				try {
					byte[] buff = new byte[2048];
					DatagramPacket p = new DatagramPacket(buff, 2048);
					while (true) {
						udpSocket.receive(p);
						so.onConnIn(p.getData(), p.getOffset(), p.getLength());
					}
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
			}
		});
	}
	
	@Override
	public void onConnOut(SctpAdapter so, byte[] data) throws IOException, NotFoundException {
		DatagramPacket packet = new DatagramPacket(data, data.length, this.remote.getAddress(), this.remote.getPort());
		udpSocket.send(packet);
	}


}

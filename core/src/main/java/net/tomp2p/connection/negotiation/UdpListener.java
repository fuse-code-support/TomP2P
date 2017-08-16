package net.tomp2p.connection.negotiation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpListener {

	private static final Logger logger = LoggerFactory.getLogger(UdpListener.class);
	
	private final ConnectionBroker broker;
	private DatagramSocket udpSocket;

	public UdpListener(final ConnectionBroker broker) {
		this.broker = broker;
	}

	public void listen(final InetAddress localIp, final int localPort) throws SocketException {
		this.udpSocket = new DatagramSocket(localPort, localIp);

		// Listening thread
		new Thread(new Runnable() {
			public void run() {
				try {
					byte[] buff = new byte[2048];
					DatagramPacket p = new DatagramPacket(buff, 2048);
					while (true) {
						udpSocket.receive(p);
						broker.addNew(p.getAddress(), p.getPort(), p.getData(), udpSocket);
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}).start();
	}
}

/*
* Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tomp2p.sctp.core;

import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import net.tomp2p.connection.Ports;
import net.tomp2p.sctp.listener.SctpListenThread;
import net.tomp2p.utils.Pair;

import java.io.*;

/**
 * Class used in code samples to send SCTP packets through UDP sockets.
 *
 * FIXME: fix receiving loop
 *
 * @author Pawel Domas
 * @author Jonas Wagner
 * 
 * 
 *         </br>
 *
 *         <p>
 *         Changes: </br>
 *         New constructor created to be able to just listen to a UDP port
 *         without explicitly binding the remote IP address or port.
 *         </p>
 */
public class UdpLinkBroker implements NetworkLink {
	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(UdpLinkBroker.class);

	/**
	 * Udp socket used for transport.
	 */
	private final DatagramSocket udpSocket;

	/**
	 * Local UDP port.
	 */
	@Getter
	private int localPort;

	/**
	 * Local <tt>InetAddress</tt>.
	 */
	@Getter
	private InetAddress localAddress;
	
	/**
	 * Destination UDP port.
	 */
	@Getter
	private int remotePort;

	/**
	 * Destination <tt>InetAddress</tt>.
	 */
	@Getter
	private InetAddress remoteAddress;

	/**
	 * Creates new instance of <tt>UdpConnection</tt>.
	 *
	 * @param sctpSocket
	 *            SCTP socket instance used by this connection.
	 * @param localIp
	 *            local IP address.
	 * @param localPort
	 *            local UDP port.
	 * @param broker
	 * 			  stores information about known remote addresses
	 * @throws IOException
	 *             when we fail to resolve any of addresses or when opening UDP
	 *             socket.
	 */
	public UdpLinkBroker(final InetAddress localIp, final int localPort, SctpDataCallback defaultCallback) throws IOException {
		this.udpSocket = new DatagramSocket(localPort, localIp);

		this.localPort = localPort;
		this.localAddress = localIp;
	}
	
	public void listen() {
		
		// Listening thread
		new Thread(new Runnable() {
			public void run() {
				try {
					byte[] buff = new byte[2048];
					DatagramPacket p = new DatagramPacket(buff, 2048);
					while (true) {
						udpSocket.receive(p);
						
						Pair<InetAddress, Integer> remote = new Pair<InetAddress, Integer>(p.getAddress(), new Integer(p.getPort()));
						
						SctpSocket sctpSocket = Sctp.findSctpSocket(remote);
						if (sctpSocket == null) {
							//FIXME jwa these ports are unsafe!
							Ports ports = new Ports();
							final SctpSocket newSctpSocket = Sctp.createSocket(ports.udpPort());
							newSctpSocket.listen();
							new Thread(new Runnable() {

								@Override
								public void run() {
									while (true) {
										logger.error("SctpSocketAcceptThread started");
										try {
											while (!newSctpSocket.accept()) {
												Thread.sleep(100);
											}
										} catch (IOException | InterruptedException e) {
											e.printStackTrace();
										}
										logger.error("SctpSocketAcceptThread finished");
									}
								}
								
							}).start();
							newSctpSocket.setLink(UdpLinkBroker.this);
							newSctpSocket.onConnIn(p.getData(), p.getOffset(), p.getLength(), new Pair<>(p.getAddress(), p.getPort()));
							Sctp.putRemote(Sctp.getPtr(newSctpSocket), remote);
							
						} else {
							sctpSocket.onConnIn(p.getData(), p.getOffset(), p.getLength(), new Pair<>(p.getAddress(), p.getPort()));
						}
					}
				} catch (IOException | NotFoundException e) {
					logger.error(e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * {@inheritDoc}
	 * @throws NotFoundException 
	 */
	@Override
	public synchronized void onConnOut(final SctpSocket s, final byte[] packetData) throws IOException, NotFoundException {
		Pair<InetAddress, Integer> remote = Sctp.findRemote(Sctp.getPtr(s));
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, remote.element0(), remote.element1());
		udpSocket.send(packet);
	}

	
	
}

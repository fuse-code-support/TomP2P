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

import lombok.Getter;
import lombok.Setter;
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
public class UdpLink implements NetworkLink {
	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(UdpLink.class);

	/**
	 * <tt>SctpSocket</tt> instance that is used in this connection.
	 */
	@Getter
	private final SctpSocket sctpSocket;

	/**
	 * Udp socket used for transport.
	 */
	private final DatagramSocket udpSocket;

	/**
	 * Destination UDP port.
	 */
	@Getter
	@Setter
	private static int remotePort = -1;

	/**
	 * Destination <tt>InetAddress</tt>.
	 */
	@Getter
	@Setter
	private static InetAddress remoteIp = null;

	/**
	 * Creates new instance of <tt>UdpConnection</tt>.
	 *
	 * @param sctpSocket
	 *            SCTP socket instance used by this connection.
	 * @param localIp
	 *            local IP address.
	 * @param localPort
	 *            local UDP port.
	 * @throws IOException
	 *             when we fail to resolve any of addresses or when opening UDP
	 *             socket.
	 */
	public UdpLink(SctpSocket sctpSocket, String localIp, int localPort, String remoteIp, int remotePort) throws IOException {
		this.sctpSocket = sctpSocket;
		UdpLink.remoteIp = InetAddress.getByName(remoteIp);
		UdpLink.remotePort = remotePort;
		this.udpSocket = new DatagramSocket(localPort, InetAddress.getByName(localIp));

		// Listening thread
		SctpConfig.getThreadPoolExecutor().execute(new Runnable() {
			public void run() {
				try {
					byte[] buff = new byte[2048];
					DatagramPacket p = new DatagramPacket(buff, 2048);
					while (true) {
						udpSocket.receive(p);
						UdpLink.this.sctpSocket.onConnIn(p.getData(), p.getOffset(), p.getLength());
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConnOut(final SctpSocket s, final byte[] packetData) throws IOException {
		DatagramPacket packet = new DatagramPacket(packetData, packetData.length, remoteIp, remotePort);
		udpSocket.send(packet);
	}

}

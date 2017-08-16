package net.tomp2p.connection.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import net.tomp2p.utils.Pair;

public class SctpReceiver {

	@Getter
	final private SctpSocket socket;
	@Getter
	final private UdpLink link;

	public SctpReceiver(InetSocketAddress local) throws IOException {
		socket = Sctp.createSocket(local.getPort());
		link = new UdpLink(socket, local.getAddress().getHostAddress(), local.getPort());
		socket.setLink(link);
	}
	
	public SctpReceiver(InetAddress localAddress, int localPort, InetAddress remoteAddress, int remotePort) throws IOException {
		socket = Sctp.createSocket(localPort);
		link = new UdpLink(socket, localAddress.getHostAddress(), localPort, remoteAddress.getHostAddress(), remotePort);
		socket.setLink(link);
	}

	/**
	 * This starts the listening Thread, which prints out all received messages
	 * */
	public void listen() throws IOException {
		socket.listen();

		SctpDataCallback callback = new SctpDataCallback() {

			@Override
			public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
					Pair<InetAddress, Integer> remote) {
				String s = new String(data, StandardCharsets.UTF_8);

				System.out.println("got message from " + remote.element0() + ":" + remote.element1() +": \\n " + s);

				try {
					link.onConnOut(socket, s.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		SctpListenThread thread = new SctpListenThread(socket, callback);
		thread.start();

	}

}

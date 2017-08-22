package net.tomp2p.sctp;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jdeferred.Deferred;

public class SctpConnectThread extends Thread {

	final private InetSocketAddress local;
	final private InetSocketAddress remote;
	final private Deferred<SctpSocket, IOException, UdpLink> deferred;

	public SctpConnectThread(final InetSocketAddress local, final InetSocketAddress remote,
			final Deferred<SctpSocket, IOException, UdpLink> deferred) {
		this.local = local;
		this.remote = remote;
		this.deferred = deferred;
	}

	@Override
	public void run() {
		super.run();

		SctpSocket socket = Sctp.createSocket(local.getPort());

		UdpLink socketWrapper = null;
		try {
			socketWrapper = new UdpLink(socket, local.getHostString(), local.getPort(),
					remote.getHostString(), remote.getPort());
			deferred.notify(socketWrapper);
		} catch (IOException e) {
			deferred.reject(e);
		}

		if (socketWrapper == null) {
			deferred.reject(new IOException("No UdpLink created!"));
		}

		socket.setLink(socketWrapper);

		try {
			socket.connect(remote.getPort());
		} catch (IOException e) {
			deferred.reject(e);
		}
		

		deferred.resolve(socket);
	}
}

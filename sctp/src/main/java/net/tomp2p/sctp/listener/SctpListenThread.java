package net.tomp2p.sctp.listener;

import java.io.IOException;

import org.jdeferred.Deferred;

import net.tomp2p.sctp.core.NetworkLink;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;

public class SctpListenThread extends Thread {

	final private SctpSocket socket;
	final private Deferred<SctpSocket, Exception, NetworkLink> d;

	public SctpListenThread(final SctpSocket socket, SctpDataCallback callback,
			Deferred<SctpSocket, Exception, NetworkLink> d) {
		this.socket = socket;
		this.socket.setDataCallback(callback);
		this.d = d;
	}

	@Override
	public void run() {
		super.run();

		int counter = 0;
		// while (true) {
		try {
			while (!socket.accept()) {
				Thread.sleep(100);
				if (counter == 2) {
					d.resolve(socket);
				}
				counter++;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		// }
	}

}

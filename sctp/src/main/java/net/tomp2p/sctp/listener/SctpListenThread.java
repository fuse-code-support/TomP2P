package net.tomp2p.sctp.listener;

import java.io.IOException;

import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;

public class SctpListenThread extends Thread {

	final private SctpSocket socket;

	public SctpListenThread(final SctpSocket socket, SctpDataCallback callback) {
		this.socket = socket;
		this.socket.setDataCallback(callback);
	}

	@Override
	public void run() {
		super.run();

		while (true) {
			try {
				while (!socket.accept()) {
					Thread.sleep(100);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

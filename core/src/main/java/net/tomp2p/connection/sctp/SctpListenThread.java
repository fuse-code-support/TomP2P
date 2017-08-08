package net.tomp2p.connection.sctp;

import java.io.IOException;

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

package net.tomp2p.sctp.core;

import org.jdeferred.Deferred;

public class SctpListenThread extends Thread {

	final private SctpFacade so;
	final private Deferred<SctpFacade, Exception, NetworkLink> d;

	public SctpListenThread(final SctpFacade so, SctpDataCallback callback,
			Deferred<SctpFacade, Exception, NetworkLink> d) {
		this.so = so;
		this.so.setDataCallback(callback);
		this.d = d;
	}

	@Override
	public void run() {
		super.run();

		boolean visited = false;
		try {
			while (!so.accept()) {
				Thread.sleep(100);
				if (!visited) {
					d.resolve(so); //we should fire resolved only once
					visited = true;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

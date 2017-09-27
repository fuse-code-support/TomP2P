package net.tomp2p.sctp.core;

import org.jdeferred.Deferred;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SctpListenThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SctpListenThread.class);
	
	final private SctpFacade so;
	final private Deferred<SctpFacade, Exception, Object> d;

	public SctpListenThread(final SctpFacade so, SctpDataCallback callback,
			Deferred<SctpFacade, Exception, Object> d) {
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
			LOG.error(e.getMessage());
			d.reject(e);
		}
	}

}

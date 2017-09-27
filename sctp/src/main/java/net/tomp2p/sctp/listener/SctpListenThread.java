package net.tomp2p.sctp.listener;

import java.io.IOException;

import org.jdeferred.Deferred;

import net.tomp2p.sctp.core.NetworkLink;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core2.SctpFacade;

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
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}

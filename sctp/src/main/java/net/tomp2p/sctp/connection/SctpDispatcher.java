package net.tomp2p.sctp.connection;

import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.sctp.core2.SctpFacade;
import net.tomp2p.sctp.core2.SctpSocket;
import net.tomp2p.sctp.core2.SctpSocketAdapter;

public class SctpDispatcher implements Dispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

	private static final ConcurrentHashMap<InetSocketAddress, SctpFacade> socketMap = new ConcurrentHashMap<>();

	@Override
	public synchronized void register(final InetSocketAddress remote, final SctpFacade so) {
		if (remote != null && so != null) {
			socketMap.put(remote, so);
		} else {
			LOG.error("Invalid input, socket could not be registered! Either remote or so was null!");
		}
	}

	/**
	 * This method removes a socket from the {@link Dispatcher} and shuts down the
	 * usrsctp counterpart. Make sure this {@link SctpSocketAdapter} instance is not
	 * used anywhere else!
	 */
	@Override
	public synchronized void unregister(SctpFacade so) {
		if (so == null) {
			LOG.error("Invalid input, null can't be removed!");
			return;
		} else if (!socketMap.contains(so)) {
			LOG.error("Invalid input, a socket, which is not registered, cannot be removed!");
			return;
		} else {
			socketMap.remove(so);
		}
	}

	public synchronized static SctpFacade locate(final String remoteAddress, final int remotePort) {
		for (Map.Entry<InetSocketAddress, SctpFacade> element : socketMap.entrySet()) {
			final int port = element.getKey().getPort();
			final String address = element.getKey().getHostName();
			
			if (port == remotePort && address.equals(remoteAddress)) {
				return socketMap.get(element.getKey());
			}
		}
		
		LOG.info("No socketMap entry found for IP:" + remoteAddress + " and port: " + remotePort);
		return null;
	}

	@Override
	public void establishChannel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getChannel() {
		// TODO Auto-generated method stub

	}

	public synchronized static SctpFacade locate(final SctpSocket sctpSocket) {
		SctpFacade facade = socketMap.values().stream().filter(so -> so.containsSctpSocket(sctpSocket)).findFirst()
				.get();

		if (facade == null) {
			LOG.error("Could not retrieve SctpSocket from SctpDispatcher!");
			return null;
		} else {
			return facade;
		}
	}

}

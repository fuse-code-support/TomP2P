package net.tomp2p.sctp.connection;

import net.tomp2p.sctp.core.SctpFacade;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core.SctpSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//TODO jwa remove all static modifiers
public class SctpDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(SctpDispatcher.class);

	private static final ConcurrentHashMap<InetSocketAddress, SctpFacade> socketMap = new ConcurrentHashMap<>();

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
	
	/**
	 * This method removes a socket from the {@link Dispatcher} and shuts down the
	 * usrsctp counterpart. Make sure this {@link SctpFacade} instance is not
	 * used anywhere else!
	 */
	public synchronized void unregister(InetSocketAddress remote) {
		if (remote == null) {
			LOG.error("Invalid input, null can't be removed!");
			return;
		} else if (!socketMap.containsKey(remote)) {
			LOG.error("Invalid input, a socket, which is not registered, cannot be removed!");
			return;
		} else {
			socketMap.remove(remote);
		}
	}

	public synchronized static SctpFacade locate(final String remoteAddress, final int remotePort) {
		for (Map.Entry<InetSocketAddress, SctpFacade> element : socketMap.entrySet()) {
			int port = element.getKey().getPort();
			String address = element.getKey().getHostName();
			
			if (address.equals("localhost")) {
				address = "127.0.0.1"; //FIXME jwa quickfix
			}
			
			if (port == remotePort && address.equals(remoteAddress)) {
				return socketMap.get(element.getKey());
			}
		}
		
		LOG.info("No socketMap entry found for IP:" + remoteAddress + " and port: " + remotePort);
		return null;
	}

	public synchronized static SctpFacade locate(final SctpSocket sctpSocket) {
		if (socketMap.isEmpty()) {
			return null;
		}
		
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

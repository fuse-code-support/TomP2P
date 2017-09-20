package net.tomp2p.sctp.connection;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.sctp.core2.SctpFacade;
import net.tomp2p.sctp.core2.SctpSocketFacadeImpl;

public class SctpDispatcher implements Dispatcher{
	
	private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

	private final ConcurrentHashMap<InetSocketAddress, SctpFacade> socketMap = new ConcurrentHashMap<>();
	
	@Override
	public synchronized void registerSocket(final InetSocketAddress remote, final SctpFacade so) {
		if (remote != null && so != null) {
			socketMap.put(remote, so);
		} else {
			LOG.error("Invalid input, socket could not be registered! Either remote or so was null!");
		}
	}

	/**
	 * This method removes a socket from the {@link Dispatcher} and shuts down the usrsctp counterpart.
	 * Make sure this {@link SctpSocketFacadeImpl} instance is not used anywhere else!
	 * */
	@Override
	public synchronized void unregisterSocket(SctpFacade so) {
		//TODO jwa handle shutdown
		
	}

	@Override
	public void locateSocket(InetSocketAddress remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void establishChannel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getChannel() {
		// TODO Auto-generated method stub
		
	}

}

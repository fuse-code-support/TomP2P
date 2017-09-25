package net.tomp2p.sctp.core2;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jdeferred.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SctpSocketAdapter implements SctpFacade{

	private static final Logger LOG = LoggerFactory.getLogger(SctpSocketAdapter.class);
	
	private final SctpSocket so;
	
	//TODO jwa implement a automatic port assignement
	
	public SctpSocketAdapter(final int localPort, final NetworkLink link) {
		this.so = Sctp.createSocket(localPort);
		this.so.setLink(link); //forwards all onConnOut to the corresponding link
	}

	@Override
	public Promise<SctpFacade, Exception, Object> connect(int localPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int send(InetSocketAddress remote) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
	}

	@Override
	public int close() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void listen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsSctpSocket(SctpSocket so) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This method is an indirection for SctpSocket, which needs to be unaccessible for a third party user.
	 */
	@Override
	public void onConnIn(byte[] data, int offset, int length) {
		try {
			this.so.onConnIn(data, offset, length);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
	
}

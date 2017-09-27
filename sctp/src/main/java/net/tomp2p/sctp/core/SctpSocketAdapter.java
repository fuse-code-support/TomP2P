package net.tomp2p.sctp.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import lombok.Setter;
import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import net.tomp2p.sctp.core.SctpDataCallback;

public class SctpSocketAdapter implements SctpFacade{

	private static final Logger LOG = LoggerFactory.getLogger(SctpSocketAdapter.class);
	
	private final SctpSocket so;
	@Getter
	private final InetSocketAddress local;
	@Getter
	private final SctpDataCallback cb;
	@Getter
	private final NetworkLink link;
	@Getter
	@Setter
	private InetSocketAddress remote;
	
	public SctpSocketAdapter(final InetSocketAddress local, final NetworkLink link, final SctpDataCallback cb) {
		this.so = Sctp.createSocket(local.getPort());
		this.so.setLink(link); //forwards all onConnOut to the corresponding link
		this.local = local;
		this.remote = remote;
		this.cb = cb;
		this.link = link;
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
		
		//TODO jwa remove port assignement from SctpPorts
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

	@Override
	public boolean accept() {
		// TODO Auto-generated method stub
		return false;
	}

}

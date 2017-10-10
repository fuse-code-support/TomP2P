package net.tomp2p.sctp.core;

import lombok.Getter;
import lombok.Setter;
import net.tomp2p.sctp.connection.SctpDispatcher;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SctpSocketAdapter implements SctpFacade{

	private static final Logger LOG = LoggerFactory.getLogger(SctpSocketAdapter.class);
	
	private final SctpSocket so;
	@Getter
	private final InetSocketAddress local;
	@Getter
	private final SctpDataCallback cb;
	@Getter
	private NetworkLink link;
	@Setter
	private InetSocketAddress remote;
	@Getter
	private SctpDispatcher dispatcher;
	
	public SctpSocketAdapter(final InetSocketAddress local, final NetworkLink link, final SctpDataCallback cb, SctpDispatcher dispatcher) {
		this.so = Sctp.createSocket(local.getPort());
		this.so.setLink(link); //forwards all onConnOut to the corresponding link
		this.local = local;
		this.cb = cb;
		this.dispatcher = dispatcher;
		this.link = link;
	}

public SctpSocketAdapter(InetSocketAddress local, InetSocketAddress remote, NetworkLink link,
			SctpDataCallback cb, SctpDispatcher dispatcher) {
		this.so = Sctp.createSocket(local.getPort());
		this.so.setLink(link); //forwards all onConnOut to the corresponding link
		this.local = local;
		this.remote = remote;
		this.cb = cb;
		this.dispatcher = dispatcher;
		this.link = link;
	}

	@Override
	public Promise<SctpFacade, Exception, Object> connect(final InetSocketAddress remote) {
		Deferred<SctpFacade, Exception, Object> d = new DeferredObject<>();
		
		class SctpConnectThread extends Thread {
			@Override
			public void run() {
				super.run();
				try {
					so.connectNative(remote.getPort());
					dispatcher.register(remote, SctpSocketAdapter.this);
					d.resolve(SctpSocketAdapter.this);
				} catch (IOException e) {
					LOG.error("Could not connect via SCTP! Cause: " + e.getMessage(), e);
					dispatcher.unregister(remote);
					d.reject(e);
				}
			}
		}
		
		SctpConfig.getThreadPoolExecutor().execute(new SctpConnectThread());
		
		return d.promise();
	}

	@Override
	public int send(byte[] data, boolean ordered, int sid, int ppid) {
		try {
			return so.sendNative(data, 0, data.length, ordered, sid, ppid);
		} catch (IOException e) {
			LOG.error("Could not send! Cause: " + e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int send(byte[] data, int offset, int len, boolean ordered, int sid, int ppid) {
		
		try {
			return so.sendNative(data, offset, len, ordered, sid, ppid);
		} catch (IOException e) {
			LOG.error("Could not send! Cause: " + e.getMessage(), e);
			return -1;
		}
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
		try {
			this.so.listenNative();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean containsSctpSocket(SctpSocket so) {
		return this.so.equals(so);
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
		try {
			return so.acceptNative();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false; //this signals a accept failure
		}
	}

	@Override
	public void setSctpDataCallback(final SctpDataCallback cb) {
		so.setDataCallbackNative(cb);
	}

	@Override
	public void setLink(NetworkLink link) {
		so.setLink(link);
		this.link = link;
	}

	@Override
	public InetSocketAddress getRemote() {
		return this.remote;
	}
}

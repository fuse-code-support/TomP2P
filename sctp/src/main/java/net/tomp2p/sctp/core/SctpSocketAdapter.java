package net.tomp2p.sctp.core;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import net.tomp2p.sctp.connection.SctpUtils;

public class SctpSocketAdapter implements SctpAdapter{

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
	private SctpMapper mapper;
	
	public SctpSocketAdapter(final InetSocketAddress local, int localSctpPort, final NetworkLink link, final SctpDataCallback cb, SctpMapper mapper) {
		this(local, localSctpPort, null, link, cb, mapper);
	}

public SctpSocketAdapter(InetSocketAddress local, int localSctpPort, InetSocketAddress remote, NetworkLink link,
			SctpDataCallback cb, SctpMapper mapper) {
		this.so = Sctp.createSocket(localSctpPort);
		this.so.setLink(link); //forwards all onConnOut to the corresponding link
		this.link = link;
		this.local = local;
		this.remote = remote;
		this.so.setDataCallbackNative(cb);
		this.cb = cb;
		this.mapper = mapper;
	}

	@Override
	public Promise<SctpAdapter, Exception, Object> connect(final InetSocketAddress remote) {
		Deferred<SctpAdapter, Exception, Object> d = new DeferredObject<>();
		
		class SctpConnectThread extends Thread {
			@Override
			public void run() {
				super.run();
				try {
					so.connectNative(remote.getPort());
					mapper.register(remote, SctpSocketAdapter.this);
					d.resolve(SctpSocketAdapter.this);
				} catch (IOException e) {
					LOG.error("Could not connect via SCTP! Cause: " + e.getMessage(), e);
					mapper.unregister(remote);
					d.reject(e);
				}
			}
		}
		
		SctpUtils.getThreadPoolExecutor().execute(new SctpConnectThread());
		
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
	public int close() {
		so.closeNative();
		mapper.unregister(this);
		SctpPorts.getInstance().removePort(this);
		link.close();
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

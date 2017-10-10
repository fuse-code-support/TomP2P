package net.tomp2p.sctp.connection;

import java.net.InetSocketAddress;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import net.tomp2p.sctp.core.SctpChannelFacade;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.UdpClientLink;

@Builder
public class SctpChannel {
	
	private static final Logger LOG = LoggerFactory.getLogger(SctpChannel.class);
	
	private SctpConfig config;
	private InetSocketAddress local;
	private InetSocketAddress remote;
	private SctpDataCallback cb;
	
	public Promise<SctpChannelFacade, Exception, UdpClientLink> connect() {
		Deferred<SctpChannelFacade, Exception, UdpClientLink> d = new DeferredObject<>();
		
		if (remote == null) {
			LOG.error("Remote InetSocketAddress was null. We can't connect to null!");
			d.reject(new NullPointerException("Remote InetSocketAddress was null. We can't connect to null!"));
		}
		
		if (local == null) {
			LOG.error("Local InetSocketAddress was null. We can't connect to null!");
			d.reject(new NullPointerException("Local InetSocketAddress was null. We can't connect to null!"));
		}
		
		if (config == null) {
			config = new SctpDefaultConfig();
		}
	}
}

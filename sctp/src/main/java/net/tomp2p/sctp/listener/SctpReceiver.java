package net.tomp2p.sctp.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import lombok.Getter;
import lombok.Setter;
import net.tomp2p.sctp.core.NetworkLink;
import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.core.SctpConfig;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core.UdpLink;
import net.tomp2p.sctp.core.UdpLinkBroker;
import net.tomp2p.utils.Pair;

public class SctpReceiver {

	@Getter
	final private SctpSocket socket;
	@Getter
	final private NetworkLink link;
	@Getter @Setter
	private SctpDataCallback callback;

	public SctpReceiver(InetSocketAddress local, InetSocketAddress remote) throws IOException {
		socket = Sctp.createSocket(local.getPort());
		link = new UdpLink(socket, local.getAddress().getHostAddress(), local.getPort(),
				remote.getAddress().getHostAddress(), remote.getPort());
		socket.setLink(link);
	}
	
	public SctpReceiver(InetSocketAddress local, UdpLinkBroker link, SctpDataCallback callback) throws IOException {
		socket = Sctp.createSocket(local.getPort());
		this.link = link;
		this.callback = callback;
		socket.setLink(link);
	}

	public Promise<SctpSocket, Exception, NetworkLink> listen() throws IOException {
		Deferred<SctpSocket, Exception, NetworkLink> d = new DeferredObject<SctpSocket, Exception, NetworkLink>();
		socket.listen();
		SctpConfig.getThreadPoolExecutor().execute(new SctpListenThread(socket, callback, d));
		return d.promise();
	}

}

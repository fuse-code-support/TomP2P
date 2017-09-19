package net.tomp2p.sctp.listener;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.sctp.core.SctpConfig;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core.UdpLink;

public class SctpSender {

	public static final Logger LOG = LoggerFactory.getLogger(SctpSender.class);
	
	public Promise<SctpSocket, IOException, UdpLink> connect(InetSocketAddress local, InetSocketAddress remote) {
		
		Deferred<SctpSocket, IOException, UdpLink> deferred = new DeferredObject<>();
		SctpConfig.getThreadPoolExecutor().execute(new SctpConnectThread(local, remote, deferred));
		
		return deferred.promise();
	}
	
	public Deferred<Object, Exception, Object> send(SctpSocket socket, String message) {
		Deferred<Object, Exception, Object> deferred = new DeferredObject<>();
		
		int success = -1;
		try {
			success = socket.send(message.getBytes(), 0, message.getBytes().length, false, 1, 0);
		} catch (IOException e) {
			deferred.reject(e);
		} finally {
			if (success == -1) {
				deferred.reject(new IOException("Could not send message!"));
			} else {
				deferred.resolve(null);
			}
		}
		
		return deferred;
	}
}

package net.tomp2p.sctp.core2;

import java.net.InetSocketAddress;

import org.jdeferred.Promise;

public interface SctpFacade {
	
	public static final int MAX_NR_OF_CONN = 65535; //2³²
	
	public void listen();
	public Promise<SctpFacade, Exception, Object> connect(int localPort);
	public int send(InetSocketAddress remote);
	public void receive(); //TODO jwa implement this
	public int close(); //TODO implement shutdown
	
}

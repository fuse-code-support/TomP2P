package net.tomp2p.sctp.core2;

import java.net.InetSocketAddress;

import org.jdeferred.Promise;

public interface SctpFacade {
	
	static final int MAX_NR_OF_CONN = 65535; //2³²
	
	void listen();
	Promise<SctpFacade, Exception, Object> connect(int localPort);
	int send(InetSocketAddress remote);
	void receive(); //TODO jwa implement this
	int close(); //TODO implement shutdown
	boolean containsSctpSocket(SctpSocket so);
	void onConnIn(byte[] data, int offset, int length);
}

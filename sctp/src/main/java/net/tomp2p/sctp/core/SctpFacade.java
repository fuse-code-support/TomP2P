package net.tomp2p.sctp.core;

import org.jdeferred.Promise;

import java.net.InetSocketAddress;

public interface SctpFacade {
	
	void listen();
	Promise<SctpFacade, Exception, Object> connect(int localPort);
	int send(InetSocketAddress remote);
	void receive(); //TODO jwa implement this
	int close(); //TODO implement shutdown
	boolean containsSctpSocket(SctpSocket so);
	void onConnIn(byte[] data, int offset, int length);
	boolean accept();
	void setSctpDataCallback(SctpDataCallback cb);
}

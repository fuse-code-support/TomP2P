package net.tomp2p.sctp.core;

import java.net.InetSocketAddress;

import org.jdeferred.Promise;

import lombok.Builder;

public interface SctpFacade {
	
	static final int MAX_NR_OF_CONN = 65535; //2¹⁶
	
	void listen();
	Promise<SctpFacade, Exception, Object> connect(int localPort);
	int send(InetSocketAddress remote);
	void receive(); //TODO jwa implement this
	int close(); //TODO implement shutdown
	boolean containsSctpSocket(SctpSocket so);
	void onConnIn(byte[] data, int offset, int length);
	boolean accept();
}

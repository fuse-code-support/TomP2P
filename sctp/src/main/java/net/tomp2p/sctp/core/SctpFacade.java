package net.tomp2p.sctp.core;

import org.jdeferred.Promise;

import java.net.InetSocketAddress;

public interface SctpFacade {
	
	void listen();
	Promise<SctpFacade, Exception, Object> connect(InetSocketAddress remote);
	void receive(); //TODO jwa implement this
	int close(); //TODO implement shutdown
	boolean containsSctpSocket(SctpSocket so);
	void onConnIn(byte[] data, int offset, int length);
	boolean accept();
	void setSctpDataCallback(SctpDataCallback cb);
	void setLink(NetworkLink link);
	InetSocketAddress getRemote();
	int send(byte[] data, int offset, int len, boolean ordered, int sid, int ppid);
	int send(byte[] data, boolean ordered, int sid, int ppid);
}

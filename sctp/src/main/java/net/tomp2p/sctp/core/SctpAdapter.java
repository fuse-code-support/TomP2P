package net.tomp2p.sctp.core;

import org.jdeferred.Promise;

import java.net.InetSocketAddress;

public interface SctpAdapter extends SctpChannelFacade{
	
	void listen();
	Promise<SctpAdapter, Exception, Object> connect(InetSocketAddress remote);
	void receive(); //TODO jwa implement this
	boolean containsSctpSocket(SctpSocket so);
	void onConnIn(byte[] data, int offset, int length);
	boolean accept();
	void setSctpDataCallback(SctpDataCallback cb);
	void setLink(NetworkLink link);
	InetSocketAddress getRemote();
}

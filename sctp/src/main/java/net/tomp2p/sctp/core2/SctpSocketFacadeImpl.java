package net.tomp2p.sctp.core2;

import java.net.InetSocketAddress;

import org.jdeferred.Promise;

public class SctpSocketFacadeImpl implements SctpFacade{

	@Override
	public void listen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Promise<SctpFacade, Exception, Object> connect(int localPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int send(InetSocketAddress remote) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int close() {
		// TODO Auto-generated method stub
		return 0;
	}

}

package net.tomp2p.connection.sctp;

import java.net.InetAddress;

import net.tomp2p.connection.negotiation.ConnectionBroker;
import net.tomp2p.connection.negotiation.UdpListener;

public class SctpReceiverTestDriver {
	public static void main(String[] args) throws Exception {
		Sctp.init();
		
		InetAddress localAddress = InetAddress.getByName("10.200.13.224");
		int localPort = 9999;
		
		ConnectionBroker broker = new ConnectionBroker();
		UdpListener listener = new UdpListener(broker);
		listener.listen(localAddress, localPort);
	}
}

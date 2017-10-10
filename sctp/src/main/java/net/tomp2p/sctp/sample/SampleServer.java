package net.tomp2p.sctp.sample;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.tomp2p.sctp.connection.SctpDispatcher;
import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.UdpServerLink;
import net.tomp2p.utils.Pair;

public class SampleServer {

	public static void main(String[] args) throws UnknownHostException, SocketException {

		Sctp.init();
		
		InetSocketAddress local = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9899);
		
		SctpDispatcher dispatcher = new SctpDispatcher();
		
		SctpDataCallback cb = new SctpDataCallback() {
			
			@Override
			public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
					Pair<InetAddress, Integer> remote) {
				System.out.println("I WAS HERE");
			}
		};
		
		UdpServerLink link = new UdpServerLink(dispatcher, local.getAddress(), cb);
		
		System.out.println("SETUP COMPLETE");
	}
}

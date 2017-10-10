package net.tomp2p.sctp.connection.sample;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import net.tomp2p.sctp.connection.SctpUtils;
import net.tomp2p.sctp.core.SctpAdapter;
import net.tomp2p.sctp.core.SctpDataCallback;

public class SimpleServer {
	
	public static void main(String[] args) throws Exception {
		InetAddress localHost = Inet6Address.getByName("::1");
		
		SctpDataCallback cb = new SctpDataCallback() {
			
			@Override
			public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
					SctpAdapter so) {
				System.out.println("I WAS HERE");
				System.out.println("got data: " + new String(data, StandardCharsets.UTF_8));
				so.send(data, false, sid, (int) ppid);
			}
		};
		
		SctpUtils.init(localHost, 9899, cb);
		
		System.out.println("Server ready!");
	}
}

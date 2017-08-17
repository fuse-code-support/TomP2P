package net.tomp2p.connection.sctp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.connection.negotiation.SctpBroker;
import net.tomp2p.connection.negotiation.UdpListener;
import net.tomp2p.utils.Pair;

public class SctpTestDriver {

	private static final Logger logger = LoggerFactory.getLogger(SctpTestDriver.class);
	
	public static void main(String[] args) throws Exception {

		Sctp.init();

		InetAddress localAddress = InetAddress.getByName("10.200.14.101");
		int localPort = 9989;
		InetAddress remoteAddress = InetAddress.getByName("10.200.13.224");
		int remotePort = 9999;

		SctpBroker broker = new SctpBroker();
//		UdpListener listener = new UdpListener(broker);
//		listener.listen(localAddress, localPort);
		
		Promise<SctpSocket, Exception, UdpLink> p = broker.connect(localAddress, localPort, remoteAddress, remotePort);
		p.done(new DoneCallback<SctpSocket>() {

			@Override
			public void onDone(SctpSocket result) {
				try {
					result.send(new String("Hello World").getBytes(), false, 0, 1);
					result.setDataCallback(new SctpDataCallback() {
						
						@Override
						public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
								Pair<InetAddress, Integer> remote) {
							logger.error(new String(data, StandardCharsets.UTF_8));
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		p.fail(new FailCallback<Exception>() {

			@Override
			public void onFail(Exception result) {
				System.out.println("ERROR");
			}
		});
	}

}

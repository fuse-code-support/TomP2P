package net.tomp2p.sctp.sample;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import javassist.NotFoundException;
import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core.UdpLinkBroker;
import net.tomp2p.utils.Pair;

public class SctpReceiverTestDriver2 {

	public static void main(String[] args) throws IOException {
		Sctp.init();

		InetAddress localAddress = InetAddress.getByName("192.168.0.106");
		int localPort = 9899;

		SctpDataCallback callback = new SctpDataCallback() {

			@Override
			public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
					Pair<InetAddress, Integer> remote) {
				String s = new String(data, StandardCharsets.UTF_8);

				System.out.println("got message from " + remote.element0() + ":" + remote.element1() + ": \\n " + s);

				String message = s + " replied";
				try {
					SctpSocket sctpSocket = Sctp.findSctpSocket(remote);
					int success = -1;
					success = sctpSocket.send(message.getBytes(), false, sid, (int) ppid);

					if (success == -1) {
						Thread.sleep(100);
						success = sctpSocket.send(message.getBytes(), false, sid, (int) ppid);
					}
				} catch (NotFoundException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		UdpLinkBroker link = new UdpLinkBroker(localAddress, localPort, callback);
		link.listen();
	}
}

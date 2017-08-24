package net.tomp2p.sctp.sample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;

import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.core.SctpDataCallback;
import net.tomp2p.sctp.core.SctpSocket;
import net.tomp2p.sctp.core.UdpLink;
import net.tomp2p.sctp.listener.SctpSender;
import net.tomp2p.utils.Pair;

public class SctpTestDriver2 {

	public static UdpLink link = null;

	public static void main(String[] args) {

		Sctp.init();

		InetSocketAddress local = InetSocketAddress.createUnresolved("192.168.0.103", 9999);
		InetSocketAddress remote = InetSocketAddress.createUnresolved("192.168.0.106", 9899);

		SctpSender sender = new SctpSender();
		Promise<SctpSocket, IOException, UdpLink> p = sender.connect(local, remote);

		p.progress(new ProgressCallback<UdpLink>() {

			@Override
			public void onProgress(UdpLink progress) {
				SctpTestDriver2.link = progress;

				link.getSctpSocket().setDataCallback(new SctpDataCallback() {

					@Override
					public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
							Pair<InetAddress, Integer> remote) {
						String s = new String(data, StandardCharsets.UTF_8);

						System.out.println("got message: /n " + s);

					}
				});
			}
		});

		try {
			p.waitSafely();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!p.isResolved()) {
			System.out.println("FAIL!");
			p.fail(new FailCallback<IOException>() {

				@Override
				public void onFail(IOException result) {
					result.printStackTrace();
				}
			});
		} else {
			p.done(new DoneCallback<SctpSocket>() {

				@Override
				public void onDone(SctpSocket result) {
					System.out.println("SUCCESS!");
					sender.send(result, "Hello World");
				}
			});
		}
	}

}

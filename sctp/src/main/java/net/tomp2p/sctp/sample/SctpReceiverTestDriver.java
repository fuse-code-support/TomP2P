package net.tomp2p.sctp.sample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.listener.SctpReceiver;

public class SctpReceiverTestDriver {
	public static void main(String[] args) {
		Sctp.init();
		
		InetSocketAddress local = null;
		InetSocketAddress remote = null;
		try {
			local = new InetSocketAddress(InetAddress.getByName("192.168.0.103"), 9899);
			remote = new InetSocketAddress(InetAddress.getByName("192.168.0.106"), 9999);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if (local == null | remote == null) {
			throw new NullPointerException();
		}

		SctpReceiver receiver = null;
		try {
			receiver = new SctpReceiver(local, remote);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (receiver == null) {
			throw new NullPointerException();
		}
		
		try {
			receiver.listen(local);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(400000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}

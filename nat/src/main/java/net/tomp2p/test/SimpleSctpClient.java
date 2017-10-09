package net.tomp2p.test;

import net.tomp2p.connection.Bindings;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.nat.PeerNAT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class SimpleSctpClient {

	private static Peer peer;
	private static final int port = 4000;
	private static PeerNAT peerNAT;
	private static PeerAddress masterPeerAddress;
	private static String masterIpAddress;

	public static void start(boolean isMaster, String id, String eth) {

		try {

			createPeer(isMaster, id, eth);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(peer.peerAddress().toString());
	}

	private static void createPeer(boolean isMaster, String id, String eth) throws IOException {

		// we have to check if we override the default interface (which is in
		// most cases eth0)
		Bindings b = new Bindings();
		if (eth != null && eth.length() > 0) {
			b.addInterface(eth);
		}

		if (isMaster) {
			peer = new PeerBuilder(Number160.createHash("master")).bindings(b).ports(port).start();
			new PeerBuilderNAT(peer).start();
		} else {
			peer = new PeerBuilder(Number160.createHash(id)).bindings(b).ports(port).start();
			peerNAT = new PeerBuilderNAT(peer).start();
		}
	}

	public static Peer getPeer() {
		return peer;
	}

	public static boolean usualBootstrap(String masterIpAddress) throws UnknownHostException {
		boolean success = false;

		masterPeerAddress = PeerAddress.create(Number160.createHash("master"), Inet4Address.getByName(masterIpAddress), port, port, port);
		
		// do PeerDiscover
		FutureDiscover fd = peer.discover().peerAddress(peer.peerAddress()).start().awaitUninterruptibly();
		if (!fd.isSuccess()) {
			return success;
		}

		FutureBootstrap fb = peer.bootstrap().peerAddress(masterPeerAddress).start();
		fb.awaitUninterruptibly();
		if (fb.isSuccess()) {
			System.out.println("BOOTSTRAP SUCCESS!");
			success = true;
		} else {
			System.out.println("BOOTSTRAP FAIL!");
		}

		return success;
	}
	
	public static boolean sendDummy(String dummy, String id, String ip) throws IOException {
		boolean success = false;
		PeerAddress recepient = null;

		if (id == null || ip == null) {
			System.out.println("MESSAGE SENT TO MASTER");
			recepient = masterPeerAddress;
		} else {
			System.out.println("DIRECTED MESSAGE TO " + ip);
			recepient = PeerAddress.create(Number160.createHash(id), Inet4Address.getByName(ip), port, port, port);
		}

		FutureDirect fd = peer.sendDirect(recepient).object(dummy).start();
		fd.awaitUninterruptibly(10000);

		if (fd.isSuccess()) {
			System.out.println("FUTURE DIRECT SUCCESS!");
			success = true;
		} else {
			System.out.println("FUTURE DIRECT FAIL!");
		}

		return success;
	}

}

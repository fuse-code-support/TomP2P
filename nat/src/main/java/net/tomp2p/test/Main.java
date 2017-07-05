package net.tomp2p.test;

import net.tomp2p.connection.Bindings;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Random;

/**
 * Created by root on 28.02.17.
 */
public class Main {

	private static final Random r = new Random();
	private static Peer peer = null;
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		if (args.length != 0 && args[0].equals("master")) {
			try {
				Bindings b = new Bindings();

				if (args.length > 1) {
					b.addInterface(args[1]);
				} else {
					b.addInterface("eth3");
				}
				peer = new PeerBuilder(new Number160(r)).bindings(b).ports(4000).behindFirewall(true).start();

				peer.objectDataReply(new ObjectDataReply() {
					public Object reply(PeerAddress peerAddress, Object o) throws Exception {
						LOG.error("Message from peer: " + peerAddress.toString() + "\n" + "Message: " + ((String) o));
						return "World";
					}
				});
			} catch (IOException e) {
				LOG.error(e.getStackTrace().toString());
			}
		} else {
			try {
				Bindings b = new Bindings();
				b.addInterface("enp0s25");
				peer = new PeerBuilder(new Number160(r)).bindings(b).ports(4001).start();
//				peer.objectDataReply(new ObjectDataReply() {
//					public Object reply(PeerAddress peerAddress, Object o) throws Exception {
//						LOG.error("Message from peer: " + peerAddress.toString() + "\n" + "Message: " + ((String) o));
//						return "";
//					}
//				});

//				InetAddress address = Inet4Address.getByName("194.230.137.73");
				InetAddress address = Inet4Address.getByName("10.1.1.1");

				FutureDiscover futureDiscover = peer.discover().inetAddress(address).ports(4000).start();
				futureDiscover.awaitUninterruptibly();

				if (futureDiscover.isSuccess()) {
					System.out.println("found that my outside address is " + futureDiscover.peerAddress());
				} else {
					System.out.println("failed " + futureDiscover.peerAddress());
					LOG.error("FAIL");
					LOG.error("shutdown initiated");
					BaseFuture fs = peer.shutdown();
					fs.awaitUninterruptibly();
					System.exit(1);
				}
				FutureBootstrap fb = peer.bootstrap().peerAddress(futureDiscover.peerAddress()).start();
				fb.awaitUninterruptibly();

				if (fb.isSuccess()) {
					LOG.error("SUCCESS");
				} else {
					LOG.error("FAIL");
					LOG.error("shutdown initiated");
					BaseFuture fs = peer.shutdown();
					fs.awaitUninterruptibly();
					System.exit(1);
				}

				FutureDirect direct = peer.sendDirect(futureDiscover.reporter()).object("HELLO").start();
				direct.awaitUninterruptibly();
				
				if (direct.isSuccess()) {
					LOG.error("SUCCESS INDEED!");
					System.out.println((String) direct.object());
					System.out.println(direct.object().toString());
					System.out.println(direct);
				} else {
					System.out.println("MESSAGE COULD NOT BE TRANSFERRED");
				}
				
				LOG.error("shutdown initiated");
				BaseFuture fs = peer.shutdown();
				fs.awaitUninterruptibly();
				System.exit(0);

			} catch (Exception e) {
				LOG.error(e.getStackTrace().toString());
			}
		}
	}

}


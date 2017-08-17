package net.tomp2p.connection.negotiation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.connection.Ports;
import net.tomp2p.connection.sctp.SctpConnectThread;
import net.tomp2p.connection.sctp.SctpDataCallback;
import net.tomp2p.connection.sctp.SctpReceiver;
import net.tomp2p.connection.sctp.SctpSocket;
import net.tomp2p.connection.sctp.UdpLink;
import net.tomp2p.utils.Pair;

public class ConnectionBroker {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionBroker.class);
	
	/**
	 * Integer: id InetSocketAddress: remote socket
	 */
	private final Map<SctpSocket, Pair<InetAddress, Integer>> activePeers = new ConcurrentHashMap<>();

	public org.jdeferred.Promise<SctpSocket, Exception, UdpLink> connect(InetAddress localAddress, int localPort, InetAddress remoteAddress, int remotePort) {
		Deferred<SctpSocket, Exception, UdpLink> def = new DeferredObject<>();
		int portCandidate = assignPort(); 
		String portInfo = "" + portCandidate;
		
		try {
			DatagramSocket udpSocket = new DatagramSocket(localPort, localAddress);
			DatagramPacket p = new DatagramPacket(portInfo.getBytes(), portInfo.length(), remoteAddress, remotePort);
			
			//TODO: fix timeout time 
			int timeout = 50000; //50s at the moment
			
			// Listening thread
			new Thread(new Runnable() {
				public void run() {
					try {
						byte[] buff = new byte[2048];
						DatagramPacket p = new DatagramPacket(buff, 2048);
						long startTime = System.currentTimeMillis();
						while ((System.currentTimeMillis()-startTime)<timeout) {
							
//							udpSocket.setSoTimeout(timeout);
							udpSocket.receive(p); //this method blocks
							
							String s = new String(p.getData(), StandardCharsets.UTF_8);
							int remotePortInfo = Integer.parseInt(s.substring(0, 5));
							InetSocketAddress local = InetSocketAddress.createUnresolved(localAddress.getHostName(), portCandidate);
							InetSocketAddress remote = InetSocketAddress.createUnresolved(remoteAddress.getHostName(), remotePortInfo);
							
							SctpConnectThread thread = new SctpConnectThread(local, remote, def);
							thread.start();
						}
						//TODO: jwa maybe do not close the socket here
						udpSocket.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}).start();
			udpSocket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return def.promise();
	}

	public synchronized void addNew(final InetAddress remoteAddress, final int RemotePortInitiator, final byte[] data, final DatagramSocket listener) {
		String s = new String(data, StandardCharsets.UTF_8);
		int remotePortInfo = Integer.parseInt(s.substring(0, 5));
		if (!checkIfNew(remoteAddress, remotePortInfo)) {
			logger.warn("The connection is already established with ip:" + remoteAddress.toString() + " and port: " + remotePortInfo
					+ ". TomP2P ignores this connection attempt.");
			return;
		} else {
			logger.warn("new Connection initiated from peer: " + remoteAddress + ":" + RemotePortInitiator);
			int localPort = assignPort();
			Promise<SctpSocket, Exception, UdpLink> promise = negotiate(remoteAddress, localPort, remotePortInfo, listener);
			promise.done(new DoneCallback<SctpSocket>() {
				
				@Override
				public void onDone(SctpSocket result) {
					activePeers.put(result, new Pair<InetAddress, Integer>(remoteAddress, remotePortInfo));
					
					//TODO jwa: we should forward all this stuff to a dispatcher
					result.setDataCallback(new SctpDataCallback() {
						
						@Override
						public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
								Pair<InetAddress, Integer> remote) {
							System.out.println(new String(data, StandardCharsets.UTF_8));
						}
					});
					
					String port = "" + localPort;
					try {
						listener.send(new DatagramPacket(port.getBytes(), port.length(), remoteAddress, RemotePortInitiator));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
			promise.fail(new FailCallback<Exception>() {
				
				@Override
				public void onFail(Exception result) {
					result.printStackTrace();
				}
			});
		}
	}

	private org.jdeferred.Promise<SctpSocket, Exception, UdpLink> negotiate(final InetAddress remoteAddress, final int localPort,
			final Integer remotePortInfo, DatagramSocket listener) {
		Deferred<SctpSocket, Exception, UdpLink> def = new DeferredObject<>();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				SctpReceiver receiver;
				try {
					receiver = new SctpReceiver(listener.getLocalAddress(), localPort, remoteAddress, remotePortInfo);
					def.notify(receiver.getLink());
					receiver.listen();
					def.resolve(receiver.getSocket());
				} catch (IOException e) {
					def.reject(e);
					logger.error("Receiver could not be created correctly.", e);
				}
			}
		}).start();
		
		return def.promise();
	}
	
	private boolean checkIfNew(InetAddress address, Integer remotePortInfo) {
		for (Map.Entry<SctpSocket, Pair<InetAddress, Integer>> entry : activePeers.entrySet()) {
			if ((entry.getValue().element0().toString().equals(address.toString()))
					&& entry.getValue().element1().intValue() == remotePortInfo.intValue()) {
				return false;
			}
		}
		return true;
	}
	
	private int assignPort() {
		Random rand = new Random();
		
		//TODO: jwa fix this port choosing mess
		int portCandidate = rand.nextInt(Ports.MAX_PORT - Ports.MIN_DYN_PORT) + Ports.MIN_DYN_PORT;
		return portCandidate;
	}

	public int convertByteToInt(byte[] b) {
		int value = 0;
		return (value << 16) | b[0];
	}
}

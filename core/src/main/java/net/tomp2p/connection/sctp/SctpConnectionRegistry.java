package net.tomp2p.connection.sctp;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import net.tomp2p.utils.Pair;

public class SctpConnectionRegistry {
	
	private static final ConcurrentHashMap<String, Pair<InetAddress, Integer>> connections = new ConcurrentHashMap<>();

	public static synchronized void put(final String key, final Pair<InetAddress, Integer> value) {
		connections.put(key, value);
	}
	
	public static synchronized void remove(final String key) {
		connections.remove(key);
	}
	
	public static synchronized void remove(final Pair<InetAddress, Integer> value) {
		connections.remove(value);
	}
	
	public static Pair<InetAddress, Integer> find(String key) {
		return connections.get(key);
	}
}

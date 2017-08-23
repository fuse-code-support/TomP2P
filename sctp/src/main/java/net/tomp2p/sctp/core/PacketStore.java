package net.tomp2p.sctp.core;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.NotFoundException;
import net.tomp2p.utils.Pair;

public final class PacketStore {

	private static final Logger LOG = LoggerFactory.getLogger(PacketStore.class);
	private final ConcurrentHashMap<Long, Pair<InetAddress, Integer>> packetRemoteAddressMap = new ConcurrentHashMap<>();
	
	public synchronized void addPacket(final Long id, final Pair<InetAddress, Integer> packetRemoteAddress) {
		this.packetRemoteAddressMap.put(id, packetRemoteAddress);
	}
	
	public Pair<InetAddress, Integer> getRemoteAddress(final Long id) throws NotFoundException {
		Pair<InetAddress, Integer> packetRemoteAddress = this.packetRemoteAddressMap.get(id);
		if (packetRemoteAddress != null) {
			return packetRemoteAddress;
		} else {
			LOG.error("remote address not found for id:" + id);
			throw new NotFoundException("remote address not found for id:" + id);
		}
	}
	
	public synchronized Pair<InetAddress, Integer> getAndRemoveRemoteAddress(final Long id) throws NotFoundException {
		Pair<InetAddress, Integer> packetRemoteAddress = this.packetRemoteAddressMap.get(id);
		if (packetRemoteAddress != null) {
			return packetRemoteAddress;
		} else {
			LOG.error("remote address not found for id:" + id);
			throw new NotFoundException("remote address not found for id:" + id);
		}
	}
}

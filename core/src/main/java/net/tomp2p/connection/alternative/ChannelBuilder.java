package net.tomp2p.connection.alternative;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.connection.ChannelClientConfiguration;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.connection.DiscoverNetworkListener;
import net.tomp2p.connection.DiscoverResults;
import net.tomp2p.connection.sctp.Sctp;
import net.tomp2p.connection.sctp.SctpSocket;
import net.tomp2p.connection.sctp.UdpLink;
import net.tomp2p.futures.FutureDone;

public class ChannelBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelBuilder.class);

	private final int maxPermitsUDP;
	private final int maxPermitsSCTPWithUDP;

	private final Semaphore semaphoreUPD;
	private final Semaphore semaphoreSCTPWithUDP;

	// we should be fair, otherwise we see connection timeouts due to unfairness
	// if busy
	private final ReadWriteLock readWriteLockUDP = new ReentrantReadWriteLock(true);
	private final Lock readUDP = readWriteLockUDP.readLock();
	private final Lock writeUDP = readWriteLockUDP.writeLock();

	private final ReadWriteLock readWriteLockSCTPWithUDP = new ReentrantReadWriteLock(true);
	private final Lock readTCP = readWriteLockSCTPWithUDP.readLock();
	private final Lock writeTCP = readWriteLockSCTPWithUDP.writeLock();

	private final FutureDone<Void> futureChannelCreationDone;

	private final ChannelClientConfiguration channelClientConfiguration;

	private final InetAddress sendFromAddress;

	private boolean shutdownUDP = false;
	private boolean shutdownSCTPWithUDP = false;

	/**
	 * Package private constructor, since this is created by {@link Reservation} and
	 * should never be called directly.
	 * 
	 * @param futureChannelCreationDone
	 *            We need to set this from the outside as we want to attach
	 *            listeners to it
	 * @param maxPermitsUDP
	 *            The number of max. parallel UDP connections.
	 * @param maxPermitsSCTPWithUDP
	 *            The number of max. parallel SCTPWithUDP connections.
	 * @param channelClientConfiguration
	 *            The configuration that contains the pipeline filter
	 */
	ChannelBuilder(final FutureDone<Void> futureChannelCreationDone, int maxPermitsUDP, int maxPermitsSCTPWithUDP,
			final ChannelClientConfiguration channelClientConfiguration, InetAddress sendFromAddress) {
		this.futureChannelCreationDone = futureChannelCreationDone;
		this.maxPermitsUDP = maxPermitsUDP;
		this.maxPermitsSCTPWithUDP = maxPermitsSCTPWithUDP;
		this.semaphoreUPD = new Semaphore(maxPermitsUDP);
		this.semaphoreSCTPWithUDP = new Semaphore(maxPermitsSCTPWithUDP);
		this.channelClientConfiguration = channelClientConfiguration;
		this.sendFromAddress = sendFromAddress;
	}

	public FutureDone createUDP(final FutureDone future) {
		// do something
		Math.random();
		return future;
	}

	public FutureDone<SctpSocket> createSCTPWithUDP(final FutureDone<SctpSocket> future, final String localAddr,
			final int localPort, final String remoteAddr, final int remotePort) {

		// FIXME jwa this must be initialized way earlier
		Sctp.init();

		// FIXME jwa this is a magic number (could be specialized by some config class)
		//
		// 9899 is the official assigne port for SCTP over UDP. (see IANA
		// https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt)
		final int officialSctpPort = 9989;
		
		// 9899 is the official assigne port for SCTP over UDP. (see IANA
		// https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt)
		final SctpSocket client = Sctp.createSocket(officialSctpPort);

		UdpLink link;
		try {
			link = new UdpLink(client, localAddr, localPort, remoteAddr, remotePort);
		} catch (IOException e) {
			LOG.error("Could not set UDP link for Sctp connection!", e);
			e.printStackTrace();
			return future.failed(e);
		}

		client.setLink(link);

		try {
			client.connect(officialSctpPort);
		} catch (IOException e) {
			LOG.error("Sctp connect failed!", e);
			e.printStackTrace();
			return future.failed(e);
		}

		return future.done(client);
	}
}

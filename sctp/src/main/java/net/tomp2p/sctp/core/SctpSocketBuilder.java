package net.tomp2p.sctp.core;

import net.tomp2p.connection.Ports;
import net.tomp2p.sctp.connection.SctpDispatcher;
import net.tomp2p.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SctpSocketBuilder {

	//TODO jwa implement all possible variables and parameters for a given SCTP connection

	private static final Logger LOG = LoggerFactory.getLogger(SctpSocketBuilder.class);

	private int localSctpPort = -1;
	private int localPort = -1;
	private InetAddress localAddress = null;
	private int remotePort = -1;
	private InetAddress remoteAddress = null;
	private SctpDataCallback cb = null;
	private NetworkLink link = null;
	private SctpDispatcher dispatcher = null;
	
	public SctpFacade build() {

		if (localSctpPort == -1) {
			localSctpPort = SctpPorts.getInstance().generateDynPort();
		}

		if (cb == null) {
			cb = new SctpDataCallback() {
				@Override
				public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags, Pair<InetAddress, Integer> remote) {
					//do nothing
				}
			};
		}
		
		if (dispatcher == null) {
			LOG.error("No dispatcher added! You need a Dispatcher to create a new SctpFacade!");
			return null;
		}

		InetSocketAddress local = new InetSocketAddress(localAddress, localSctpPort);
		SctpFacade so = null;
		if (remoteAddress == null || remotePort == -1) {
			return (SctpFacade) new SctpSocketAdapter(local, link, cb, dispatcher);
		} else {
			InetSocketAddress remote = new InetSocketAddress(remoteAddress, remotePort);
			return (SctpFacade) new SctpSocketAdapter(local, remote, link, cb, dispatcher);
		}

	}

	public SctpSocketBuilder localPort(int localPort) {
		if (isInRange(localPort)) {
			this.localPort = localPort;
		} else {
			LOG.error("Port is out of range (possible range: 0-65535)!");
			return null;
		}
		return this;
	}

	public SctpSocketBuilder localSctpPort(int localSctpPort) {
		if (isInRange(localSctpPort)) {
			this.localSctpPort = localSctpPort;
		} else {
			LOG.error("Port is out of range (possible range: 0-65535)!");
			return null;
		}
		return this;
	}

	private boolean isInRange(int localPort) {
		return localPort >= 0 && localPort < Ports.MAX_PORT;
	}

	public SctpSocketBuilder localAddress(InetAddress localAddress) {
		if (localAddress != null) {
			this.localAddress = localAddress;
		} else {
			LOG.error("Null can't be added as localAddress!");
		}
		return this;
	}

	public SctpSocketBuilder remotePort(int remotePort) {
		if (isInRange(remotePort)) {
			this.remotePort = remotePort;
		} else {
			LOG.error("Port is out of range (possible range: 0-65535)!");
		}
		return this;
	}

	public SctpSocketBuilder remoteAddress(InetAddress remoteAddress) {
		if (remoteAddress != null) {
			this.remoteAddress = remoteAddress;
		} else {
			LOG.error("Null can't be added as remoteAddress!");
		}
		return this;
	}

	public SctpSocketBuilder sctpDataCallBack(SctpDataCallback cb) {
		if (cb != null) {
			this.cb = cb;
		} else {
			LOG.error("Null can't be added as dataCallback!");
		}
		return this;
	}

	public SctpSocketBuilder networkLink(NetworkLink link) {
		if (link != null) {
			this.link = link;
		} else {
			LOG.error("Null can't be added as networkLink!");
		}
		return this;
	}
	
	public SctpSocketBuilder dispatcher(SctpDispatcher dispatcher) {
		if (dispatcher != null) {
			this.dispatcher = dispatcher;
		} else {
			LOG.error("Null can't be added as dispatcher!");
		}
		return this;
	}
}

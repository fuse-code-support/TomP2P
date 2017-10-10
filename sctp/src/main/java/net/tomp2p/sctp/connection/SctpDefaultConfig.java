package net.tomp2p.sctp.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import net.tomp2p.sctp.core.SctpAdapter;
import net.tomp2p.sctp.core.SctpDataCallback;

public class SctpDefaultConfig {
	//TODO jwa implement this

	private static final Logger LOG = LoggerFactory.getLogger(SctpDefaultConfig.class);

	@Getter
	private final SctpDataCallback cb = new SctpDataCallback() {
		
		@Override
		public void onSctpPacket(byte[] data, int sid, int ssn, int tsn, long ppid, int context, int flags,
				SctpAdapter so) {
			//do nothing and notify the log
			LOG.info("ignored message from " + so.getRemote().getAddress().getHostAddress() + ":" + so.getRemote().getPort());
		}
	};

	@Getter
	private int connectPeriodMillis = 500;
}

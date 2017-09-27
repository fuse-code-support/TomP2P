package net.tomp2p.sctp.core;

import java.net.InetAddress;

import lombok.Builder;
import lombok.Setter;

@Builder
public class SctpSocketBuilder {

	//TODO jwa implement all possible variables and parameters for a given SCTP connection
	
	@Setter
	private int localPort = -1;
	@Setter
	private InetAddress localAddress = null;
	@Setter
	private int remotePort = -1;
	@Setter
	private InetAddress remoteAddress = null;
	@Setter
	private SctpDataCallback cb = null;
	@Setter
	private NetworkLink link = null;
	
	
	
	public SctpFacade build() {
		if (localPort == -1) {
			localPort = SctpPorts.getInstance().generateDynPort();
		}
		
		
		return null;
	}

}

package net.tomp2p.sctp.connection;

import java.net.InetSocketAddress;

import net.tomp2p.sctp.core2.SctpFacade;

public interface Dispatcher {

	void register(InetSocketAddress remote, SctpFacade so);

	void unregister(SctpFacade so);

	void establishChannel(); // TODO jwa what does this mean again?

	void getChannel(); // TODO jwa what does this mean again?

}

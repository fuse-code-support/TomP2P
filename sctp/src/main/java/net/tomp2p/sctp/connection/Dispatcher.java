package net.tomp2p.sctp.connection;

import net.tomp2p.sctp.core.SctpFacade;

import java.net.InetSocketAddress;

public interface Dispatcher {

	void register(InetSocketAddress remote, SctpFacade so);

	void unregister(SctpFacade so);

	void establishChannel(); // TODO jwa what does this mean again?

	void getChannel(); // TODO jwa what does this mean again?

}

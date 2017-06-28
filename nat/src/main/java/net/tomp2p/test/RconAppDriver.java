package net.tomp2p.test;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class RconAppDriver {

	private static RconAppDriver appDriver = new RconAppDriver();
	private static final Logger LOG = LoggerFactory.getLogger(RconAppDriver.class);
	
	private RconAppDriver() {

	}

	public static RconAppDriver getInstance() {
		return appDriver;
	}

	/**
	 * args[0] = interface (if there is only 1 argument, this is the masterpeer
	 * args[1] = ip of master (String)
	 * args[2] = my own id (String) (optional)
	 * args[3] = use relay bootstrapping
	 * 
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException {

		// set Logger Level
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.WARN);
		LOG.warn("Logger with Level " + Level.WARN.toString() + " initialized");

		if (args.length > 1) {
			SimpleSctpClient.start(false, args[2], args[0]);

			// do usual Bootstrap (normal peer)
			if (args.length == 3) {
				SimpleSctpClient.usualBootstrap(args[1]);
				System.err.println();
				System.err.println("usualBootstrap Success!");
				System.err.println();

				// do relay bootstrapping (nat peer)
			} else if (args.length > 3) {
				System.err.println();
				System.err.println("Start relaying");
				System.err.println();

				//SimpleSctpClient.natBootstrap(args[0]);
			}
		} else {
			SimpleSctpClient.start(true, null, args[0]);
		}

		// start GUI
		RconController rController = new RconController();
		rController.start();
	}
}

package net.tomp2p.sctp.connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import net.tomp2p.sctp.core.Sctp;
import net.tomp2p.sctp.core.SctpMapper;

public class SctpUtils {
	
	@Getter
	private static volatile boolean isInitialized = false;
	@Getter
	static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	@Getter
	static volatile SctpMapper mapper = new SctpMapper();
	
	public static synchronized void init() {
		
		if (isInitialized) {
			return; //we only need to init once
		}
		
		Sctp.init();
		
		isInitialized = true;
	}
}

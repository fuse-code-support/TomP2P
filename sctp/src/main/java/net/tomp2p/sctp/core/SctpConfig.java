package net.tomp2p.sctp.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

public class SctpConfig {

	@Getter
	private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
}

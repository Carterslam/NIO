package com.lilinfeng.threadpoolioŒ±“Ï≤Ω;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerServerHandlerExecutorPool {

	private ExecutorService executor;

	public TimerServerHandlerExecutorPool(int maxPoolSize,int queueSize) {
		System.out.println("ø…”√availableProcessors = "+Runtime.getRuntime().availableProcessors());
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
											maxPoolSize, 
											120L, 
											TimeUnit.SECONDS,
											new ArrayBlockingQueue<java.lang.Runnable>(queueSize));
	}

	

	public void execute(java.lang.Runnable task){
		executor.execute(task);
		
	}

}

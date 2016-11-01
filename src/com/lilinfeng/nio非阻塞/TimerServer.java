package com.lilinfeng.nio·Ç×èÈû;

import java.io.IOException;

public class TimerServer {
	
	public static void main(String[] args) throws IOException {
		int port=8080;
		

			MultipExerTimeServer timeServer = new MultipExerTimeServer(port);
			new Thread(timeServer,"NIO-MultipExerTimeServer-001").start();
			//System.exit(0);
		
	}
	
	
}

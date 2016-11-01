package com.lilinfeng.nio·Ç×èÈû;

import java.io.IOException;

public class TimeClient {
	public static void main(String[] args) throws IOException {
		int port=8080;
		String address = "127.0.0.1";

		TimeClientHandle timeClient = new TimeClientHandle(address,port);
			new Thread(timeClient,"NIO-TimeClientHandle-001").start();
			//System.exit(0);
	}
}

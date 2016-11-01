package com.lilinfeng.threadpoolioα�첽;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimerServer {
	
	public static void main(String[] args) throws IOException {
		int port=8080;
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port);
			System.out.println("timer server is start");
			Socket socket = null;
			TimerServerHandlerExecutorPool pool = new TimerServerHandlerExecutorPool(50,10000);
			
			while(true){
				socket = server.accept();
				pool.execute(new TimerServerHandler(socket));
			}
		} finally{
			if(server!=null){
				System.out.println("time server is close");
				server.close();
				server=null;
			}
		}

	}
	
	
}

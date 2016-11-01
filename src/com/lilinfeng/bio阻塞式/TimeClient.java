package com.lilinfeng.bio阻塞式;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient {


	public static void main(String[] args) {
		
		int port = 8080;
		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;

		try {
			socket = new Socket("127.0.0.1",port);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("QUERY TIME ORDER");
			System.out.println("send order 2 server succeed");
			String resp = in.readLine();
			System.out.println("Now is ==="+ resp);

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
		}
		if (out != null) {
			out.close();
			out = null;
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			socket = null;
		}

	}

}

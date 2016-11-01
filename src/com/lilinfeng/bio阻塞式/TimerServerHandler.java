package com.lilinfeng.bio����ʽ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimerServerHandler implements Runnable {

	private Socket socket;

	public TimerServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			in = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true);

			String body = null;
			String currentTime = null;

			while (true) {
				body = in.readLine();
				if (body == null)
					break;
				System.out
						.println("timer server handler recive body = " + body);
				if ("QUERY TIME ORDER".equalsIgnoreCase(body)) {
					currentTime = new java.util.Date(System.currentTimeMillis())
							.toString();
				} else {
					currentTime = "BAD ORDER";
				}
				out.println(currentTime);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
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
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			socket = null;
		}

	}

}

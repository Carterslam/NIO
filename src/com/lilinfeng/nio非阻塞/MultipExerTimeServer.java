package com.lilinfeng.nio非阻塞;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultipExerTimeServer implements Runnable {

	private Selector selector;

	private ServerSocketChannel servChannle;
	
	private volatile boolean stop;

	MultipExerTimeServer(int port) {
		try {
			// 1.打开多路复用器
			selector = Selector.open();
			// 2.打开通（管）道
			servChannle = ServerSocketChannel.open();
			// 3.通道配置非阻塞
			servChannle.configureBlocking(false);
			// 4.通道绑定地址和端口
			servChannle.bind(new InetSocketAddress("127.0.0.1",port), 1024);
			// 5.管道注册到多路复用器
			servChannle.register(selector, SelectionKey.OP_ACCEPT);
			// 6.打印启动信息
			System.out.println("nio time server start..."+SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(!stop){
			try {
				//多路复用器，每隔一秒轮询
				selector.select(3000);
				//获取就绪状态的channel
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				//当获取到就绪状态的channel
				while(it.hasNext()){
					key = it.next();
					//it.remove();
					try{
					handleInput(key);
					}catch(Exception e){
						key.cancel();
						if(key.channel()!=null)
							key.channel().close();
					}
				}
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
		
		if(selector!=null){
			try {
				selector.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}

	}
	
	//处理输入业务
	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			//处理接入新的请求
			if(key.isAcceptable()){
				ServerSocketChannel scc = (ServerSocketChannel)key.channel();
				SocketChannel sc = scc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				//获取管道
				SocketChannel sc = (SocketChannel)key.channel();
				//创建缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes>0){
					readBuffer.flip();//反转此缓冲区。limit=position, position=0，如果已定义了标记，则丢弃该标记。
					byte[] bytes = new byte[readBuffer.remaining()];//返回剩余的可用长度，此长度为实际读取的数据长度，最大自然是底层数组的长度。
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF-8");
					System.out.println("nio time server receive msg = "+body);
					
					String response = "QUERY TIME ORDER".equalsIgnoreCase(body)?new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
				    if(response!=null && response.length()>0){
				    	byte[] resBytes = response.getBytes();
				    	ByteBuffer writeBuffer = ByteBuffer.allocate(resBytes.length);
				    	writeBuffer.put(resBytes);
				    	writeBuffer.flip();
				    	sc.write(writeBuffer);
				    }
				}
			}
		}
	}
}

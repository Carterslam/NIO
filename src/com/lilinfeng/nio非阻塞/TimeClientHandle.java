package com.lilinfeng.nio������;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable{
	
	private int port;
	private String address;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;
	
	TimeClientHandle(String address,int port){
		this.address = address;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	
	//���ӷ����
	private void doConnection() throws IOException{
		boolean isConnect = socketChannel.connect(new InetSocketAddress(address, port));
		if(isConnect){
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	private void doWrite(SocketChannel sc) throws IOException{
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		if(!writeBuffer.hasRemaining()){
			System.out.println("there are no element between the current position and  the limit in byteBuffer");
		}
	}

	@Override
	public void run() {
		try {
			doConnection();
			
			while(!stop){
				selector.select(3000);
				Set<SelectionKey> selectionKeys = selector.keys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				//����ȡ������״̬��channel
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
			}
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	
	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			SocketChannel sc = (SocketChannel)key.channel();
			if(key.isConnectable()){
				if(sc.finishConnect()){
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				}else{
					System.exit(1);
				}
			}
			if(key.isReadable()){
				//����������
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes>0){
					readBuffer.flip();//��ת�˻�������limit=position, position=0������Ѷ����˱�ǣ������ñ�ǡ�
					byte[] bytes = new byte[readBuffer.remaining()];//����ʣ��Ŀ��ó��ȣ��˳���Ϊʵ�ʶ�ȡ�����ݳ��ȣ������Ȼ�ǵײ�����ĳ��ȡ�
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF-8");
					System.out.println("time client receive msg = "+body);
					
					this.stop = true;
				}else if(readBytes<0){
					key.cancel();
					sc.close();
				}else{
					System.out.println("no data");
				}
			
			}
		}
	}

	
}

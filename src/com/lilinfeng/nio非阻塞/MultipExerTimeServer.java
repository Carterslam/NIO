package com.lilinfeng.nio������;

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
			// 1.�򿪶�·������
			selector = Selector.open();
			// 2.��ͨ���ܣ���
			servChannle = ServerSocketChannel.open();
			// 3.ͨ�����÷�����
			servChannle.configureBlocking(false);
			// 4.ͨ���󶨵�ַ�Ͷ˿�
			servChannle.bind(new InetSocketAddress("127.0.0.1",port), 1024);
			// 5.�ܵ�ע�ᵽ��·������
			servChannle.register(selector, SelectionKey.OP_ACCEPT);
			// 6.��ӡ������Ϣ
			System.out.println("nio time server start..."+SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(!stop){
			try {
				//��·��������ÿ��һ����ѯ
				selector.select(3000);
				//��ȡ����״̬��channel
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
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
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
		}
		
		if(selector!=null){
			try {
				selector.close();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}

	}
	
	//��������ҵ��
	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			//��������µ�����
			if(key.isAcceptable()){
				ServerSocketChannel scc = (ServerSocketChannel)key.channel();
				SocketChannel sc = scc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				//��ȡ�ܵ�
				SocketChannel sc = (SocketChannel)key.channel();
				//����������
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes>0){
					readBuffer.flip();//��ת�˻�������limit=position, position=0������Ѷ����˱�ǣ������ñ�ǡ�
					byte[] bytes = new byte[readBuffer.remaining()];//����ʣ��Ŀ��ó��ȣ��˳���Ϊʵ�ʶ�ȡ�����ݳ��ȣ������Ȼ�ǵײ�����ĳ��ȡ�
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

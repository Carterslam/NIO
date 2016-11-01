package com.netty;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
           b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new DiscardServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            
           /*** ServerBootstrap c = b.group(bossGroup, workerGroup);
            ServerBootstrap d = c.channel(NioServerSocketChannel.class); // (3)
            ServerBootstrap e = d.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DiscardServerHandler());
                }
            });
            ServerBootstrap g = e.option(ChannelOption.SO_BACKLOG, 128);         // (5)
            g.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)*/

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new DiscardServer(port).run();
    }
    
    /**
     * (1).NioEventLoopGroup ��һ�����̵߳��¼�ѭ������I/O������Netty�ṩ��ͬ����Ĵ������eventloopgroup��ʵ�֡�
     * ��������������У���������Ӧ�ó����ʵ�֣���˽�ʹ����NioEventLoopGroup ����һ������������Ϊ���ϰ塱������һ����������ӡ�
     * �ڶ�����ͨ������Ϊ�����ˡ������������ܵ����ӵ�������һ���ϰ�������ӣ���ע����ܵ����ӵ����ˡ����̵߳�ʹ�ú����������ӳ�䵽����������
     * ȡ����eventloopgroupʵ�֣�������ͨ�����캯����
     * 
     * (2).new ServerBootstrap ��һ�����÷������ĸ����ࡣ������ֱ��ʹ��ͨ�����÷�������Ȼ������ע�⣬����һ�������Ĺ��̣��㲻��Ҫ���������ڴ��������¡�
     * 
     * (3).group() ���������ָ��Ҫʹ�õ�NioServerSocketChannel �����ʵ����һ���µ����������ܴ�������ӡ�
     * 
     * (4).childHandler() ����ָ���Ĵ��������Զ��һ���½��ܵ��ŵ�������������һ�������ChannelInitializer ��������ּ�ڰ����û�����һ���µ�ͨ����
     * �������п��ܵģ��������һЩ������discardserverhandlerʵʩ�������Ӧ�ó������õ���ͨ��ChannelPipeline��
     * ����Ӧ�ó����ø��ӣ����ܿ�����������Ӹ���Ĵ�����򵽹ܵ��У������ս������������ȡ��һ���������С�
     * 
     * (5).option() �����������ض����ŵ�ʵ�ֵĲ���������дһ��TCP/IP�ķ������������������������׽���ѡ����tcpnodelay��KeepAlive��
     * �����channeloption��api�ĵ��;���channelconfigʵ�ֵõ�֧�ֵ�channeloptions������
     * 
     * (6).childOption() ��ע�⵽option() ������ childOption()��������option()�ǽ��ܴ������ӵ�nioserversocketchannel��
     * childoption()�Ǹ������serverchannelͨ����nioserversocketchannel��
     * 
     * (7).��������׼�����ˡ�ʣ�µ��ǰ󶨵��˿ڲ�������������������������е������󶨵��˿�8080������ӿڿ�����
     * �����ڿ����������bind()�����ˣ��ò�ͬ�İ󶨵�ַ����
     */
}

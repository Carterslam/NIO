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
     * (1).NioEventLoopGroup 是一个多线程的事件循环处理I/O操作。Netty提供不同种类的传输各种eventloopgroup的实现。
     * 我们在这个例子中，服务器端应用程序的实现，因此将使用两NioEventLoopGroup 。第一个，经常被称为“老板”，接受一个传入的连接。
     * 第二个，通常被称为“工人”，处理所接受的连接的流量，一旦老板接受连接，并注册接受的连接到工人。多线程的使用和它们是如何映射到创建渠道，
     * 取决于eventloopgroup实现，甚至可通过构造函数。
     * 
     * (2).new ServerBootstrap 是一个设置服务器的辅助类。您可以直接使用通道设置服务器。然而，请注意，这是一个繁琐的过程，你不需要这样做，在大多数情况下。
     * 
     * (3).group() 在这里，我们指定要使用的NioServerSocketChannel 类进行实例化一个新的渠道来接受传入的连接。
     * 
     * (4).childHandler() 这里指定的处理程序将永远由一个新接受的信道进行评估。是一种特殊的ChannelInitializer 处理器，旨在帮助用户配置一个新的通道。
     * 这是最有可能的，你想添加一些程序如discardserverhandler实施你的网络应用程序配置的新通道ChannelPipeline。
     * 由于应用程序变得复杂，它很可能是您将添加更多的处理程序到管道中，并最终将这个匿名类提取到一个顶级类中。
     * 
     * (5).option() 还可以设置特定于信道实现的参数。我们写一个TCP/IP的服务器，所以我们允许设置套接字选项如tcpnodelay和KeepAlive。
     * 请参阅channeloption的api文档和具体channelconfig实现得到支持的channeloptions概述。
     * 
     * (6).childOption() 你注意到option() 方法和 childOption()方法了吗？option()是接受传入连接的nioserversocketchannel。
     * childoption()是父类接收serverchannel通道的nioserversocketchannel。
     * 
     * (7).我们现在准备好了。剩下的是绑定到端口并启动服务器。在这里，我们所有的网卡绑定到端口8080（网络接口卡）。
     * 你现在可以随意调用bind()方法了（用不同的绑定地址。）
     */
}

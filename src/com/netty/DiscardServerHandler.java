package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    	ByteBuf in = (ByteBuf)msg;
    	try{
        // Discard the received data silently.
        //((ByteBuf) msg).release(); // (3)
    		
    		//while(in.isReadable()){
    			//System.out.println((char)in.readChar());
    			System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
    			System.out.flush();
    			
    			ctx.write("response"+msg);
    			ctx.flush();
    		//}
    	}finally{
    		//ReferenceCountUtil.release(msg);
    		in.release();
    	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}

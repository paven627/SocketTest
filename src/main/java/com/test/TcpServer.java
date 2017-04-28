package com.test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class TcpServer {

	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
//							pipeline.addLast(new LineBasedFrameDecoder(80));
//							pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
							pipeline.addLast(new TcpServerHandler()); // 针对每个TCP连接创建一个新的ChannelHandler实例
						}
					});
			ChannelFuture f = b.bind(8080).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}

/**
 * 每个连接使用不同的Handler，可以保存一个变量进行计数
 */
class TcpServerHandler extends ChannelInboundHandlerAdapter {

	// 连接相关的信息直接保存在TcpServerHandler的成员变量中
	private int counter = 0;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
	    int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
	    
		SocketAddress remoteAddress = ctx.channel().remoteAddress();
		System.out.println("addr:" + remoteAddress +", host="+ host +", port="+ port);

		ByteBuf in = (ByteBuf) msg;
		 try {
		        while (in.isReadable()) { // (1)
		            System.out.print((char) in.readByte());
		            System.out.flush();
		        }
		    } finally {
		        ReferenceCountUtil.release(msg); // (2)
		    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
package com.test;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.moji.launchserver.AdCommonInterface.AdRequest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.util.ReferenceCountUtil;

public class TcpServer {
	static Logger logger = Logger.getLogger(TcpServer.class);

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
							pipeline.addLast(new HAProxyMessageDecoder());
							pipeline.addLast(new DecodeHandler());
//							pipeline.addLast(new TcpServerHandler());

							byte[] endMark = { -128, -128, -128, -128, -128 };
							ByteBuf delimter = Unpooled.copiedBuffer(endMark);
							pipeline.addLast("delimiterBasedFrameDecoder",
									new DelimiterBasedFrameDecoder(4096, delimter));
							
							pipeline.addLast("realHandler", new RealHandler());
							
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

class DecodeHandler extends ByteToMessageDecoder {

	Logger logger = Logger.getLogger(DecodeHandler.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();

		logger.info("decoder, readable = " + readableBytes + ", Thread=" + Thread.currentThread());
//		byte[] sign = new byte[readableBytes];
//		in.readBytes(sign);
//		logger.info("数据=" + new String(sign) + ", Thread=" + Thread.currentThread());
//		for (byte b : sign) {
//			System.out.print((int) b + ",");
//		}
		System.out.println();
		
//		List<String> names = ctx.pipeline().names();
//		System.out.println(names);
		ctx.pipeline().addLast( "real", new RealHandler());
		ctx.pipeline().remove(this);
//		System.out.println(ctx.pipeline().names());
	}

}


class RealHandler extends ChannelInboundHandlerAdapter {

	HAProxyMessage ha;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("22222222222222");
		if (msg instanceof HAProxyMessage) {
			ha = (HAProxyMessage) msg;
			String sourceAddress = ha.sourceAddress();
			System.out.println("1:" + sourceAddress + Thread.currentThread());
		} else {
			ByteBuf in = (ByteBuf) msg;
			int readableBytes = in.readableBytes();
			System.out.println("readable=" + readableBytes);
//			if (readableBytes != 326) {
//				System.out.println("111111111111111111111111111111");
//			}
			byte type = in.readByte();
			System.out.println("type=" + (int) type + Thread.currentThread());
			byte[] b = new byte[in.readableBytes()];
			in.readBytes(b);
			System.out.println("请求数据");
			if( ha != null) {
				System.out.println("2:" + ha.sourceAddress() + ", " + Thread.currentThread());
			}
			
			// for (byte c : b) {
			// System.out.print((int)c);
			// }

			// AdRequest adRequest = AdRequest.parseFrom(b);
			// System.out.println(adRequest);
		}

	}
}




class TcpServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {

		ByteBuf in = (ByteBuf) msg;
		try {

			byte[] b = new byte[in.readableBytes()];
			in.readBytes(b);
			for (byte c : b) {
				System.out.print((int) c + ",");
			}
			System.out.println();
			AdRequest adRequest = AdRequest.parseFrom(b);
			System.out.println(adRequest);

		} finally {
			ReferenceCountUtil.release(msg); // (2)
			// ctx.writeAndFlush(Unpooled.copiedBuffer("OK111111".getBytes()));
			// ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
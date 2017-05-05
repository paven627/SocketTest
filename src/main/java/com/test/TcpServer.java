package com.test;

import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.moji.launchserver.AdCommonInterface.AdRequest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

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
							pipeline.addLast(new DecodeHandler());

							byte[] endMark = { -128, -128, -128, -128, -128 };
							ByteBuf delimter = Unpooled.copiedBuffer(endMark);
							pipeline.addLast("delimiterBasedFrameDecoder",
									new DelimiterBasedFrameDecoder(4096, delimter));
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

	int proxy = 5;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// int readableBytes = in.readableBytes();

		// byte[] sign = new byte[5];
		// in.readBytes(sign);
		// String s = new String (sign);
		// System.out.println(s);
		// byte b = 13;
		// int indexOf = in.indexOf(0, 113, b);
		// System.out.println(indexOf);

		int rIndex = in.forEachByte(0, 108, ByteBufProcessor.FIND_CR);
		System.out.println(rIndex);
		int nIndex = in.forEachByte(rIndex + 1, 1, ByteBufProcessor.FIND_LF);
		System.out.println(nIndex);

		byte[] proxyLine = new byte[nIndex + 1];
		in.readBytes(proxyLine, 0, nIndex + 1);
		for (byte b : proxyLine) {
			System.out.print((int) b + ",");
		}
		System.out.println();

		System.out.println();
		for (byte c :proxyLine) {
			System.out.print((char) c );
		}
		String[] arr = splitToStr(proxyLine);
		for (String string : arr) {
			System.out.println(string);
		}
		byte proType = in.readByte(); // 1字节的协议类型
		System.out.println("type=" + proType);

		// readByte = in.readByte();
		// System.out.println((int)readByte);

		ctx.pipeline().addLast(new TcpServerHandler()); // 针对每个TCP连接创建一个新的ChannelHandler实例
		ctx.pipeline().remove(this);
	}

	private String[] splitToStr(byte[] proxyLine) {
		String proxy = new String (proxyLine);
		String[] split = proxy.split(" ");
		return split;
	}
	
}

/**
 * 每个连接使用不同的Handler，可以保存一个变量进行计数
 */
class TcpServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {

		ByteBuf in = (ByteBuf) msg;
		try {
			// while (in.isReadable()) { // (1)
			// System.out.print((char) in.readByte() +" ,");
			// System.out.flush();
			// }

			byte[] b = new byte[in.readableBytes()];
			in.readBytes(b);
			for (byte c : b) {
				System.out.print((int) c + ",");
			}
			System.out.println();
			AdRequest adRequest = AdRequest.parseFrom(b);
			System.out.println(adRequest);

		} finally {
			// ReferenceCountUtil.release(msg); // (2)
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
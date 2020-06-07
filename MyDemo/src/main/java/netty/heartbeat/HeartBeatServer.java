package netty.heartbeat;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 带心跳机制的Server
 *
 * @author cz
 */
public class HeartBeatServer {

	public static void main(String[] args) throws Exception {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 得到Pipeline
							ChannelPipeline pipeline = ch.pipeline();
							// 添加编解码器
							pipeline.addLast("decoder", new StringDecoder());
							pipeline.addLast("encoder", new StringEncoder());
							// IdleStateHandler的readerIdleTime参数指定超过3秒还没收到客户端的连接，
							// 会触发IdleStateEvent事件并且交给下一个handler处理，下一个handler必须
							// 实现userEventTriggered方法处理对应事件
							pipeline.addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS));
							pipeline.addLast(new HeartBeatServerHandler());
						}
					});
			System.out.println("netty server start。。");
			ChannelFuture future = bootstrap.bind(9000).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			worker.shutdownGracefully();
			boss.shutdownGracefully();
		}
	}
}
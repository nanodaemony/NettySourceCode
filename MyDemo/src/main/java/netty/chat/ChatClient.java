package netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 群聊客户端
 * @author cz
 */
public class ChatClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            // 添加客户端处理器
                            pipeline.addLast(new ChatClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            // 得到channel
            Channel channel = channelFuture.channel();
            System.out.println("========" + channel.localAddress() + "========");
            // 客户端需要输入信息,创建一个扫描器
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNextLine()) {
//                String msg = scanner.nextLine();
//                // 通过channel发送到服务器端
//                channel.writeAndFlush(msg);
//            }
            // 测试粘包
            for (int i = 0; i < 200; i++) {
                channel.writeAndFlush("Hello!NanoJava!");
            }

        } finally {
            group.shutdownGracefully();
        }
    }
}
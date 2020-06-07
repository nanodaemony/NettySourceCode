package netty.chat;

import java.text.SimpleDateFormat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 聊天室服务器处理器
 * @author cz
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * GlobalEventExecutor.INSTANCE是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
	 * 表示Channel处于就绪状态,提示上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将该客户加入聊天的信息推送给其它在线的客户端
        // 该方法会将channelGroup中所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 上线了 " + sdf.format(new
                java.util.Date())+ "\n");
        // 将当前channel加入到channelGroup中
        channelGroup.add(channel);
        System.out.println(ctx.channel().remoteAddress() + " 上线了"+ "\n");
    }

    /**
	 * 表示channel处于不活动状态,提示离线了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将客户离开信息推送给当前在线的客户
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 下线了"+ "\n");
        System.out.println(ctx.channel().remoteAddress() + " 下线了"+ "\n");
        System.out.println("ChannelGroup size=" + channelGroup.size());
    }

    /**
	 * 读取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取到当前channel
        Channel channel = ctx.channel();
        System.out.println(msg.toString());
        // 这时我们遍历channelGroup里面所有的Channel,根据不同的情况,回送不同的消息
        channelGroup.forEach(ch -> {
            // 不是当前的 channel,转发消息
            if (channel != ch) {
                ch.writeAndFlush("[客户端]" + channel.remoteAddress() + " 发送了消息：" + msg + "\n");
                // 回显自己发送的消息给自己
            } else {
                ch.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭通道
        ctx.close();
    }
}
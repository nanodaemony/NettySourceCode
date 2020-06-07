package netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端处理器
 * @author cz
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

	/**
	 * 接收到数据
     *
     * @param ctx 上下文
     * @param msg 消息
     * @throws Exception Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 打印出接收到的数据
        System.out.println(msg.trim());
    }
}
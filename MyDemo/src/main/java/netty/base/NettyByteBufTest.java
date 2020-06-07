package netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * ByteBuf测试类
 * @author cz
 */
public class NettyByteBufTest {
    public static void main(String[] args) {
        // 创建byteBuf对象，该对象内部包含一个字节数组byte[10]
        // 通过readerindex和writerIndex和capacity，将buffer分成三个区域
        // 已经读取的区域：[0,readerindex)
        // 可读取的区域：[readerindex, writerIndex)
        // 可写的区域: [writerIndex, capacity)
        ByteBuf byteBuf = Unpooled.buffer(10);
        // (ridx: 0, widx: 0, cap: 10)
        System.out.println("byteBuf=" + byteBuf);
        // 写入8个字节
        for (int i = 0; i < 8; i++) {
            byteBuf.writeByte(i);
        }
        // (ridx: 0, widx: 8, cap: 10)这里writeIndex变成了8
        System.out.println("byteBuf=" + byteBuf);
        // 读取数据
        for (int i = 0; i < 5; i++) {
            System.out.print(byteBuf.getByte(i) + " ");
        }
        // (ridx: 0, widx: 8, cap: 10) readIndex没变，调用getByte()方法不会改变readIndex没变
        System.out.println("byteBuf=" + byteBuf);

        // 再次调用读数据
        for (int i = 0; i < 5; i++) {
            System.out.print(byteBuf.readByte() + " ");
        }
        // (ridx: 5, widx: 8, cap: 10),readIndex变化，调用readByte()方法会改变readIndex
        System.out.println("byteBuf=" + byteBuf);

        // 用Unpooled工具类创建ByteBuf
        ByteBuf byteBuf2 = Unpooled.copiedBuffer("hello,Cindy!", CharsetUtil.UTF_8);
        // 使用相关的方法
        if (byteBuf2.hasArray()) {
            byte[] content = byteBuf2.array();
            // 将content转成字符串
            System.out.println(new String(content, CharsetUtil.UTF_8));
            System.out.println("byteBuf=" + byteBuf2);
            System.out.println(byteBuf2.readerIndex()); // 0
            System.out.println(byteBuf2.writerIndex()); // 12
            System.out.println(byteBuf2.capacity());    // 36

            System.out.println(byteBuf2.getByte(0));  // 获取数组0这个位置的字符h的ascii码，h=104

            int len = byteBuf2.readableBytes();         // 可读的字节数  12
            System.out.println("len=" + len);

            // 使用for取出各个字节
            for (int i = 0; i < len; i++) {
                System.out.print((char) byteBuf2.getByte(i) + " ");
            }

            // 范围读取
            System.out.println(byteBuf2.getCharSequence(0, 6, CharsetUtil.UTF_8));
            System.out.println(byteBuf2.getCharSequence(6, 6, CharsetUtil.UTF_8));
        }
    }
}

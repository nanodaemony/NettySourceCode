package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

/**
 * NIOServer
 * @author cz
 */
public class NioServer {

    //public static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        // 创建一个在本地端口进行监听的服务Socket通道.并设置为非阻塞方式，把它当成一个服务器就行了
        java.nio.channels.ServerSocketChannel serverSocketChannel = java.nio.channels.ServerSocketChannel.open();
        // 必须配置为非阻塞才能往selector上注册，否则会报错，selector模式本身就是非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        // 创建一个选择器selector
        java.nio.channels.Selector selector = java.nio.channels.Selector.open();
        // 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            System.out.println("等待事件发生。。");
            // 轮询监听channel里的key，select是阻塞的，accept()也是阻塞的，在此处阻塞了。
            int select = selector.select();
            System.out.println("有事件发生了。。");
            // 有客户端请求，被轮询监听到
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                // 删除本次已处理的key，防止下次select重复处理
                it.remove();
                // 这里可以使用线程池来处理事件：但是也可能出现并发问题
                handle(key);
            }
        }
    }

    /**
     * 处理事件:分事件进行分别处理
     *
     * @param key Key
     */
    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            System.out.println("有客户端连接事件发生了。。");
            java.nio.channels.ServerSocketChannel serverSocketChannel = (java.nio.channels.ServerSocketChannel) key.channel();
            // serverSocketChannel阻塞等到连接事件发生，然后将这个链接对应的通道socketChannel注册到多路复用器上
            // 此处是非阻塞的体现之一：调用accept是阻塞的，但是这里因为是发生了连接事件，所以这个方法会马上执行完，所以不会阻塞
            java.nio.channels.SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            // 通过Selector监听Channel时对读事件感兴趣
            socketChannel.register(key.selector(), SelectionKey.OP_READ);

            // 读事件
        } else if (key.isReadable()) {
            System.out.println("有客户端数据可读事件发生了。。");
            // 注意这个channel跟上面不一样！
            java.nio.channels.SocketChannel socketChannel = (java.nio.channels.SocketChannel) key.channel();
            // 分配数据缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // NIO非阻塞体现:首先read方法不会阻塞，其次这种事件响应模型，当调用到read方法时肯定是发生了客户端发送数据的事件
            int len = socketChannel.read(buffer);
            if (len != -1) {
                System.out.println("读取到客户端发送的数据：" + new String(buffer.array(), 0, len));
            }
            ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
            socketChannel.write(bufferToWrite);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else if (key.isWritable()) {
            System.out.println("Write事件");
            // 服务端的write事件就是写数据给客户端
            java.nio.channels.SocketChannel socketChannel = (java.nio.channels.SocketChannel) key.channel();
            // NIO事件触发是水平触发
            // 使用Java的NIO编程的时候，在没有数据可以往外写的时候要取消写事件，
            // 在有数据往外写的时候再注册写事件
            key.interestOps(SelectionKey.OP_READ);
            //sc.close();
        }
    }
}
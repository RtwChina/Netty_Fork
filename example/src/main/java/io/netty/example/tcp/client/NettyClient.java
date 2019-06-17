package io.netty.example.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.tcp.handler.SimpleClientHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ResourceLeakDetector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.omg.PortableServer.THREAD_POLICY_ID;

/**
 * @author rtw
 * @since 2019/2/24
 */
public class NettyClient {

    final Bootstrap b = new Bootstrap(); // (1)

    static ChannelFuture f = null;


    public NettyClient() {

    }

    public static void main(String[] args) throws Exception{
        NettyClient nettyClient = new NettyClient();
        nettyClient.beginClient();
        f.await();
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(()->{
            while (true) {
                if (f == null) {
                    return;
                }
                System.out.println("发送你好");
//                f.channel().writeAndFlush("hello Server2\r\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("搞点什么啊");
    }



    public void beginClient() throws Exception {
        new Thread(()->{
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            String host = "localhost";
            int port = 8080;
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                b.group(workerGroup); // (2)
                b.channel(NioSocketChannel.class); // (3)
                b.handler(new ChannelInitializer<SocketChannel>() {// (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleClientHandler());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
                // Start the client.
                f = b.connect(host, port).sync(); // (5)
                f.channel().writeAndFlush("hello Server\r\n");
                // Wait until the connection is closed.
                f.channel().closeFuture().sync();
                System.out.println("client 通道关闭");
            } catch (Exception e) {
                System.out.println("Client启动异常，e={}" + e.toString());
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
        Thread.sleep(1000);
    }

}

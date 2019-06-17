package io.netty.example.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.tcp.handler.SimpleServerHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rtw
 * @since 2018/11/9
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);


    private int port = 8080;

    public void beginServer() {
        logger.error("222");

        EventLoopGroup bossGroup = new NioEventLoopGroup(); //1
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            ServerBootstrap b = new ServerBootstrap(); //2
            b.group(bossGroup, workerGroup)
                    // 队列等待数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 心跳包
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class) //3
                    .childHandler(new ChannelInitializer<SocketChannel>() { //4
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 分隔符解码: Delimiters.lineDelimiter()[0] ==  '\r', '\n'
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                            // 字符串解码
                            ch.pipeline().addLast(new StringDecoder());
                            // 心跳
//                            ch.pipeline().addLast(new IdleStateHandler(10, 10, 10, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new SimpleServerHandler());
                            // 字符串编码
                            ch.pipeline().addLast(new StringEncoder());

                        }
                    });
            ChannelFuture f = b.bind(port).sync(); //5
//            CuratorFramework client = ZookeeperFactory.create();
//            InetAddress inetAddress = InetAddress.getLocalHost();
            // 去Zookeper注册一个临时的文件夹，文件名为当前server地址,当server与Zk的回话结束时，该节点就会自己消失。
//            client.create().withMode(CreateMode.EPHEMERAL).forPath(Constants.SERVER_PATH + inetAddress.getHostAddress());
            logger.info("开始监听{}端口", port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer().beginServer();
    }
}

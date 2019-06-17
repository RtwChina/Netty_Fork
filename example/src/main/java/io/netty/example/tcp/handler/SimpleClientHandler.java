package io.netty.example.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtw
 * @since 2019/2/24
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg.toString().equals("ping")){
            ctx.channel().writeAndFlush("ping\r\n");
        }
        System.out.println(msg.toString());
        super.channelRead(ctx, msg);
    }

    /**
     *
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}

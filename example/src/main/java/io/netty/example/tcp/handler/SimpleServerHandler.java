package io.netty.example.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * @author rtw
 * @since 2019/2/14
 */
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.channel().writeAndFlush("is OK\r\n");
        ReferenceCountUtil.release(msg);

    }

    /**
     * 当有Event被触发时会调用
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("读空闲===");
                ctx.channel().close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("写空闲===");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("都空闲===");
//                ctx.channel().writeAndFlush("ping \r\n");

            }
        }
        super.userEventTriggered(ctx, evt);
    }
}

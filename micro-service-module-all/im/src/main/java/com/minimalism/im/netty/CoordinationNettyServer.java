//package com.minimalism.im.netty;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpServerCodec;
//import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
//import io.netty.handler.stream.ChunkedWriteHandler;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.ObjectUtils;
//
//import javax.annotation.Resource;
//
//@Data
//@Slf4j
//@Configuration
//@ConfigurationProperties(prefix = "netty")
//public class CoordinationNettyServer {
//
//    private Integer port;
//    private Integer maxContentLength;
//    private String websocketPath;
//    private String subprotocols;
//    private Boolean allowExtensions;
//    private Integer maxFrameSize;
//
//    @Resource
//    private CoordinationSocketHandler coordinationSocketHandler;
//
//    public void start() throws Exception {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            int port = ObjectUtils.isEmpty(this.port) ? 8005 : this.port;
//            int maxContentLength = ObjectUtils.isEmpty(this.maxContentLength) ? 8191 : this.maxContentLength;
//            String websocketPath = ObjectUtils.isEmpty(this.websocketPath) ? "/ws1" : this.websocketPath;
//            String subprotocols = ObjectUtils.isEmpty(this.subprotocols) ? "WebSocket1" : this.subprotocols;
//            Boolean allowExtensions = ObjectUtils.isEmpty(this.allowExtensions) ? false : this.allowExtensions;
//            Integer maxFrameSize = ObjectUtils.isEmpty(this.maxFrameSize) ? 65535 * 10 : this.maxFrameSize;
//            log.info("[port:{},maxContentLength:{},websocketPath:{},subprotocols:{},allowExtensions:{},maxFrameSize:{}]"
//                    , port, maxContentLength, websocketPath, subprotocols, allowExtensions, maxFrameSize);
//
//
//            ServerBootstrap sb = new ServerBootstrap();
//            sb.option(ChannelOption.SO_BACKLOG, 1024);
//            sb.group(group, bossGroup) // 绑定线程池
//                    .channel(NioServerSocketChannel.class) // 指定使用的channel
//                    .localAddress(port)// 绑定监听端口
//                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
//                            ch.pipeline().addLast(new HttpServerCodec());
//                            //以块的方式来写的处理器
//                            ch.pipeline().addLast(new ChunkedWriteHandler());
//                            ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
//                            ch.pipeline().addLast(new WebSocketServerProtocolHandler(websocketPath, subprotocols, allowExtensions, maxFrameSize));
//                            ch.pipeline().addLast(coordinationSocketHandler);//自定义消息处理类
//                        }
//                    });
//            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
//            log.info("{} 已启动，正在监听： {}", getClass().getName(), cf.channel().localAddress());
//            cf.channel().closeFuture().sync(); // 关闭服务器通道
//        } finally {
//            group.shutdownGracefully().sync(); // 释放线程池资源
//            bossGroup.shutdownGracefully().sync();
//        }
//    }
//}

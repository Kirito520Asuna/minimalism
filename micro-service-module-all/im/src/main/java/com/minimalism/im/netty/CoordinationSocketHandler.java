//package com.minimalism.im.netty;
//
//
//import com.alibaba.fastjson.JSON;
//import com.minimalism.enums.im.NettyMessageType;
//import com.minimalism.im.domain.netty.NettyMessage;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.channel.group.DefaultChannelGroup;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.util.concurrent.GlobalEventExecutor;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Configuration
//@ChannelHandler.Sharable @Slf4j
//public class CoordinationSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
//
//    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
//
//    public Map<String, Channel> cmap = new HashMap<>();
//    private String user = "user";
//    private String uid = "uid";
//    private String to = "to";
//    private String message = "message";
//    private String type = "type";
//
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.info("与客户端建立连接，通道开启！");
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//       log.info("与客户端断开连接，通道关闭！");
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
//        //接收的消息
//        Map map = JSON.parseObject(msg.text(), Map.class);
//        String type = map.get(this.type).toString();
//        if (NettyMessageType.LOGIN.getType().equals(type)) {
//            // 登录
//            websocketLogin(map, ctx);
//        } else if (NettyMessageType.SEND.getType().equals(type)) {
//            // 发送对话消息
//            send(map);
//        } else if (NettyMessageType.CALL.getType().equals(type)) {
//            // 请求进行视频通话
//            call(map);
//        } else if (NettyMessageType.RESPONSE.getType().equals(type)) {
//            // 对方回应通话
//            response(map);
//        } else if (NettyMessageType.ASKOFF.getType().equals(type)) {
//            //传递请求off
//            askOff(map);
//        } else if (NettyMessageType.GETOFF.getType().equals(type)) {
//            //获取off
//            getOff(map);
//        } else if (NettyMessageType.ANSWER.getType().equals(type)) {
//            answer(map);
//        } else if (NettyMessageType.CANDIDATE.getType().equals(type)) {
//            candidate(map);
//        }
//        System.out.println(String.format("收到客户端%s的数据：%s", ctx.channel().id(), msg.text()));
////        channelGroup.writeAndFlush(new TextWebSocketFrame("aaabbb"));
//    }
//
//    private void answer(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.ANSWER.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//  /*          Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 7); // 回应通话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            obj.put("message", map.get("message"));
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//    private void nettyMessage(Channel channel,NettyMessage nettyMessage){
//        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(nettyMessage)));
//    }
//    private void candidate(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.CANDIDATE.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//        /*    Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 8); // 回应通话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            obj.put("message", map.get("message"));
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//    private void getOff(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.GETOFF.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//         /*   Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 6); // 回应通话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            obj.put("message", map.get("message"));
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//
//    private void askOff(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.ASKOFF.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//            /*Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 5); // 回应通话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//    private void response(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.RESPONSE.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//            /*Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 4); // 回应通话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//    private void call(Map map) {
//        String to = map.get(this.to).toString();
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setTo(to);
//            nettyMessage.setFrom(map.get(uid).toString());
//            nettyMessage.setType(NettyMessageType.CALL.getType());
//            nettyMessage.setMessage(map.get(message).toString());
//            nettyMessage(channel,nettyMessage);
//
//            /*Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 3); // 对话
//            obj.put("uuid", UUID.randomUUID().toString());
//            obj.put("from", map.get("uid").toString());
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//    private void send(@NotNull Map map) {
//        String to = map.get(this.to).toString();
//        System.out.println(map + "map");
//        System.out.println(cmap + "cmap");
//        if (cmap.containsKey(user + to)) {
//            Channel channel = cmap.get(user + to);
//
//            NettyMessage nettyMessage = new NettyMessage();
//            nettyMessage.setType(NettyMessageType.RESPONSE.getType());
//            nettyMessage(channel,nettyMessage);
//
//           /* Map<String, Object> obj = new HashMap<>();
//            obj.put("type", 2); // 对话
//            obj.put("uuid", UUID.randomUUID().toString());
//            System.out.println("111111111");
//            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(obj)));
//            System.out.println("22222222222");*/
//        } else {
//            log.error("未登录");
//        }
//    }
//
//    private void websocketLogin(Map map,ChannelHandlerContext ctx) {
//        String uid = map.get(this.uid).toString();
//        cmap.put(user + uid, ctx.channel());
//        log.error(map + "map");
//        log.error(uid + "登录");
//    }
//
//    private void sendMessage(ChannelHandlerContext ctx) throws InterruptedException {
//        String message = "我是服务器，你好呀";
//        ctx.writeAndFlush(new TextWebSocketFrame("hello"));
//    }
//
//
//}
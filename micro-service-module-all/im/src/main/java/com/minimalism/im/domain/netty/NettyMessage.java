//package com.minimalism.im.domain.netty;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.util.UUID;
//
///**
// * @Author yan
// * @Date 2023/8/18 0018 17:05
// * @Description
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class NettyMessage implements Serializable {
//    @Schema(description = "接收")
//    private String to;
//    @Schema(description = "发送")
//    private String from;
//    @Schema(description = "消息类型")
//    private String type;
//    @Schema(description = "唯一值")
//    private String uuid = UUID.randomUUID().toString();
//    @Schema(description = "消息")
//    private String message ;
//}

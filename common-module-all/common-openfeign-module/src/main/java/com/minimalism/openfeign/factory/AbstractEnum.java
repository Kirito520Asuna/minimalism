package com.minimalism.openfeign.factory;

import com.minimalism.openfeign.ApiOpenfeignClientEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2024/5/14 0014 11:46
 * @Description
 */
@Getter @AllArgsConstructor
public enum AbstractEnum {
    DEFAULT, ORDER("order","订单模块"),
    OPENFEIGN_ONE("open-one")
    ;
    String serverName;
    String desc;
    ApiOpenfeignClientEnum apiSalt;
    private void apiSalt() {
        this.apiSalt = ApiOpenfeignClientEnum.DEFAULT;
    }
    AbstractEnum() {
        this.serverName = "DEFAULT";
        this.desc = "默认模块";
        apiSalt();
    }
    AbstractEnum(String serverName) {
        this.serverName = serverName;
        this.desc = serverName + "模块";
        apiSalt();
    }
    AbstractEnum(String serverName, String desc) {
        this.serverName = serverName;
        this.desc = desc;
        apiSalt();
    }
}

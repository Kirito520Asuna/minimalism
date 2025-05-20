package com.minimalism.mp.handler;

import com.minimalism.abs.handler.AbstractEntityHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;


/**
 * @Author yan
 * @Date 2024/5/22 0022 17:40
 * @Description
 */
@Component
public class EntityHandler implements AbstractEntityHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = getUserId();
        AbstractEntityHandler.super.insertFill(metaObject);
        this.strictInsertFill(metaObject, "createBy", () -> userId, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String userId = getUserId();
        AbstractEntityHandler.super.updateFill(metaObject);
        this.strictInsertFill(metaObject, "updateBy", () -> userId, String.class);
    }


}

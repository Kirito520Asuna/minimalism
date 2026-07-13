package com.minimalism.mp.handler;

import com.minimalism.mp.abs.handler.AbsEntityHandler;
import com.minimalism.mp.pojo.AutoEntity;
import com.minimalism.mp.pojo.BaseEntity;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @Author yan
 * @Date 2024/5/22 0022 17:40
 * @Description
 */
@Component
@ConditionalOnMissingBean(AbsEntityHandler.class)
public class EntityHandler implements AbsEntityHandler {
    @Override
    public List<AutoEntity> getAutoEntityList() {
        String userId = getUserId();
        List<AutoEntity> list = AbsEntityHandler.super.getAutoEntityList();
        list.add(new AutoEntity(BaseEntity.COL_CREATE_BY, userId, String.class, true, false));
        list.add(new AutoEntity(BaseEntity.COL_UPDATE_BY, userId, String.class, true, true));
        return list;
    }
}

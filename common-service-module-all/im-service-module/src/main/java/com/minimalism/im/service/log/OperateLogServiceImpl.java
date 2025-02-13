package com.minimalism.im.service.log;

import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.service.AbstractOperateLogService;
import com.minimalism.pojo.OperateLogInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2023/10/31 0031 15:22
 * @Description
 */
@Slf4j
@Service
public class OperateLogServiceImpl implements AbstractOperateLogService {
    @Override
    public void createOperateLog(OperateLogInfo operateLog) {
        AbstractOperateLogService.super.createOperateLog(operateLog);
    }

    @Override
    public void updateOperateLog(OperateLogInfo operateLog) {
        AbstractOperateLogService.super.updateOperateLog(operateLog);
    }

}

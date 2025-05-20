package com.minimalism.im.service.log;

import com.minimalism.aop.abs.service.AbsOperateLogService;
import com.minimalism.aop.pojo.OperateLogInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2023/10/31 0031 15:22
 * @Description
 */
@Slf4j
@Service
public class OperateLogServiceImpl implements AbsOperateLogService {
    @Override
    public void createOperateLog(OperateLogInfo operateLog) {
        AbsOperateLogService.super.createOperateLog(operateLog);
    }

    @Override
    public void updateOperateLog(OperateLogInfo operateLog) {
        AbsOperateLogService.super.updateOperateLog(operateLog);
    }

}

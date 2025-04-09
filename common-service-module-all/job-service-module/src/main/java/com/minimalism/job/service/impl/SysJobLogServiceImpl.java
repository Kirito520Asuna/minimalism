package com.minimalism.job.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.job.mapper.SysJobLogMapper;
import com.minimalism.job.domain.SysJobLog;
import com.minimalism.job.service.SysJobLogService;
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService{

}

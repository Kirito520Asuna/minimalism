package com.minimalism.job.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.job.mapper.SysJobMapper;
import com.minimalism.job.domain.SysJob;
import com.minimalism.job.service.SysJobService;
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService{

}

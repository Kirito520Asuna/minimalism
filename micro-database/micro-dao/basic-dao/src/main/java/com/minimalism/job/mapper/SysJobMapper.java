package com.minimalism.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.job.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
    /**
     * 查询定时任务列表
     * @param job
     * @return
     */
    List<SysJob> selectJobList(SysJob job);
}
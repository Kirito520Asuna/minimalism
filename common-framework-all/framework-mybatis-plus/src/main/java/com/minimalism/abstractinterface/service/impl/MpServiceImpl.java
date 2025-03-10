package com.minimalism.abstractinterface.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.abstractinterface.mapper.MpMapper;
import com.minimalism.abstractinterface.service.MpIService;

/**
 * @Author yan
 * @Date 2025/3/10 17:26:44
 * @Description
 */
public class MpServiceImpl<M extends MpMapper<T>, T> extends ServiceImpl<M,T> implements MpIService<T> {
}

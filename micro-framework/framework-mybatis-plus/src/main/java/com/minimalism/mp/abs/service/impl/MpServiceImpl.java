package com.minimalism.mp.abs.service.impl;

import com.minimalism.mp.abs.mapper.MpMapper;
import com.minimalism.mp.abs.service.MpIService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Author yan
 * @Date 2025/3/10 17:26:44
 * @Description
 */
public class MpServiceImpl<M extends MpMapper<T>, T> extends ServiceImpl<M,T> implements MpIService<T> {
}

package com.minimalism.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.user.domain.SysUserRole;
import com.minimalism.user.mapper.SysUserRoleDao;
import com.minimalism.user.service.SysUserRoleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements SysUserRoleService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysUserRole> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysUserRole> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysUserRole> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysUserRole> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysUserRole record) {
        return baseMapper.insertOrUpdate(record);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysUserRole record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}

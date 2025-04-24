package com.minimalism.dept.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.minimalism.common.service.CommonUserService;
import com.minimalism.dept.domain.SysUserDept;
import com.minimalism.dept.mapper.SysUserDeptMapper;
import com.minimalism.dept.service.SysUserDeptService;
import com.minimalism.exception.BusinessException;
import com.minimalism.mp.aop.dataScope.DataScope;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;

import com.minimalism.dept.mapper.SysDeptMapper;
import com.minimalism.dept.domain.SysDept;
import com.minimalism.dept.service.SysDeptService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysDept> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchUseMultiQuery(List<SysDept> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysDept> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysDept> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysDept> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysDept record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysDept record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        return baseMapper.selectSysDeptList(dept);
    }

    @Override
    public void checkDeptDataScope(List<Long> deptIds) {
        for (Long deptId : deptIds) {
            checkDeptDataScope(deptId);
        }
    }
    @Override
    public void checkDeptDataScope(Long deptId) {
        boolean isAdmin = SpringUtil.getBean(CommonUserService.class).isAdmin();
        if (!isAdmin && ObjectUtils.isNotNull(deptId)) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> deptList = SpringUtil.getBean(getClass()).selectDeptList(dept);
            if (CollUtil.isEmpty(deptList)) {
                throw new BusinessException("没有权限访问部门数据！");
            }
        }
    }

    @Override
    public boolean hasChildByDeptIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDelFlag, "0")
                .in(SysDept::getParentId, deptIds)
                .last(" limit 1");
        int count = count(query);
        return count > 0;
    }

    @Override
    public boolean checkDeptExistUser(List<Long> deptIds) {
        List<SysUserDept> sysUserDeptList = new ArrayList<>();
        List<SysUserDept> userDeptList = SpringUtil.getBean(SysUserDeptService.class)
                .getByIds(deptIds);
        sysUserDeptList = SpringUtil.getBean(SysUserDeptMapper.class)
                .selectBatchIds(userDeptList.stream().map(SysUserDept::getUserId).collect(Collectors.toList()));
        return CollUtil.isNotEmpty(sysUserDeptList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByDeptIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDelFlag, "2")
                .in(SysDept::getDeptId, deptIds);
        return remove(query);
    }
}

package com.minimalism.dept.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.dept.domain.SysUserDept;
import com.minimalism.dept.mapper.SysUserDeptMapper;
import com.minimalism.dept.service.SysUserDeptService;
@Service
public class SysUserDeptServiceImpl extends ServiceImpl<SysUserDeptMapper, SysUserDept> implements SysUserDeptService{

}

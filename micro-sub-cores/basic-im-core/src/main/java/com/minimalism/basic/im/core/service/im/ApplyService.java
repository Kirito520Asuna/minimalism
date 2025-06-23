package com.minimalism.basic.im.core.service.im;


import com.minimalism.basic.core.vo.user.UserVo;
import com.minimalism.im.domain.im.Apply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
public interface ApplyService extends IService<Apply> {


    /**
     * @param apply
     * @return
     */
    UserVo applyAgree(Apply apply);
}

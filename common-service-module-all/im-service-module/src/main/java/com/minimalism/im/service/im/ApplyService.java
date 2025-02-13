package com.minimalism.im.service.im;


import com.baomidou.mybatisplus.extension.service.IService;
import com.minimalism.im.domain.im.Apply;
import com.minimalism.vo.user.UserVo;

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

package com.minimalism.tomapper;

import com.minimalism.dto.SysMenuDto;
import com.minimalism.user.domain.SysMenu;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.vo.SysMenuTreeVo;

/**
 * @Author yan
 * @Date 2024/10/3 下午8:49:16
 * @Description
 */
public class SysMenuMapper {
    public static SysMenuTreeVo mapperToTreeVo(SysMenu sysMenu) {
        SysMenuTreeVo sysMenuTreeVo = new SysMenuTreeVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenu, sysMenuTreeVo);
        return sysMenuTreeVo;
    }

    public static SysMenuDto mapperToDto(SysMenu sysMenu) {
        SysMenuDto sysMenuDto = new SysMenuDto();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenu, sysMenuDto);
        return sysMenuDto;
    }

    public static SysMenu mapperToDao(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = new SysMenu();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenuDto, sysMenu);
        return sysMenu;
    }
}

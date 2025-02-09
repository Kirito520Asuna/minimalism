import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.aop.tree.Tree;
import com.minimalism.aop.tree.TreeMap;
import com.minimalism.utils.tree.TreeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/3 下午8:07:48
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysMenuTreeVo  implements AbstractBean {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单ID")
    @Tree(id = true)
    private Long menuId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    @Schema(description = "父菜单ID")
    @Tree(parentId = true )
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField(value = "`path`")
    @Schema(description = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @TableField(value = "component")
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由参数
     */
    @TableField(value = "query")
    @Schema(description = "路由参数")
    private String query;

    /**
     * 路由名称
     */
    @TableField(value = "route_name")
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    @TableField(value = "is_frame")
    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @TableField(value = "is_cache")
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @TableField(value = "menu_type")
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @TableField(value = "visible")
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;

    /**
     * 权限标识
     */
    @TableField(value = "perms")
    @Schema(description = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by")
    @Schema(description = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    @Tree(subset = true)
    List<SysMenuTreeVo> children = CollUtil.newArrayList();

    @Override
    public void init() {
        AbstractBean.super.init();
    }

    public static void main(String[] args) {
        ArrayList<SysMenuTreeVo> sysMenus = CollUtil.newArrayList(
                new SysMenuTreeVo().setMenuId(1l).setParentId(null),
                new SysMenuTreeVo().setMenuId(2l).setParentId(1l),
                new SysMenuTreeVo().setMenuId(3l).setParentId(1l),
                new SysMenuTreeVo().setMenuId(4l).setParentId(1l),

                new SysMenuTreeVo().setMenuId(5l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(6l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(7l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(8l).setParentId(2l),

                new SysMenuTreeVo().setMenuId(5l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(6l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(7l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(8l + 4).setParentId(3l),

                new SysMenuTreeVo().setMenuId(8l + 4 + 1).setParentId(3l + 1)
        );

        List<Map<String, Object>> maps = TreeUtils.listToMapList(sysMenus);
        Class<SysMenuTreeVo> tClass = SysMenuTreeVo.class;

        List<SysMenuTreeVo> treeVos = TreeUtils.mapsToTree(maps, tClass, true, TreeMap.getTreeFieldMaps(tClass));
        System.err.println(JSONUtil.toJsonStr(treeVos));
    }
}

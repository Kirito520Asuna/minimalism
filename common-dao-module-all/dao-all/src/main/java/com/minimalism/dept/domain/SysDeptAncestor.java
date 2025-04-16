package com.minimalism.dept.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalism.mp.pojo.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 部门祖先表
 */
@Schema(description = "部门祖先表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_dept_ancestor")
public class SysDeptAncestor implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    /**
     * ID
     */
    @TableField(value = "dept_id")
    @Schema(description = "ID")
    private Long deptId;

    /**
     * 上级ID
     */
    @TableField(value = "dept_parent_id")
    @Schema(description = "上级ID")
    private Long deptParentId;

    /**
     * 第几级祖先 从自身往上数 0->
     */
    @TableField(value = "`level`")
    @Schema(description = "第几级祖先 从自身往上数 0->")
    private Long level;

    public static final String COL_ID = "id";

    public static final String COL_DEPT_ID = "dept_id";

    public static final String COL_DEPT_PARENT_ID = "dept_parent_id";

    public static final String COL_LEVEL = "level";
}
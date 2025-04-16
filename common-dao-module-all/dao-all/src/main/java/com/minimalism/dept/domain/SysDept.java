package com.minimalism.dept.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalism.mp.pojo.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 部门表
 */
@Schema(description="部门表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_dept")
public class SysDept extends BaseEntity implements Serializable {
    /**
     * 部门id
     */
    @TableId(value = "dept_id", type = IdType.AUTO)
    @Schema(description="部门id")
    private Long deptId;

    /**
     * 父部门id
     */
    @TableField(value = "parent_id")
    @Schema(description="父部门id")
    private Long parentId;

    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    @Schema(description="部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    @Schema(description="显示顺序")
    private Integer orderNum;

    /**
     * 负责人
     */
    @TableField(value = "leader")
    @Schema(description="负责人")
    private String leader;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    @Schema(description="联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    @Schema(description="邮箱")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description="部门状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description="删除标志（0代表存在 2代表删除）")
    private String delFlag;

    public static final String COL_DEPT_ID = "dept_id";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_DEPT_NAME = "dept_name";

    public static final String COL_ORDER_NUM = "order_num";

    public static final String COL_LEADER = "leader";

    public static final String COL_PHONE = "phone";

    public static final String COL_EMAIL = "email";

    public static final String COL_STATUS = "status";

    public static final String COL_DEL_FLAG = "del_flag";
}
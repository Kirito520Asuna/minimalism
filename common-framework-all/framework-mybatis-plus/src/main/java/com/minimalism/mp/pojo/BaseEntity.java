package com.minimalism.mp.pojo;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity基类
 *
 * @author yan
 */

@Data @SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity extends SearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField(value = "`create_by`")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @TableField(value = "`create_time`", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @Schema(description = "更新时间")
    @TableField(value = "`update_by`")
    private String updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField(value = "`remark`")
    private String remark;

    public static final String COL_CREATE_BY = "create_by";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_BY = "update_by";
    public static final String COL_UPDATE_TIME = "update_time";
    public static final String COL_REMARK = "remark";

}

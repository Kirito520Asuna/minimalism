package com.minimalism.pojo;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.minimalism.abstractinterface.entity.AbstractEntity;
import com.minimalism.util.ObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 *
 * @author yan
 */

@Data @SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable, AbstractEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;

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

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
    public Map<String,Object> toParams(){
        return toParams(ObjectUtils.defaultIfEmpty(getParams(), this));
    }

    public static final String COL_CREATE_BY = "create_by";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_BY = "update_by";
    public static final String COL_UPDATE_TIME = "update_time";
    public static final String COL_REMARK = "remark";

}

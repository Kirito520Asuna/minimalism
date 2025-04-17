package com.minimalism.mp.pojo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity基类
 *
 * @author yan
 */

@Data
@Slf4j
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity extends SearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    //@TableField(value = "`create_by`", fill = FieldFill.INSERT)
    @TableField(value = COL_CREATE_BY, fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    //@TableField(value = "`create_time`", fill = FieldFill.INSERT)
    @TableField(value = COL_CREATE_TIME, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @Schema(description = "更新者")
    //@TableField(value = "`update_by`", fill = FieldFill.UPDATE)
    @TableField(value = COL_UPDATE_BY, fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    //@TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    @TableField(value = COL_UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    //@TableField(value = "`remark`")
    @TableField(value = COL_REMARK)
    private String remark;

    public static final String COL_CREATE_BY = "create_by";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_BY = "update_by";
    public static final String COL_UPDATE_TIME = "update_time";
    public static final String COL_REMARK = "remark";

    protected void buildEntity(Map<String, Object> buildMap) {
        log.warn("buildEntity is not implemented buildMap:{}", buildMap);
    }

    protected void buildEntity(Object obj) {
        log.debug("execute buildEntity method");
        buildEntity(BeanUtil.beanToMap(obj));
    }

    public void buildEntity() {
        buildEntity(this);
    }

}

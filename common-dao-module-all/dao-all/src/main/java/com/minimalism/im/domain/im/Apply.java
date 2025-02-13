package com.minimalism.im.domain.im;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.view.BaseJsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author yan
 * @Date 2023/8/7 0007 10:39
 * @Description
 */
@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`apply`")
public class Apply implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "")
    @NotNull(message = "不能为null",groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.UpdateView.class})
    private Long id;

    @TableField(value = "`uid`")
    @Schema(description = "申请人")
    @NotNull(message = "不能为null",groups = {BaseJsonView.ApplyView.class,BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.ApplyView.class,BaseJsonView.UpdateView.class})
    private Long uid;

    @TableField(value = "`tid`")
    @Schema(description = "申请添加人")
    @NotNull(message = "不能为null",groups = {BaseJsonView.ApplyView.class})
    @JsonView(value = {BaseJsonView.ApplyView.class})
    private Long tid;
    @TableField(exist = false)
    @Schema(description = "是否同意添加")
    @NotNull(message = "不能为null",groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.UpdateView.class})
    private Boolean agree = false;

    private static final long serialVersionUID = -5650386246274539633L;
}
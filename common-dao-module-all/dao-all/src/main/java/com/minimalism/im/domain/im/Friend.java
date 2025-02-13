package com.minimalism.im.domain.im;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "`friend`")
public class Friend implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "")
    @NotNull(message = "不能为null")
    private Long id;

    @TableField(value = "`uid`")
    @Schema(description = "")
    private Long uid;

    @TableField(value = "`fid`")
    @Schema(description = "")
    private Long fid;

    private static final long serialVersionUID = -4412818290561333867L;
}
package com.minimalism.file.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @Author yan
 * @Date 2025/3/6 上午12:50:38
 * @Description
 */

/**
 * 文件分片表
 */
@Schema(description = "文件分片表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "file_part")
public class FilePart implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "part_id", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long partId;

    /**
     * code用于合并文件
     */
    @TableField(value = "part_code")
    @Schema(description = "code用于合并文件")
    private String partCode;

    /**
     * 文件编号
     */
    @TableField(value = "file_id")
    @Schema(description = "文件编号")
    private Long fileId;

    /**
     * 资源路径
     */
    @TableField(value = "url")
    @Schema(description = "资源路径")
    private String url;

    /**
     * 分片大小
     */
    @TableField(value = "part_size")
    @Schema(description = "分片大小")
    private Long partSize;

    /**
     * 分片顺序
     */
    @TableField(value = "part_sort")
    @Schema(description = "分片顺序")
    private Integer partSort;

    private static final long serialVersionUID = 1L;

    public static final String COL_PART_ID = "part_id";

    public static final String COL_PART_CODE = "part_code";

    public static final String COL_FILE_ID = "file_id";

    public static final String COL_URL = "url";

    public static final String COL_PART_SIZE = "part_size";

    public static final String COL_PART_SORT = "part_sort";
}
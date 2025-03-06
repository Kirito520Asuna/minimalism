package com.minimalism.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @Author yan
 * @Date 2025/3/6 14:19:22
 * @Description
 */

/**
 * 文件信息表
 */
@Schema(description = "文件信息表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "file_info")
public class FileInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long fileId;

    /**
     * 资源路径
     */
    @TableField(value = "url")
    @Schema(description = "资源路径")
    private String url;

    /**
     * 资源原始名称
     */
    @TableField(value = "`name`")
    @Schema(description = "资源原始名称")
    private String name;

    /**
     * 资源名称
     */
    @TableField(value = "file_name")
    @Schema(description = "资源名称")
    private String fileName;

    /**
     * 后缀名
     */
    @TableField(value = "suffix")
    @Schema(description = "后缀名")
    private String suffix;

    /**
     * 是否图片
     */
    @TableField(value = "is_img")
    @Schema(description = "是否图片")
    private Boolean isImg;

    /**
     * 尺寸
     */
    @TableField(value = "`size`")
    @Schema(description = "尺寸")
    private Long size;

    /**
     * 文件展示类型，非后缀名
     */
    @TableField(value = "`type`")
    @Schema(description = "文件展示类型，非后缀名")
    private String type;

    /**
     * 是否目录
     */
    @TableField(value = "is_dir")
    @Schema(description = "是否目录")
    private Boolean isDir;

    @TableField(value = "parent_id")
    @Schema(description = "")
    private Long parentId;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
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
    @TableField(value = "update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    private static final long serialVersionUID = 1L;

    public static final String COL_FILE_ID = "file_id";

    public static final String COL_URL = "url";

    public static final String COL_NAME = "name";

    public static final String COL_FILE_NAME = "file_name";

    public static final String COL_SUFFIX = "suffix";

    public static final String COL_IS_IMG = "is_img";

    public static final String COL_SIZE = "size";

    public static final String COL_TYPE = "type";

    public static final String COL_IS_DIR = "is_dir";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_CREATE_BY = "create_by";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_BY = "update_by";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_REMARK = "remark";
}
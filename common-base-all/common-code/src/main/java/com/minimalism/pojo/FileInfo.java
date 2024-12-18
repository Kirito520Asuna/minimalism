package com.minimalism.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/8/12 0012 10:21:13
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FileInfo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    static class FileBase{
        private Integer id;
        @Schema(description = "唯一code")
        private String code;
        @Schema(description = "文件名")
        private String fileName;
        @Schema(description = "后缀名")
        private String suffix;
    }
    private Integer id;
    @Schema(description = "文件基本信息")
    private FileBase fileBase;
    @Schema(description = "文件类型 Part-分片类型,Common-通用类型")
    private String type;
    @Schema(description = "url")
    private String url;
    @Schema(description = "顺序")
    private Integer sort;
}

package com.minimalism.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2025/3/6 15:32:25
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PartVo {
    @Schema(description = "url")
    private String url;
    @Schema(description = "本地资源")
    private String localResource;
    @Schema(description = "文件唯一值")
    private String identifier;
    @Schema(description = "是否本地资源")
    private Boolean local = Boolean.FALSE;
}

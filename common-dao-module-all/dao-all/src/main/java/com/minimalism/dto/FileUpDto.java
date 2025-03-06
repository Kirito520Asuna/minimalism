package com.minimalism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2025/3/6 上午1:13:42
 * @Description
 */
@NoArgsConstructor
@Data
public class FileUpDto implements Serializable {
    private static final long serialVersionUID = 5360609118081008199L;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("suffix")
    private String suffix;
    @JsonProperty("isImg")
    private Boolean isImg = Boolean.FALSE;
    @JsonProperty("size")
    private Long size;
    @JsonProperty("type")
    private String type;
    @JsonProperty("isDir")
    private Boolean isDir = Boolean.TRUE;
}

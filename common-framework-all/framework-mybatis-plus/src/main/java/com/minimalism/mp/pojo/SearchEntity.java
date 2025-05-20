package com.minimalism.mp.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.minimalism.abs.entity.AbstractEntity;
import com.minimalism.mp.util.MpObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Data @Slf4j
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class SearchEntity implements AbstractEntity {
    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;
    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    @PostConstruct
    public void init() {
        toEntityParams();
    }

    protected Map<String, Object> toEntityParams() {
        log.debug("execute toEntityParams method");
        Map<String, Object> toEntityParams = toParams(MpObjectUtils.defaultIfEmpty(getParams(), this));
        this.params = toEntityParams;
        return toEntityParams;
    }

    public Map<String, Object> toParams() {
        return toEntityParams();
    }
}

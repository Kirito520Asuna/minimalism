package com.minimalism.gen.vo;

import com.minimalism.gen.domain.GenTable;
import com.minimalism.gen.domain.GenTableColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/29 下午7:10:32
 * @Description
 */
@Data @NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
public class GenTableInfoVo implements Serializable {
    private GenTable genTable;
    private List<GenTableColumn> genTableColumnList;
    private List<GenTable> tables;

}

package com.minimalism.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.pagehelper.PageInfo;
import com.minimalism.view.BaseJsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页封装类
 *
 * @author yan
 */
@Schema(description = "通用分页")
@Data
@NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
@JsonView(BaseJsonView.BaseView.class)
public class ResultPage<T> implements Serializable {
    private static final long serialVersionUID = 5716478706296860374L;
    /**
     * 返回列表
     */

    @Schema(description = "返回列表")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("list")
    private List<T> list;
    /**
     * 每页记录数
     */

    @Schema(description = "每页记录数", example = "10")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pageSize")
    private long pageSize;
    /**
     * 当前页码数
     */

    @Schema(description = "当前页码数", example = "1")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pageNumber")
    private long pageNumber;
    /**
     * 总页码数
     */

    @Schema(description = "总页码数", example = "2")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("pages")
    private long pages;
    /**
     * 总记录数
     */

    @Schema(description = "总记录数", example = "13")
    @JsonView(BaseJsonView.BaseView.class)
    @JsonProperty("total")
    private long total;

    /**
     * 通用返回
     *
     * @param list
     * @param pageNumber
     * @param pageSize
     * @param total
     * @param <T>
     * @return
     */
    public static <T> Result<ResultPage<T>> result(List<T> list, long pageNumber, long pageSize, long total) {
        long tempPages = total / pageSize;
        long pages = total % pageSize > 0 ? tempPages : tempPages + 1;
        ResultPage<T> resultPage = new ResultPage<>();
        resultPage.setList(list);
        resultPage.setPageSize(pageSize);
        resultPage.setPageNumber(pageNumber);
        resultPage.setPages(pages);
        resultPage.setTotal(total);
        return Result.ok(resultPage);
    }

    /**
     * 通用返回
     *
     * @param list
     * @param pageNumber
     * @param pageSize
     * @param total
     * @param <T>
     * @return
     */
    public static <T> ResultPage<T> resultPage(List<T> list, long pageNumber, long pageSize, long total) {
        long tempPages = total / pageSize;
        long pages = total % pageSize > 0 ? tempPages : tempPages + 1;
        ResultPage<T> resultPage = new ResultPage<>();
        resultPage.setList(list);
        resultPage.setPageSize(pageSize);
        resultPage.setPageNumber(pageNumber);
        resultPage.setPages(pages);
        resultPage.setTotal(total);
        return resultPage;
    }

    /**
     * pagehelper分页插件分页转换
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> ResultPage<T> listToPage(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        int pageNumber = pageInfo.getPageNum();
        int pageSize = pageInfo.getPageSize();
        int pages = pageInfo.getPages();
        long total = pageInfo.getTotal();
        List<T> pageInfoList = pageInfo.getList();
        ResultPage<T> resultPage = new ResultPage<>(pageInfoList, pageSize, pageNumber, pages, total);
        return resultPage;
    }


    //
//    /**
//     * mybatis-plus 分页转化为通用分页
//     * @param iPage
//     * @return
//     * @param <T>
//     */
//    public static <T> ResultPage<T> ipageToPage(IPage<T> iPage){
//        long pages = iPage.getPages();
//        List<T> list = iPage.getRecords();
//        long total = iPage.getTotal();
//        long pageSize = iPage.getSize();
//        long pageNumber = iPage.getCurrent();
//        ResultPage<T> resultPage = new ResultPage<>(list, pageSize, pageNumber, pages, total);
//        return resultPage;
//    }
    public static <T> ResultPage<T> onePageToAnotherPage(ResultPage page, List<T> list) {
        // 数据列表转换为通用分页
        ResultPage<T> result = new ResultPage<>();
        result.setList(list);
        result.setPageSize(page.getPageSize());
        result.setPageNumber(page.getPageNumber());
        result.setPages(page.getPages());
        result.setTotal(page.getTotal());
        // 返回结果
        return result;
    }

}

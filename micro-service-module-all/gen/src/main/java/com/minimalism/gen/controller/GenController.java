package com.minimalism.gen.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.aop.async.AsyncFuture;
import com.minimalism.aop.log.SysLog;
import com.minimalism.aop.shiro.ShiroPermissions;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.enums.BusinessType;
import com.minimalism.enums.OperateTypeEnum;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.gen.domain.GenTable;
import com.minimalism.gen.domain.GenTableColumn;
import com.minimalism.gen.service.GenTableColumnService;
import com.minimalism.gen.service.GenTableService;
import com.minimalism.gen.vo.GenTableInfoVo;
import com.minimalism.result.Result;
import com.minimalism.result.ResultPage;
import com.minimalism.text.Convert;
import com.minimalism.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author minimalism
 * @Date 2024/10/29 下午4:58:40
 * @Description
 */
@Tag(name = "代码生成")
@RequestMapping({"/gen", "/api/gen"})
@RestController
public class GenController implements AbstractBaseController {
    @Resource
    private GenTableService genTableService;
    @Resource
    private GenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
    @ShiroPermissions("tool:gen:list")
    @GetMapping("/list")
    @Operation(summary = "查询代码生成列表")
    @SysLog(title = "查询代码生成列表", type = OperateTypeEnum.OTHER)
    public Result<ResultPage<GenTable>> genList(@RequestParam(defaultValue = "1") Long pageNumber,
                                                @RequestParam(defaultValue = "10") Long pageSize) {
        GenTable genTable = new GenTable();
        Map<String, Object> params = genTable.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return listToPage(list);
    }

    /**
     * 查询数据库列表
     */
    @ShiroPermissions("tool:gen:list")
    @Operation(summary = "查询数据库列表")
    @SysLog(title = "查询数据库列表")
    @GetMapping("/db/list")
    public Result<ResultPage<GenTable>> dataList(@RequestParam(defaultValue = "1") Long pageNumber,
                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        List<String> excludeTableNames = CollUtil.newArrayList();
        //excludeTableNames =  genTableService.selectGenTableNameList();
        GenTable genTable = new GenTable();
        Map<String, Object> params = genTable.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        params.put("excludeTableNames", excludeTableNames);
        PageUtils.startPage(params);
        List<GenTable> list = genTableService.selectDbTableList(genTable);
        return listToPage(list);
    }

    /**
     * 查询数据表字段列表
     */
    @SysLog(title = "查询数据表字段列表")
    @Operation(summary = "查询数据表字段列表")
    @GetMapping(value = "/column/{tableId}")
    public Result<ResultPage<GenTableColumn>> columnList(@PathVariable Long tableId) {
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        return listToPage(list);
    }

    /**
     * 修改代码生成业务
     */
    @ShiroPermissions("tool:gen:query")
    @SysLog(title = "修改代码生成业务")
    @Operation(summary = "修改代码生成业务")
    @GetMapping(value = "/{tableId}")
    public Result<GenTableInfoVo> getInfo(@PathVariable Long tableId) {
        GenTable table = genTableService.getById(tableId);
        List<GenTable> tables = genTableService.list();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);

        GenTableInfoVo genTableInfoVo = new GenTableInfoVo()
                .setGenTable(table)
                .setTables(tables)
                .setGenTableColumnList(list);
        return ok(genTableInfoVo);
    }

    /**
     * 生成代码（自定义路径）
     */
    @SysLog(title = "生成代码（自定义路径）", businessType = BusinessType.GENCODE)
    @Operation(summary = "生成代码（自定义路径）")
    @ShiroPermissions("tool:gen:code")
    @GetMapping("/genCode/{tableName}")
    public Result genCode(@PathVariable("tableName") String tableName,
                          @Parameter(description = "Shiro|Security  defaultValue = \"Shiro\"")
                          @RequestParam(required = false, defaultValue = "Shiro") String usePermissions) {
        genTableService.generatorCode(tableName, usePermissions);
        return ok();
    }

    /**
     * 导入表结构（保存）
     */
    @SysLog(title = "导入表结构（保存）", businessType = BusinessType.IMPORT)
    @Operation(summary = "导入表结构（保存）")
    @ShiroPermissions("tool:gen:import")
    @PostMapping("/importTable")
    public Result importTableSave(String tables) {
        List<String> tableNames = new ArrayList<>();
        tableNames = Arrays.stream(Convert.toStrArray(tables)).collect(Collectors.toList());
        if (CollUtil.isEmpty(tableNames)) {
            throw new GlobalCustomException("请选择要导入的表");
        }
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList);
        return ok();
    }

    /**
     * 修改保存代码生成业务
     */
    @SysLog(title = "修改保存代码生成业务", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改保存代码生成业务")
    @ShiroPermissions("tool:gen:edit")
    @PutMapping
    public Result editSave(@Validated @RequestBody GenTable genTable) {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return ok();
    }

    /**
     * 预览代码
     */
    @SysLog(title = "预览代码")
    @Operation(summary = "预览代码")
    @ShiroPermissions("tool:gen:preview")
    @GetMapping("/preview/{tableId}")
    public Result preview(@PathVariable("tableId") Long tableId) throws IOException {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return ok(dataMap);
    }

    /**
     * 同步数据库
     */
    @SysLog(title = "同步数据库", businessType = BusinessType.UPDATE)
    @Operation(summary = "同步数据库")
    @ShiroPermissions("tool:gen:edit")
    @GetMapping("/synchDb/{tableName}")
    public Result synchDb(@PathVariable("tableName") String tableName) {
        genTableService.synchDb(tableName);
        return ok();
    }

    /**
     * 删除代码生成
     */
    @ShiroPermissions("tool:gen:remove")
    @Operation(summary = "删除代码生成")
    @SysLog(title = "删除代码生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    public Result remove(@PathVariable("tableIds") Long[] tableIds) {
        List<Long> tableIdList = Arrays.stream(tableIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(tableIdList)) {
            genTableService.deleteGenTableByIds(tableIdList);
        }
        return ok();
    }

    /**
     * 生成代码（下载方式）
     */
    @ShiroPermissions("tool:gen:code")
    @Operation(summary = "生成代码（下载方式）")
    @SysLog(title = "生成代码（下载方式）", businessType = BusinessType.GENCODE)
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName, @Parameter(description = "Shiro|Security  defaultValue = \"Shiro\"")
    @RequestParam(required = false, defaultValue = "Shiro") String usePermissions) throws IOException {
        byte[] data = genTableService.downloadCode(tableName, usePermissions);
        genCode(response, data, null);
    }

    /**
     * 批量生成代码
     */
    @ShiroPermissions("tool:gen:code")
    @Operation(summary = "批量生成代码")
    @SysLog(title = "批量生成代码", businessType = BusinessType.GENCODE)
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response,
                             @RequestParam String tables, @Parameter(description = "Shiro|Security  defaultValue = \"Shiro\"")
                             @RequestParam(required = false, defaultValue = "Shiro") String usePermissions) throws IOException {
        List<String> tableNames = Arrays.stream(Convert.toStrArray(tables)).collect(Collectors.toList());
        byte[] data = genTableService.downloadCode(tableNames, usePermissions);
        genCode(response, data, null);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data, String filename) throws IOException {
        String minimalismPrefix = "minimalism_";
        filename = ObjectUtil.defaultIfNull(filename, minimalismPrefix + UUID.randomUUID());
        if (!filename.startsWith(minimalismPrefix)) {
            filename = minimalismPrefix + filename;
        }
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".zip");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    @Operation(summary = "test")
    @SysLog(title = "test")
    @GetMapping("/test")
    public void asyncGenCode() {
        SpringUtil.getBean(GenController.class).asyncGenCode2();
    }

    @AsyncFuture
    public void asyncGenCode2() {
        getLogger().info("asyncGenCode");
    }
}

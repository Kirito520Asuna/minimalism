package com.minimalism.task.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.job.domain.SysJobLog;
import com.minimalism.job.service.SysJobLogService;
import com.minimalism.utils.poi.ExcelUtil;
import com.minimalism.controller.AbstractBaseController;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.minimalism.aop.log.SysLog;
import com.minimalism.enums.BusinessType;
import com.minimalism.aop.shiro.ShiroPermissions;
import com.minimalism.result.Result;
import com.minimalism.result.ResultPage;
import com.minimalism.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 定时任务调度日志Controller
 *
 * @author ${author}
 * @date 2025-04-09
 */
@Tag(name = "定时任务调度日志")
@RestController
@RequestMapping({"/api/job/log", "/jwt/job/log", "/job/log"})
public class JobLogController implements AbstractBaseController {
    @Resource
    private SysJobLogService sysJobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @SysLog(title = "查询定时任务调度日志列表")
    @Operation(summary = "查询定时任务调度日志列表")
    @ShiroPermissions("minimalism:job:list")
    @GetMapping("/list")
    public Result<ResultPage<SysJobLog>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                              @RequestParam(defaultValue = "10") Long pageSize) {
        SysJobLog sysJobLog = new SysJobLog();
        Map<String, Object> params = sysJobLog.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysJobLog> list = sysJobLogService.selectJobLogList(sysJobLog);
        return listToPage(list);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @SysLog(title = "导出定时任务调度日志列表", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出定时任务调度日志列表")
    @ShiroPermissions("minimalism:job:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog) {
        PageUtils.startPage(sysJobLog.getParams());
        List<SysJobLog> list = sysJobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        util.exportExcel(response, list, "定时任务调度日志数据");
    }


    /**
     * 获取定时任务调度日志详细信息
     */
    @SysLog(title = "获取定时任务调度日志详细信息")
    @Operation(summary = "获取定时任务调度日志详细信息")
    @ShiroPermissions("minimalism:job:query")
    @GetMapping(value = "/{jobLogId}")
    public Result<SysJobLog> getInfo(@PathVariable("jobLogId") Long jobLogId) {
        return ok(sysJobLogService.getById(jobLogId));
    }

    /**
     * 删除定时任务调度日志
     */
    @SysLog(title = "删除定时任务调度日志", businessType = BusinessType.DELETE)
    @Operation(summary = "删除定时任务调度日志")
    @ShiroPermissions("minimalism:job:remove")
    @DeleteMapping("/{jobLogIds}")
    public Result remove(@PathVariable Long[] jobLogIds) {

        List<Long> jobLogIdList = Arrays.stream(jobLogIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(jobLogIdList)) {
            sysJobLogService.removeByIds(jobLogIdList);
        }
        return ok();
    }

    /**
     * 删除定时任务调度日志
     */
    @SysLog(title = "删除定时任务调度日志", businessType = BusinessType.CLEAN)
    @Operation(summary = "删除定时任务调度日志")
    @ShiroPermissions("minimalism:job:remove")
    @DeleteMapping("/clean")
    public Result clean() {
        sysJobLogService.cleanJobLog();
        return ok();
    }
}

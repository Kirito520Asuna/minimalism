package com.minimalism.task.controller;


import cn.hutool.core.collection.CollUtil;
import com.minimalism.aop.log.SysLog;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.enums.BusinessType;
import com.minimalism.job.domain.SysJob;
import com.minimalism.job.service.SysJobService;
import com.minimalism.result.Result;
import com.minimalism.result.ResultPage;
import com.minimalism.util.PageUtils;
import com.minimalism.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.minimalism.aop.shiro.ShiroPermissions;

@Tag(description = "定时任务管理", name = "定时任务管理")
@RestController
@RequestMapping({"/api/job", "/jwt/job", "/job"})
public class JobController implements AbstractBaseController {
    @Resource
    private SysJobService sysJobService;

    /**
     * 查询定时任务调度列表
     */
    @SysLog(title = "查询定时任务调度列表")
    @Operation(summary = "查询定时任务调度列表")

    @ShiroPermissions("minimalism:job:list")
    @GetMapping("/list")
    public Result<ResultPage<SysJob>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                           @RequestParam(defaultValue = "10") Long pageSize) {
        SysJob sysJob = new SysJob();
        Map<String, Object> params = sysJob.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysJob> list = sysJobService.selectJobList(sysJob);
        return listToPage(list);
    }

    /**
     * 导出定时任务调度列表
     */
    @SysLog(title = "导出定时任务调度列表", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出定时任务调度列表")
    @ShiroPermissions("minimalism:job:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob) {
        PageUtils.startPage(sysJob.getParams());
        List<SysJob> list = sysJobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "定时任务调度数据");
    }

    /**
     * 获取定时任务调度详细信息
     */
    @SysLog(title = "获取定时任务调度详细信息")
    @Operation(summary = "获取定时任务调度详细信息")
    @ShiroPermissions("minimalism:job:query")
    @GetMapping(value = "/{jobId}")
    public Result<SysJob> getInfo(@PathVariable("jobId") Long jobId) {
        return ok(sysJobService.getById(jobId));
    }

    /**
     * 新增定时任务调度
     */
    @SysLog(title = "新增定时任务调度", businessType = BusinessType.INSERT)
    @Operation(summary = "新增定时任务调度")
    @ShiroPermissions("minimalism:job:add")
    @PostMapping
    public Result add(@RequestBody SysJob sysJob) {
        return ok(sysJobService.save(sysJob));
    }

    /**
     * 修改定时任务调度
     */
    @SysLog(title = "修改定时任务调度", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改定时任务调度")
    @ShiroPermissions("minimalism:job:edit")
    @PutMapping
    public Result edit(@RequestBody SysJob sysJob) {
        return ok(sysJobService.updateById(sysJob));
    }

    /**
     * 定时任务状态修改
     */
    @ShiroPermissions("minimalism:job:changeStatus")
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "定时任务状态修改")
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = sysJobService.getById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return ok(sysJobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @ShiroPermissions("minimalism:job:changeStatus")
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "定时任务立即执行一次")
    @PutMapping("/run")
    public Result run(@RequestBody SysJob job) throws SchedulerException {
        boolean result = sysJobService.run(job);
        return result ? ok() : fail("任务不存在或已过期！");
    }

    /**
     * 删除定时任务调度
     */
    @SysLog(title = "删除定时任务调度", businessType = BusinessType.DELETE)
    @Operation(summary = "删除定时任务调度")
    @ShiroPermissions("minimalism:job:remove")
    @DeleteMapping("/{jobIds}")
    public Result remove(@PathVariable Long[] jobIds) throws SchedulerException {

        List<Long> jobIdList = Arrays.stream(jobIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(jobIdList)) {
            sysJobService.deleteJobByIds(jobIdList);
        }
        return ok();
    }
}

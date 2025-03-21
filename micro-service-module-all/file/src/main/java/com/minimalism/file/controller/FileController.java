package com.minimalism.file.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.aop.aviator.Aviator;
import com.minimalism.aop.aviator.AviatorValid;
import com.minimalism.aop.aviator.AviatorValids;
import com.minimalism.aop.log.SysLog;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.dto.FileUpDto;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.config.FileUploadConfig;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.service.FileInfoService;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.result.Result;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.jvm.JVMUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import com.minimalism.vo.FileUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;


/**
 * @Author yan
 * @Date 2025/3/6 14:59:55
 * @Description
 */
@Tag(name = "文件接口")
@RequestMapping({"/file", "/api/file", "/jwt/file"})
@RestController
public class FileController implements AbstractBaseController {
    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private FileService fileService;

    @AviatorValids(values = {@AviatorValid(expression = "!(arg0.img && arg0.dir)", errorMessage = "非法请求")})
    @SysLog
    @Operation(summary = "分片上传 初始调用")
    @PostMapping("/upload/start")
    public Result<FileUploadVo> uploadStart(@RequestBody FileUpDto dto) {
        //long maxMemory = JVMUtils.maxMemory();
        //if (maxMemory < dto.getSize()) {
        //    throw new GlobalCustomException("大文件上传不支持");
        //}
        //允许大文件分片上传
        FileUploadVo fileUploadVo = new FileUploadVo();
        FileInfo fileInfo = new FileInfo();
        CustomBeanUtils.copyPropertiesIgnoreNull(dto, fileInfo);
        fileInfo.setName(FileUtil.mainName(fileInfo.getFileName()))
                .setPartCode(fileUploadVo.getIdentifier())
                .setUploadDir(LocalOSSUtils.getUploadDir());
        fileInfoService.save(fileInfo);

        int chunkSize = FileUploadConfig.maxChunkSize; // 每个分片大小为10MB
        //chunkSize = 10485760;
        Long size = fileInfo.getSize();
        Long fileId = fileInfo.getFileId();
        int totalChunks = (int) Math.ceil((double) size / chunkSize);

        //String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        return ok(fileUploadVo
                .setFileId(fileId)
                .setTotalFileSize(size)
                .setChunkSize(chunkSize)
                .setTotalChunks(totalChunks));
    }


    @Operation(summary = "分片上传")
    @SysLog
    @PostMapping("/upload/chunk")
    public Result<String> uploadChunkV1(@RequestPart("file") MultipartFile file,
                                        @RequestParam("chunkNumber") int chunkNumber,
                                        @RequestParam("totalChunks") int totalChunks,
                                        @Parameter(description = "文件总大小")
                                        @RequestParam("totalFileSize") long totalFileSize,
                                        @RequestParam(value = "fileId", required = false) Long fileId,
                                        @RequestParam("identifier") String identifier) {

        //long maxMemory = JVMUtils.maxMemory();
        //if (maxMemory < totalFileSize) {
        //    throw new GlobalCustomException("大文件上传不支持");
        //}
        //允许大文件分片上传

        try {
            long fileSize = file.getSize();
            InputStream inputStream = file.getInputStream();
            boolean uploadChunk = fileService.uploadChunk(inputStream, identifier, chunkNumber, totalChunks, fileId);
            if (!uploadChunk) {
                throw new GlobalCustomException("Upload failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("Upload failed");
        }
        return ok();
    }

    @SneakyThrows
    @Operation(summary = "分片合并(禁止大文件合并 解决方案客户端获取字节数组自行合并)")
    @SysLog
    @PostMapping("/upload/merge")
    @AviatorValids(values = {
            @AviatorValid(expression = "!(arg2==null && (arg3==null||arg3==''))", errorMessage = "非法请求"),
    })
    public Result<String> mergeChunks(
            @RequestParam("identifier") String identifier,
            @RequestParam(value = "totalFileSize") Long totalFileSize,
            @RequestParam(value = "fileId", required = false) Long fileId,
            @RequestParam(value = "fileName", required = false) String fileName) {

        long maxMemory = JVMUtils.maxMemory();
        if (maxMemory < totalFileSize) {
            //禁止大文件分片合并
            throw new GlobalCustomException("大文件上传不支持");
        }

        fileService.mergeChunks(identifier, fileId, fileName);
        return ok();
    }

    @SneakyThrows
    @Operation(summary = "(纯技术)分片合并")
    @SysLog
    @PostMapping("/upload/merge/chunks")
    public Result<String> uploadMergeChunks(
            @RequestParam("identifier") String identifier,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) {
        fileService.uploadMergeChunks(identifier, totalChunks, fileName);
        return ok();
    }

    @SysLog
    @Operation(summary = "获取本服务器文件字节")
    @GetMapping("${file.byte.get:/fetch/byte/local}")
    public Result<List<byte[]>> fetchByteByLocal(@RequestParam(required = false) String identifier,
                                                 @RequestParam(required = false) String folder,
                                                 @RequestParam(required = false) String fileName,
                                                 @RequestParam(required = false) Integer chunkNumber) {
        if (StrUtil.isBlank(fileName) && (StrUtil.isBlank(folder) || StrUtil.isBlank(identifier))) {
            throw new GlobalCustomException("非法请求");
        }
        return ok(fileService.getByteByLocal(fileName, folder, identifier, chunkNumber));
    }

    @SysLog
    @Operation(summary = "移除本服务器文件")
    @DeleteMapping("${file.byte.del:/del/file/local}")
    public Result<String> delFileLocal(@RequestParam(required = false) String identifier,
                                       @RequestParam(required = false) String folder,
                                       @RequestParam(required = false) String fileName,
                                       @RequestParam(required = false) Integer chunkNumber) {
        if (StrUtil.isBlank(fileName) && (StrUtil.isBlank(folder) || StrUtil.isBlank(identifier))) {
            throw new GlobalCustomException("非法请求");
        }
        fileService.delByteByLocal(fileName, folder, identifier, chunkNumber);
        return ok();
    }

    @SysLog
    @Operation(summary = "下载")
    @GetMapping("download/{identifier}")
    public void download(@PathVariable("identifier") String identifier,
                         @RequestParam(required = false) boolean isPart,
                         @RequestParam(required = false) Integer partSort,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        if (isPart && ObjectUtils.isEmpty(partSort)) {
            throw new GlobalCustomException("非法请求");
        }
        fileService.downLoadFileMultiThread(identifier, isPart, partSort, request,response);
    }

}

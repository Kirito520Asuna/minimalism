package com.minimalism.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.minimalism.aop.log.SysLog;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


/**
 * @Author yan
 * @Date 2025/3/6 14:59:55
 * @Description
 */
@Tag(name = "文件接口")
@RequestMapping({"/file", "/api/file", "/jwt/file"})
@RestController
public class FileController implements AbstractBaseController {
    @Value("${file.upload-dir:tmp/uploads}")
    private String uploadDir;

    @Operation(summary = "分片上传")
    @SysLog(title = "分片上传")
    @PostMapping("/upload/chunk/v1")
    public Result<String> uploadChunkV1(@RequestParam("file") MultipartFile file,
                                        @RequestParam("chunkNumber") int chunkNumber,
                                        @RequestParam("totalChunks") int totalChunks,
                                        @RequestParam(value = "fileId", required = false) Long fileId,
                                        @RequestParam("identifier") String identifier) {
        // 使用HuTool保存分片文件
        String part = ".part";
        // 使用HuTool保存分片文件
        String path = uploadDir  + "/" + identifier + "/" + chunkNumber + part;
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = file.getInputStream();
            IoUtil.copy(inputStream, outputStream);
            FileUtil.writeFromStream(inputStream, FileUtil.newFile(path));
            //String upload = upload(outputStream, part);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("Upload failed");
        }
        return ok();
    }

    @SneakyThrows
    @Operation(summary = "分片合并")
    @SysLog
    @PostMapping("/upload/merge")
    public Result<String> mergeChunks(
            @RequestParam("identifier") String identifier,
            @RequestParam(value = "fileId", required = false) Long fileId) {
        getBean(FileService.class).mergeOutputStream(identifier, fileId);
        return ok();
    }

}

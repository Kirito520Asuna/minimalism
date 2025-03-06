package com.minimalism.file.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.aop.aviator.Aviator;
import com.minimalism.aop.aviator.AviatorValid;
import com.minimalism.aop.aviator.AviatorValids;
import com.minimalism.aop.log.SysLog;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.dto.FileUpDto;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.service.FileInfoService;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.result.Result;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.vo.FileUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
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
    @Value("${file.upload-dir:tmp/uploads}")
    private String uploadDir;

    @AviatorValids(values = {@AviatorValid(expression = "arg0.img && arg0.dir", errorMessage = "非法请求")})
    @SysLog
    @Operation(summary = "分片上传 初始调用")
    @PostMapping("/upload/start")
    public Result<FileUploadVo> uploadStart(@RequestBody FileUpDto dto){
        FileInfo fileInfo = new FileInfo();
        CustomBeanUtils.copyPropertiesIgnoreNull(dto, fileInfo);
        fileInfoService.save(fileInfo);

        int chunkSize = 1024 * 1024; // 每个分片大小为1MB

        Long size = fileInfo.getSize();
        Long fileId = fileInfo.getFileId();
        int totalChunks = (int) Math.ceil((double) size / chunkSize);
        //String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        return ok(new FileUploadVo().setFileId(fileId).setTotalChunks(totalChunks));
    }


    @Operation(summary = "分片上传")
    @SysLog
    @PostMapping("/upload/chunk")
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
        if (fileId != null) {
            FileInfo fileInfo = fileInfoService.getById(fileId);
            String fileName = fileInfo.getFileName();
            String suffix = fileInfo.getSuffix();
        }
        return ok();
    }

}

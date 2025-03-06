package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.vo.PartVo;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/6 15:15:24
 * @Description
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FilePartService filePartService;
    @Override
    public List<PartVo> getPartList(String identifier, Long fileId) {
        return filePartService.getPartList(identifier, fileId);
    }

    @Override
    public InputStream partToInputStream(PartVo partVo) {
        InputStream inputStream = null;
        Boolean local = partVo.getLocal();
        if (ObjectUtils.defaultIfEmpty(local, Boolean.FALSE)) {
            inputStream = FileUtil.getInputStream(partVo.getLocalResource());
        } else {
            try {
                inputStream = new URL(partVo.getUrl()).openStream();
            } catch (IOException e) {
                throw new GlobalCustomException(e.getMessage());
            }
        }
        return inputStream;
    }

    @Override
    public List<InputStream> getPartInputStreamList(String identifier, Long fileId) {
        List<PartVo> partList = getPartList(identifier, fileId);
        List<InputStream> inputStreamList = partList.stream().map(this::partToInputStream).collect(Collectors.toList());
        return inputStreamList;
    }

    @Override
    public OutputStream mergeOutputStream(String identifier, Long fileId) {
        List<PartVo> partList = getPartList(identifier, fileId);
        List<InputStream> list = partList.stream().map(this::partToInputStream).collect(Collectors.toList());
        //List<InputStream> list = getPartInputStreamList(identifier,fileId);
        OutputStream out = new ByteArrayOutputStream();
        try (OutputStream outputStream = out) {
            for (InputStream chunk : list) {
                try (InputStream inputStream = chunk) {
                    IoUtil.copy(inputStream, outputStream);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("合并错误！");
        }
        //filePartService.removePart(identifier, fileId);
        return out;
    }
}

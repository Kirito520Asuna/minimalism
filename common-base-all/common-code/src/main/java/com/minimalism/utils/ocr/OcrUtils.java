package com.minimalism.utils.ocr;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.benjaminwan.ocrlibrary.OcrResult;
import com.minimalism.utils.object.ObjectUtils;
import io.github.mymonstercat.Model;
import io.github.mymonstercat.ocr.InferenceEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2024/8/13 0013 10:39:14
 * @Description
 */
@Slf4j
public class OcrUtils {
    @SneakyThrows
    public static void main(String[] args) {
        boolean isInput = false;
        InferenceEngine engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V3);
        String imagePath = "C:\\Users\\Administrator\\Desktop\\Snipaste_2024-08-12_18-12-35.png";
        imagePath = "https://img.zcool.cn/community/0177c35548e8fb0000019ae93040ce.jpg@2o.jpg";
        imagePath = "/home/yan/图片/截图/jrebel.png";
        InputStream input = null;
        String prx = imagePath.substring(imagePath.lastIndexOf("."), imagePath.length());
        try {
            input = new URL(imagePath).openStream();
            isInput = true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                input = FileUtil.getInputStream(new File(imagePath));
            } catch (Exception ex) {

            }
        }
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (true) {
            OcrResult ocrResult = ocr(input, prx, "temp/", null);
            System.err.println(JSONUtil.toJsonStr(ocrResult));
            System.err.println(ocrResult.getStrRes().trim());
            return;
        }

/*        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        String pathname = "temp/" + format + "/" + UUID.randomUUID() + prx;
        File file = new File(pathname);
        try {
            OutputStream output = FileUtil.getOutputStream(file);
            if (isInput) {
                IoUtil.copy(input, output);
            }
            String path = file.getPath();
            OcrResult ocrResult = engine.runOcr(path);
            System.err.println(JSONUtil.toJsonStr(ocrResult));
            System.err.println(ocrResult.getStrRes().trim());
        } finally {
            FileUtil.del(file);
        }*/
    }

    /**
     * 通用ocr
     *
     * @param input
     * @param suffix          文件后缀
     * @param ocrFileTempPath 文件临时路径
     * @param model
     * @return
     */
    public static OcrResult ocr(InputStream input, String suffix, String ocrFileTempPath, Model model) {
        InferenceEngine engine = InferenceEngine.getInstance(ObjectUtils.defaultIfEmpty(model, Model.ONNX_PPOCR_V3));
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        String pathname = ocrFileTempPath + format + "/" + UUID.randomUUID() + suffix;
        log.info("ocr file init pathname:{}", pathname);
        File file = new File(pathname);
        try {
            OutputStream output = FileUtil.getOutputStream(file);
            IoUtil.copy(input, output);
            String path = file.getPath();
            OcrResult ocrResult = engine.runOcr(path);
            log.info("ocr result:{}", JSONUtil.toJsonStr(ocrResult));
            return ocrResult;
        } finally {
            FileUtil.del(file);
            log.info("ocr file del file pathname:{}", pathname);
        }
    }
}

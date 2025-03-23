package com.minimalism.utils.file;

import cn.hutool.core.io.FileUtil;
import com.minimalism.constant.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author yan
 * @Date 2025/3/12 22:36:32
 * @Description
 */
@Slf4j
public class FileUtils extends FileUtil {
    public static boolean isFile(String path) {
        return FileUtil.isFile(path) || FileUtil.getName(path).contains(".");
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return FileUtil.isFile(file) || FileUtil.getName(file).contains(Constants.PART_SUFFIX);
    }

    /**
     * 下载文件
     *
     * @param response
     * @param fileName
     * @param bytes
     * @throws IOException
     */
    public static void downLoadFileMultiThread(HttpServletResponse response, String fileName, byte[] bytes) throws IOException {
        // 获取文件的总大小
        int fileSize = bytes.length;

        // 设置响应的文件类型为二进制流
        response.setContentType("application/octet-stream");
        // 设置响应头，告诉浏览器这是一个附件，提供下载
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        // 设置文件总大小，方便浏览器展示下载进度
        response.setHeader("Content-Length", String.valueOf(fileSize));

        // 设置每个分片的大小为4MB
        int chunkSize = 4 * 1024 * 1024; // 每个分片4MB
        // 计算文件需要分成多少个块
        int numChunks = (int) Math.ceil((double) fileSize / chunkSize);

        // 创建线程池，最大线程数为8，避免线程过多导致性能问题
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(numChunks, 8));  // 根据需要动态调整线程池大小
        List<Future<byte[]>> futures = new ArrayList<>(numChunks);

        // 将每个文件分块的读取任务提交到线程池
        for (int i = 0; i < numChunks; i++) {
            final int chunkIndex = i;  // 当前块的索引
            futures.add(executor.submit(() -> {
                // 根据分块大小计算读取范围
                int start = chunkIndex * chunkSize;
                int end = Math.min(start + chunkSize, fileSize);
                byte[] buffer = new byte[end - start];
                System.arraycopy(bytes, start, buffer, 0, buffer.length);
                return buffer; // 返回当前块的数据
            }));
        }

        // 将下载的分块按顺序写入到响应的输出流中
        try (OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            for (int i = 0; i < futures.size(); i++) {
                // 获取当前块的数据，并写入到响应输出流中
                byte[] chunk = futures.get(i).get(); // 使用future.get()等待当前块数据的完成
                os.write(chunk); // 写入当前分块数据
            }
            os.flush(); // 确保所有数据写入完成
        } catch (InterruptedException | ExecutionException e) {
            // 捕获异常，日志记录和适当处理
            log.error("下载过程中出现异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 关闭线程池，释放资源
            executor.shutdown();
        }
    }




}

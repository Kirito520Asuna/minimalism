import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalism.dto.FileUpDto;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2025/3/8 18:11:03
 * @Description
 */
public class UploadTest {
    public static void main(String[] args) {
        uploadChunk();
        //System.err.println(FileUtil.mainName("G:\\zip-all\\tbtool.7z") + "." + FileUtil.getSuffix("G:\\zip-all\\tbtool.7z"));
    }

    private static final int THREAD_COUNT = 4; // 线程池大小，可调整

    @SneakyThrows
    public static void uploadChunk() {
        String host = "192.168.3.85:13600";

        //String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "C:\\Users\\Administrator\\Desktop\\VMware Workstation.zip";
        String fileName = FileUtil.getName(path);

        FileUpDto fileUpDto = new FileUpDto();
        fileUpDto.setFileName(fileName)
                .setDir(Boolean.FALSE).setImg(Boolean.FALSE)
                .setType(FileUtil.getSuffix(path)).setSize(IoUtils.size(FileUtil.getInputStream(path)))
                .setSuffix("." + FileUtil.getSuffix(path));

        String format = String.format("http://%s/file/file/upload/start", host);
        HttpRequest request = HttpUtil.createPost(format);
        request.body(JSONUtil.toJsonStr(fileUpDto));

        String response = request.execute().body();
        JSONObject entries = JSONUtil.toBean(response, JSONObject.class);
        System.err.println("start :" + JSONUtil.toJsonStr(entries, JSONConfig.create().setIgnoreNullValue(false)));
        Integer code = (Integer) entries.getByPath("code");
        if (!ObjectUtils.equals(Integer.valueOf(200), code)) {
            throw new RuntimeException(entries.getByPath("message").toString());
        }
        //JSONObject data = entries.getByPath("data", JSONObject.class);
        int chunkSize = (Integer) entries.getByPath("data.chunkSize");
        int chunkNumber = (Integer) entries.getByPath("data.chunkNumber");
        int totalChunks = (Integer) entries.getByPath("data.totalChunks");
        Long totalFileSize = Long.parseLong("" + entries.getByPath("data.totalFileSize"));
        Long fileId = Long.valueOf((Integer) entries.getByPath("data.fileId"));
        String identifier = (String) entries.getByPath("data.identifier");

        InputStream inputStream = FileUtil.getInputStream(path);

        List<InputStream> streamList = IoUtils.splitInputStream(inputStream, chunkSize);

        String uploadUrl = String.format("http://%s/file/file/upload/chunk", host);
        //多线程执行
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = CollUtil.newArrayList();

        for (InputStream input : streamList) {
            final int currentChunk = chunkNumber++; // 确保每个任务的 chunkNumber 唯一
            futures.add(executorService.submit(() -> {
                try {
                    System.err.println("uploadChunk chunkNumber: " + currentChunk);
                    uploadChunk(uploadUrl, identifier, fileId, currentChunk, totalChunks, totalFileSize, input);
                } catch (Exception e) {
                    System.err.println("上传 chunk " + currentChunk + " 失败：" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }));
        }

        // 等待所有上传任务完成
        for (Future<?> future : futures) {
            try {
                future.get(); // 等待任务完成
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("任务执行失败：" + e.getMessage());
            }
        }

        // 关闭线程池
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        //for (InputStream input : streamList) {
        //    System.err.println("uploadChunk chunkNumber:" + chunkNumber);
        //    uploadChunk(uploadUrl, identifier, fileId, chunkNumber, totalChunks, totalFileSize, input);
        //    chunkNumber++;
        //}
        String mergeUrl = String.format("http://%s/file/file/upload/merge", host);
        merge(mergeUrl, fileName, identifier, totalChunks, fileId, totalFileSize);
    }

    public static void uploadChunk(String url, String identifier, Long fileId, int chunkNumber, int totalChunks, Long totalFileSize, InputStream inputStream) throws Exception {
        // 使用 Hutool 创建 POST 请求，上传 multipart 表单数据
        HttpResponse response = HttpRequest.post(url)
                //.form(form)
                .form("file", IoUtils.toByteArray(inputStream), "file")
                .form("chunkNumber", chunkNumber)
                .form("totalChunks", totalChunks)
                .form("identifier", identifier)
                .form("totalFileSize", totalFileSize)
                .form("fileId", fileId)
                // 如果需要传 fileId 参数，可添加 .form("fileId", "12345")
                .execute();

        // 输出响应内容
        System.out.println("Response: " + response.body());

    }

    public static void merge(String url, String fileName, String identifier, int totalChunks, Long fileId, Long totalFileSize) {
        System.err.println("Merging chunks...");
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("identifier", identifier);
        params.put("totalChunks", totalChunks);
        params.put("fileId", fileId);
        params.put("totalFileSize", totalFileSize);

        HttpRequest request = HttpUtil.createPost(url);
        request.form(params);

        String response = request.execute().body();
        System.err.println("Chunk " + identifier + " Merging. Server response: " + response);
    }
}

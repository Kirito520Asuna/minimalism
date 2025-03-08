import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.minimalism.utils.io.IoUtils;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Author yan
 * @Date 2025/3/8 18:11:03
 * @Description
 */
public class UploadTest {
    public static void main(String[] args) {
        uploadChunk();
    }

    @SneakyThrows
    public static void uploadChunk() {
        String fileName = "【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";
        String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "G:\\zip-all\\【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";

        InputStream inputStream = FileUtil.getInputStream(path);

        List<InputStream> streamList = IoUtils.splitInputStream(inputStream, 1024 * 1024);

        String uploadUrl = "http://127.0.0.1:13600/file/file/upload/chunk";
        int chunkNumber = 1;
        int totalChunks = streamList.size();

        for (InputStream input : streamList) {
            uploadChunk(uploadUrl, identifier, chunkNumber, totalChunks, input);
            chunkNumber++;
        }

        String mergeUrl = "http://127.0.0.1:13600/file/file/upload/merge/chunks";
        merge(mergeUrl, fileName, identifier,totalChunks);
    }

    public static void uploadChunk(String url, String identifier, int chunkNumber, int totalChunks, InputStream inputStream) throws Exception {

        // 使用 Hutool 的 HttpRequest 发送 POST 请求
        Map<String, Object> form = new HashMap<>();
        //form.put("file", IoUtils.toByteArray(inputStream));
        form.put("chunkNumber", chunkNumber);
        form.put("totalChunks", totalChunks);
        form.put("identifier", identifier);
        // 使用 Hutool 创建 POST 请求，上传 multipart 表单数据
        HttpResponse response = HttpRequest.post(url)
                //.form(form)
                .form("file", IoUtils.toByteArray(inputStream),"file")
                .form("chunkNumber", chunkNumber)
                .form("totalChunks", totalChunks)
                .form("identifier", identifier)
                // 如果需要传 fileId 参数，可添加 .form("fileId", "12345")
                .execute();

        // 输出响应内容
        System.out.println("Response: " + response.body());

    }

    public static void merge(String url, String fileName, String identifier,int totalChunks) {
        System.err.println("Merging chunks...");
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("identifier", identifier);
        params.put("totalChunks", totalChunks);

        HttpRequest request = HttpUtil.createPost(url);
        request.form(params);

        String response = request.execute().body();
        System.err.println("Chunk " + identifier + " Merging. Server response: " + response);
    }
}

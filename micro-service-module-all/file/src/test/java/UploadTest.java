import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalism.dto.FileUpDto;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;
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
        String host = "192.168.3.22";

        String fileName = "tbtool.7z";
        //String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "G:\\zip-all\\tbtool.7z";

        FileUpDto fileUpDto = new FileUpDto();
        fileUpDto.setFileName(fileName)
                .setDir(Boolean.FALSE).setImg(Boolean.FALSE)
                .setType(FileUtil.getSuffix(path)).setSize(IoUtils.size(FileUtil.getInputStream(path)))
                .setSuffix("." + FileUtil.getSuffix(path));

        String format = String.format("http://%s:13600/file/file/upload/start", host);
        HttpRequest request = HttpUtil.createPost(format);
        request.body(JSONUtil.toJsonStr(fileUpDto));

        String response = request.execute().body();
        JSONObject entries = JSONUtil.toBean(response, JSONObject.class);
        Integer code = (Integer) entries.getByPath("code");
        if (!ObjectUtils.equals(Integer.valueOf(200),code)) {
            throw new RuntimeException(entries.getByPath("message").toString());
        }
        //JSONObject data = entries.getByPath("data", JSONObject.class);
        int chunkSize = (Integer) entries.getByPath("data.chunkSize");

        int totalChunks = (Integer) entries.getByPath("data.totalChunks");
        Long fileId = Long.valueOf((Integer) entries.getByPath("data.fileId"));
        String identifier = (String) entries.getByPath("data.identifier");

        InputStream inputStream = FileUtil.getInputStream(path);

        List<InputStream> streamList = IoUtils.splitInputStream(inputStream, chunkSize);


        String uploadUrl = String.format("http://%s:13600/file/file/upload/chunk", host);
        int chunkNumber = 1;
        //totalChunks = streamList.size();

        for (InputStream input : streamList) {
            uploadChunk(uploadUrl, identifier, fileId, chunkNumber, totalChunks, input);
            chunkNumber++;
        }

        String mergeUrl = String.format("http://%s:13600/file/file/upload/merge/chunks", host);
        merge(mergeUrl, fileName, identifier, totalChunks);
    }

    public static void uploadChunk(String url, String identifier, Long fileId, int chunkNumber, int totalChunks, InputStream inputStream) throws Exception {

        // 使用 Hutool 的 HttpRequest 发送 POST 请求
        Map<String, Object> form = new HashMap<>();
        //form.put("file", IoUtils.toByteArray(inputStream));
        form.put("chunkNumber", chunkNumber);
        form.put("totalChunks", totalChunks);
        form.put("identifier", identifier);
        // 使用 Hutool 创建 POST 请求，上传 multipart 表单数据
        HttpResponse response = HttpRequest.post(url)
                //.form(form)
                .form("file", IoUtils.toByteArray(inputStream), "file")
                .form("chunkNumber", chunkNumber)
                .form("totalChunks", totalChunks)
                .form("identifier", identifier)
                .form("fileId", fileId)
                // 如果需要传 fileId 参数，可添加 .form("fileId", "12345")
                .execute();

        // 输出响应内容
        System.out.println("Response: " + response.body());

    }

    public static void merge(String url, String fileName, String identifier, int totalChunks) {
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

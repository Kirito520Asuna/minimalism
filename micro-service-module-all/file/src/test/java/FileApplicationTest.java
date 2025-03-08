import cn.hutool.core.io.FileUtil;
import com.minimalism.FileApplication;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.*;

/**
 * @Author yan
 * @Date 2025/3/8 12:52:49
 * @Description
 */
@SpringBootTest(classes = FileApplication.class)
public class FileApplicationTest {
    @Test
    void test() {
        String fileName = "docker.sh";
        String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "G:\\Linux\\sh\\docker.sh";
        InputStream inputStream = FileUtil.getInputStream(path);
        LocalOSSUtils.splitFileLocal(fileName, identifier, inputStream);
        LocalOSSUtils.mergeFileLocal(fileName, identifier);
    }

    @SneakyThrows
    @Test
    void splitInputStream() {
        // 示例：将字符串转为 InputStream，然后拆分，每块 10 字节
        String data = "这是一段用于测试的长文本数据，用来演示 InputStream 的拆分。";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

        List<InputStream> parts = IoUtils.splitInputStream(in, 10);
        System.out.println("拆分后的流数量: " + parts.size());
        //遍历每个拆分的 InputStream 并打印内容
        int partNum = 1;
        for (InputStream part : parts) {
            byte[] partBytes = new byte[part.available()];
            part.read(partBytes);
            String partStr = new String(partBytes, "UTF-8");
            System.out.println("Part " + partNum++ + ": " + partStr);
        }
    }

    @SneakyThrows
    @Test
    void mergeInputStream() {
        // 示例：将字符串转为 InputStream，然后拆分，每块 10 字节
        String data = "这是一段用于测试的长文本数据，用来演示 InputStream 的拆分。";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

        List<InputStream> parts = IoUtils.splitInputStream(in, 10);
        System.out.println("拆分后的流数量: " + parts.size());
        InputStream inM = IoUtils.mergeInputStream(parts);

        String mStr = new String(IoUtils.toByteArray(inM), "UTF-8");
        System.out.println("Me :" + mStr);
    }

}

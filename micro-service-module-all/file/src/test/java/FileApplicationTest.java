import cn.hutool.core.io.FileUtil;
import com.minimalism.FileApplication;
import com.minimalism.utils.oss.LocalOSSUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.UUID;

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
}

package com.example.yuaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YuManusTest {

    @Resource
    private YuManus yuManus;

    @Test
    void run() {
        String userPrompt = """  
                我的另一半居住在北京海淀区，请帮我找到 5 公里内评分最高的餐厅""";
        String answer = yuManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}

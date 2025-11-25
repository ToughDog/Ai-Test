package com.example.yuaiagent.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class VectorStoreConfigTest {

    @Resource
    private VectorStoreConfig vectorStoreConfig;
    @Autowired
    private DashScopeEmbeddingModel dashscopeEmbeddingModel;

    @Test
    void loveAppVectorStore() throws IOException {
        vectorStoreConfig.loveAppVectorStore(dashscopeEmbeddingModel);
    }
}
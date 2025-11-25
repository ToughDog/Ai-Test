package com.example.yuaiagent.rag;


import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
@Slf4j
public class VectorStoreConfig {

    @Resource
    private DocumentLoader documentLoader;

    @Bean
    VectorStore loveAppVectorStore(DashScopeEmbeddingModel dashscopeEmbeddingModel) throws IOException {
        SimpleVectorStore vs = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documents = documentLoader.loadDocuments();
        vs.doAdd(documents);
        return vs;
    }
}

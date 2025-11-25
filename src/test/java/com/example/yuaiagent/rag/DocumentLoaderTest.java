package com.example.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DocumentLoaderTest {

    @Resource
    private DocumentLoader documentLoader;

    @Test
    void loadDocuments() throws IOException {
        documentLoader.loadDocuments();
    }
}
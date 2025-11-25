package com.example.yuaiagent.rag;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * RAG 文档加载器
 */
@Component
@Slf4j
public class DocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;


    public DocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 Markdown 文档
     *
     * @return
     * @throws IOException
     */
    public List<Document> loadDocuments() throws IOException {
        List<Document> allDocuments = new ArrayList<>();
        Resource[] documents = resourcePatternResolver.getResources("classpath:document/*.md");
        for (Resource document : documents) {
            String fileName = document.getFilename();
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata("filename", fileName)
                    .build();

            MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(document, config);
            allDocuments.addAll(markdownDocumentReader.get());
        }
        return allDocuments;
    }
}

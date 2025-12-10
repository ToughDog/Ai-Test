package com.example.yuaiagent.tool;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {


    @Value("${search-api.api-key}")
    private String searchApiKey;


    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        MailSendTool mailSendTool = new MailSendTool();
        TerminateTool terminateTool = new TerminateTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();

        return ToolCallbacks.from(
                fileOperationTool,
                mailSendTool,
                terminateTool,
                webSearchTool,
                webScrapingTool
        );
    }
}


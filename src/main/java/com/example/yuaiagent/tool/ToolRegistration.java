package com.example.yuaiagent.tool;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {


    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        MailSendTool mailSendTool = new MailSendTool();

        return ToolCallbacks.from(
                fileOperationTool,
                mailSendTool
        );
    }
}


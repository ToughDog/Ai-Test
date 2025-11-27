package com.example.yuaiagent.tool;

import com.example.yuaiagent.utils.MailUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashSet;
import java.util.Set;

public class MailSendTool {

    @Tool(description = "Send an email to someone's address")
    private String sendEmail(@ToolParam(description = "the address of receiver") String emails,
                             @ToolParam(description = "the title of email") String title,
                             @ToolParam(description = "content of email") String content) {
        Set<String> set = new HashSet<>();
        set.add(emails);

        try {
            return MailUtils.sendEmail(set, title, content) ? "发送成功" : "发送失败";
        } catch (Exception e) {
            return "发送失败" + e.getMessage();
        }

    }
}

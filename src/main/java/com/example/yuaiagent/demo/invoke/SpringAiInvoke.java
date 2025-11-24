package com.example.yuaiagent.demo.invoke;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

public class SpringAiInvoke {

    public static void main(String[] args) throws Exception {
// 创建模型实例
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(TestApiKey.API_KEY)
                .build();
        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

// 创建 Agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .instruction("You are a helpful weather forecast assistant.")
                .build();

// 运行 Agent
        AssistantMessage call = agent.call("what is the weather in Hangzhou?");
        System.out.println(call.toString());
    }
}
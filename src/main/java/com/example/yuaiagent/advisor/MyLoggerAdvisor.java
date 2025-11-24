package com.example.yuaiagent.advisor;

import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;

/**
 * 自定义 Advisor
 * 打印 info 级别日志，只输出单次用户提示词和 AI 回复的文本
 */

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {


    // 1.0.0 版本, 自定义 Advisor 通过实现接口 CallAroundAdvisor, StreamAroundAdvisor 实现
    // 1.1.0 版本, 自定义 Advisor 通过实现接口 CallAdvisor, StreamAdvisor 实现

    // 其中的参数有变化 由 AdvisedRequest -> ChatClientRequest, AdvisedResponse -> ChatClientResponse
    // 其中的方法名也有变化 1.1.0 版本方法为 adviseCall ，adviseStream


    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        this.logRequest(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        this.logResponse(chatClientResponse);
        return chatClientResponse;
    }

    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        this.logRequest(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    protected void logRequest(ChatClientRequest request) {
        log.info("ChatClientRequest.prompt: {}", request.prompt());
        log.info("ChatClientRequest.context: {}", request.context());
    }

    protected void logResponse(ChatClientResponse chatClientResponse) {
        log.info("ChatClientResponse.chatResponse: {}", chatClientResponse.chatResponse());
        // ChatClientResponse.context:{chat_memory_retrieve_size=10, chat_memory_conversation_id=3de5895b-f5fa-4000-aba4-f83b4dbda4cf}
        // 可以用于 Advisor 之间传递变量
        log.info("ChatClientResponse.context: {}", chatClientResponse.context());
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return 0;
    }

    public String toString() {
        return MyLoggerAdvisor.class.getSimpleName();
    }

}

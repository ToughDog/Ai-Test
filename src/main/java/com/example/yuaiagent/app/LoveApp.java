package com.example.yuaiagent.app;

import com.example.yuaiagent.advisor.MyLoggerAdvisor;
import com.example.yuaiagent.chatMemory.FileBasedChatMemory;
import com.example.yuaiagent.tool.MailSendTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LoveApp {

    private static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "chat_memory_retrieve_size";
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor vectorStoreCloudAdvisor;

    @Resource
    private ToolCallback[] allTools;

//    @Resource
//    private VectorStore pgVectorStore;


    /**
     * 初始化 app
     *
     * @param dashscopeChatModel
     */
    public LoveApp(ChatModel dashscopeChatModel) {
        // 基于本地文件创建 ChatMemory
        String dir = System.getProperty("user.dir") + "/tmp/chat_memory";
        ChatMemory chatMemory = new FileBasedChatMemory(dir);

        // 创建 ChatMemory，用于存储对话历史，基于内存实现
//        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

        // 初始化client 传入系统提示词并添加 ChatMemory
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
    }

    /**
     * AI 基础对话，支持多轮输出记忆
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // JAVA 21 新特性 语法结构化输出
    record LoveReport(String title, List<String> suggestions) {
    }


    /**
     * AI 恋爱报告功能，实战结构化输出
     *
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    /**
     * RAG问答
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                // 添加 RAG Advisor
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * RAG增强，基于云知识库
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithCloudRag(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                // 添加 RAG
                .advisors(vectorStoreCloudAdvisor)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * RAG问答，基于 PostgreSQL 的文档检索
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithPg(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                // 添加 RAG Advisor
//                .advisors(QuestionAnswerAdvisor.builder(pgVectorStore).build())
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("返回结果是: {}", content);
        return content;
    }

    /**
     * 工具使用对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("返回结果是: {}", content);
        return content;
    }

}

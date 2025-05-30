package com.example.backend.controller;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.config.Memory.JdbcChatMemoryRepository;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/AI/")
public class AIController {

    private final ChatClient chatClient;
    @Autowired
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;
    @Resource
    private VectorStore vectorStore;
    @Resource
    UserService userService;


    public AIController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private Prompt SetPrompt(String message,Integer userId){
        // 将用户消息转换为UserMessage对象
        Message userMessage = new UserMessage(message);

        // 定义系统提示模板文本，使用{name}和{voice}作为可替换变量
        // 这里定义了AI助手的角色、名称和回复风格
        String systemText = """
                你是一个基于人工智能技术构建的智能助手，专门用于快速、准确地检索和整理信息。
                 - 始终保持礼貌、耐心和专业。
                 - 理解用户意图，必要时主动询问澄清问题，以确保回答精准。
                 -在回答问题时可能需要用户的Id，用户的Id是{id}
                 - 采用"{voice}"指定的风格：
                   - 如果是"幽默风趣"，可适当插入轻松幽默的语句；
                   - 如果是"简洁严谨"，则重点突出核心要点；
                   - 如果是"温暖亲切"，则多使用鼓励和关怀的表达。
                 - 对于复杂问题，分步骤列出解决思路或操作流程。
                 - 提供引用或来源时，标明出处，保证信息可信可查。
                  - 如遇超出能力范围或无法验证的信息，要诚实告知并建议用户进一步咨询可靠渠道。
                """;

        // 创建系统提示模板对象
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);

        // 填充模板变量，创建系统消息
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "枫梓的专属AI", "id",userId,"voice", "温暖亲切"));

        // 将用户消息和系统消息组合成一个Prompt对象
        // 系统消息会指导AI如何回复用户消息
        return new Prompt(List.of(userMessage, systemMessage));
    }

    //例子
    private void AddVector(Integer userId){
        // Get all stories for the user
        User user = userService.getUserById(userId);

        // Create a list to store all documents
        List<Document> documents = new ArrayList<>();

        // Add each story to the vectorStore
//        for (User user : users) {
//            // Create a document with title and content
//            String documentContent = String.format("Title: %s\nContent: %s",
//                    story.getTitle(),
//                    story.getContent());
//
//            // Create Document object with metadata
//            Document document = new Document(documentContent, Map.of(
//                    "storyId", story.getStoryId().toString(),
//                    "userId", story.getUserId().toString(),
//                    "time", story.getTime().toString()
//            ));
//            documents.add(document);
//        }

        String documentContent = String.format("username: %s\ntime: %s",
                    user.getUsername(),
                    user.getTime().toString());
        Document document=new Document(documentContent, Map.of("userId", user.getId().toString()));
        documents.add(document);
        // Add all documents to vectorStore at once
        vectorStore.add(documents);
    }


    //流式输出---在跨域里面需要配置异步操作（实现configureAsyncSupport）、JWT也需要配置asyncSupported = true
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public Flux<String> chat2(@RequestParam("message") String message,
                              @RequestParam("userId") Integer userId) {
        //创建矢量数据库
        AddVector(userId);
        // 创建聊天记忆实例
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)  // 使用注入的自定义repository
                .maxMessages(20)
                .build();

        // 获取历史消息
        List<Message> historyMessages = jdbcChatMemoryRepository.findByConversationId(userId.toString());
        for (Message msg : historyMessages) {
            chatMemory.add(userId.toString(), msg);
        }

        // 添加新消息
        Message userMessage = new UserMessage(message);
        chatMemory.add(userId.toString(), userMessage);

        StringBuilder aiReplyBuilder = new StringBuilder();

        // 获取历史消息并生成回复
        Flux<String> aiResponseFlux = chatClient.prompt(SetPrompt(message,userId))
                .messages(chatMemory.get(userId.toString()))
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .stream()
                .content()
                .doOnNext(aiReplyBuilder::append)
                .doOnComplete(() -> {
                    // 流式结束后，保存完整对话到数据库
                    String fullReply = aiReplyBuilder.toString();
                    Message aiMessage = new AssistantMessage(fullReply);
                    chatMemory.add(userId.toString(), aiMessage);
                    // 保存所有消息到数据库
                    List<Message> allMessages = chatMemory.get(userId.toString());

                    // 保存所有消息到数据库
                    jdbcChatMemoryRepository.saveAll(userId.toString(), allMessages);
                });
        return aiResponseFlux.delayElements(Duration.ofMillis(100));
    }
}

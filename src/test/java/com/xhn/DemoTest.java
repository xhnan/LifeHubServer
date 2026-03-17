package com.xhn;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xhn
 * @date 2025/12/24 9:21
 * @description
 */
@SpringBootTest
public class DemoTest {

    @Autowired
    private  DeepSeekChatModel chatModel;
    @Test
    public void test() {
        String content = "Hello World";
        UserMessage userMessage = new UserMessage(content);
        SystemMessage systemMessage = new SystemMessage("你是我的人工智能助手,请你用最快的速度回复我,并且回复的内容要简洁明了");
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        var prompt = new Prompt(messages);
        ChatResponse call = chatModel.call(prompt);
        Generation result = call.getResult();
        String text = result.getOutput().getText();
        System.out.println(text);
    }
}

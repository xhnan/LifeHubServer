package com.xhn.wechat.callback.handler;

import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 消息处理器工厂
 * 根据应用配置的 handler_bean_name 字段获取对应的处理器
 * @author xhn
 * @date 2026-02-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerFactory {

    private final ApplicationContext applicationContext;

    /**
     * 根据应用配置获取对应的处理器
     * @param appConfig 应用配置
     * @return 消息处理器
     */
    public MessageHandler getHandler(BaseWeChatAppConfig appConfig) {
        String handlerBeanName = appConfig.getHandlerBeanName();

        // 如果数据库配置了 handler_bean_name，直接从 Spring 容器获取
        if (handlerBeanName != null && !handlerBeanName.trim().isEmpty()) {
            try {
                MessageHandler handler = applicationContext.getBean(handlerBeanName, MessageHandler.class);
                log.info("Using configured handler: [{}] for app: {}", handlerBeanName, appConfig.getAppName());
                return handler;
            } catch (Exception e) {
                log.error("Failed to get handler bean: [{}], falling back to default. Error: {}", handlerBeanName, e.getMessage());
                // 继续使用默认处理器
            }
        }

        // 使用默认处理器
        return getDefaultHandler();
    }

    /**
     * 获取默认处理器
     */
    private MessageHandler getDefaultHandler() {
        try {
            // 默认处理器 bean 名称为 "defaultMessageHandler"
            return applicationContext.getBean("defaultMessageHandler", MessageHandler.class);
        } catch (Exception e) {
            log.error("Failed to get default handler", e);
            throw new IllegalStateException("Default message handler (defaultMessageHandler) not found", e);
        }
    }

    /**
     * 根据简单类名获取处理器（支持自动补全）
     * 支持：
     * - 完整Bean名：financeMessageHandler
     * - 简单类名：FinanceMessageHandler（自动转为小写开头）
     * @param beanNameOrClassName Bean名称或简单类名
     * @return 消息处理器
     */
    public MessageHandler getHandlerByName(String beanNameOrClassName) {
        if (beanNameOrClassName == null || beanNameOrClassName.trim().isEmpty()) {
            return getDefaultHandler();
        }

        // 尝试直接获取
        try {
            return applicationContext.getBean(beanNameOrClassName, MessageHandler.class);
        } catch (Exception e) {
            // 如果是简单类名（如 FinanceMessageHandler），转为 Bean 名称（financeMessageHandler）
            if (Character.isUpperCase(beanNameOrClassName.charAt(0))) {
                String beanName = Character.toLowerCase(beanNameOrClassName.charAt(0)) + beanNameOrClassName.substring(1);
                try {
                    return applicationContext.getBean(beanName, MessageHandler.class);
                } catch (Exception e2) {
                    log.error("Failed to get handler: [{}]", beanNameOrClassName);
                    return getDefaultHandler();
                }
            }
            log.error("Failed to get handler: [{}]", beanNameOrClassName);
            return getDefaultHandler();
        }
    }

    /**
     * 检查处理器是否存在
     * @param beanName Bean名称
     * @return 是否存在
     */
    public boolean isHandlerExists(String beanName) {
        try {
            applicationContext.getBean(beanName, MessageHandler.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取所有可用的处理器列表
     * @return 处理器名称列表
     */
    public String[] getAvailableHandlers() {
        return applicationContext.getBeanNamesForType(MessageHandler.class);
    }
}

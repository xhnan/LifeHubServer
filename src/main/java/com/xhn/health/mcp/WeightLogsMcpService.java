package com.xhn.health.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author xhn
 * @date 2026/3/13 11:08
 * @description
 */
@Service
public class WeightLogsMcpService {
    @Tool(description = "获取当前服务器时间")
    public String getCurrentTime() {
        return "当前时间是: " + LocalDateTime.now();
    }

    @Tool(description = "根据城市名获取天气信息")
    public String getWeather(
            @ToolParam(description = "城市名称，例如：北京") String city) {
        // 这里应该是调用真实天气服务的逻辑
        return city + "的天气是晴朗，温度25°C。";
    }
}

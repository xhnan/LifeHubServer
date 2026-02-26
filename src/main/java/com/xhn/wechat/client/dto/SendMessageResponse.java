package com.xhn.wechat.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 发送消息响应
 * @author xhn
 * @date 2026-02-26
 */
@Data
public class SendMessageResponse {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("invaliduser")
    private String invalidUser;

    @JsonProperty("invalidparty")
    private String invalidParty;

    @JsonProperty("invalidtag")
    private String invalidTag;

    @JsonProperty("msgid")
    private String msgId;
}

package com.xhn.wechat.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 获取成员授权信息响应
 * @author xhn
 * @date 2026-02-26
 */
@Data
public class AuthInfoResponse {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("usertype")
    private Integer userType;

    @JsonProperty("userid")
    private String userId;

    @JsonProperty("open_userid")
    private String openUserId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("department")
    private List<Long> department;

    @JsonProperty("device")
    private String device;
}

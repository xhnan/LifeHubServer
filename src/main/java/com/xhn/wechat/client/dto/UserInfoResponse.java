package com.xhn.wechat.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 获取成员详细信息响应
 * @author xhn
 * @date 2026-02-26
 */
@Data
public class UserInfoResponse {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("userid")
    private String userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("department")
    private List<Long> department;

    @JsonProperty("order")
    private List<Long> order;

    @JsonProperty("position")
    private String position;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("email")
    private String email;

    @JsonProperty("biz_mail")
    private String bizMail;

    @JsonProperty("is_leader_in_dept")
    private List<Integer> isLeaderInDept;

    @JsonProperty("direct_leader")
    private List<String> directLeader;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("thumb_avatar")
    private String thumbAvatar;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("address")
    private String address;

    @JsonProperty("open_userid")
    private String openUserId;

    @JsonProperty("main_department")
    private Long mainDepartment;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("qr_code")
    private String qrCode;

    @JsonProperty("external_profile")
    private ExternalProfile externalProfile;

    @JsonProperty("external_position")
    private String externalPosition;

    @Data
    public static class ExternalProfile {
        private String externalCorpName;
        private List<ExternalAttr> externalAttr;
    }

    @Data
    public static class ExternalAttr {
        private String type;
        private String name;
        private Text text;
        private Web web;

        @Data
        public static class Text {
            private String value;
        }

        @Data
        public static class Web {
            private String url;
            private String title;
        }
    }
}

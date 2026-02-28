package com.xhn.wechat.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * 企业微信回调事件
 * @author xhn
 * @date 2026-02-26
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class CallbackEvent {

    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;

    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    @JacksonXmlProperty(localName = "Event")
    private String event;

    @JacksonXmlProperty(localName = "EventKey")
    private String eventKey;

    @JacksonXmlProperty(localName = "AgentID")
    private Long agentId;

    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

    @JacksonXmlProperty(localName = "Content")
    private String content;

    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;

    @JacksonXmlProperty(localName = "Title")
    private String title;

    @JacksonXmlProperty(localName = "Description")
    private String description;

    @JacksonXmlProperty(localName = "Url")
    private String url;

    @JacksonXmlProperty(localName = "PicUrl")
    private String picUrl;

    @JacksonXmlProperty(localName = "Format")
    private String format;

    @JacksonXmlProperty(localName = "Recognition")
    private String recognition;

    @JacksonXmlProperty(localName = "ThumbMediaId")
    private String thumbMediaId;

    @JacksonXmlProperty(localName = "Location_X")
    private Double locationX;

    @JacksonXmlProperty(localName = "Location_Y")
    private Double locationY;

    @JacksonXmlProperty(localName = "Scale")
    private Double scale;

    @JacksonXmlProperty(localName = "Label")
    private String label;

    @JacksonXmlProperty(localName = "FileSize")
    private Long fileSize;

    @JacksonXmlProperty(localName = "Ticket")
    private String ticket;

    @JacksonXmlProperty(localName = "Longitude")
    private Double longitude;

    @JacksonXmlProperty(localName = "Latitude")
    private Double latitude;

    @JacksonXmlProperty(localName = "Precision")
    private Double precision;

    @JacksonXmlProperty(localName = "ChangeType")
    private String changeType;

    @JacksonXmlProperty(localName = "UserID")
    private String userId;

    @JacksonXmlProperty(localName = "Status")
    private Integer status;

    @JacksonXmlProperty(localName = "ChatType")
    private String chatType;
}

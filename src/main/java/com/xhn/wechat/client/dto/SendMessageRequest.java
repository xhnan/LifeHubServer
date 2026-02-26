package com.xhn.wechat.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发送消息请求
 * @author xhn
 * @date 2026-02-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @JsonProperty("touser")
    private String toUser;

    @JsonProperty("toparty")
    private String toParty;

    @JsonProperty("totag")
    private String toTag;

    @JsonProperty("msgtype")
    private String msgType;

    @JsonProperty("agentid")
    private Long agentId;

    private Text text;

    private TextCard textCard;

    private Image image;

    private Voice voice;

    private Video video;

    private File file;

    private Link link;

    private Markdown markdown;

    private News news;

    private Mpnews mpnews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text {
        @JsonProperty("content")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextCard {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("url")
        private String url;

        @JsonProperty("btntxt")
        private String btnTxt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        @JsonProperty("media_id")
        private String mediaId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Voice {
        @JsonProperty("media_id")
        private String mediaId;

        @JsonProperty("duration")
        private Integer duration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Video {
        @JsonProperty("media_id")
        private String mediaId;

        @JsonProperty("duration")
        private Integer duration;

        @JsonProperty("title")
        private String title;

        @JsonProperty("thumb_media_id")
        private String thumbMediaId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class File {
        @JsonProperty("media_id")
        private String mediaId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("url")
        private String url;

        @JsonProperty("picurl")
        private String picUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Markdown {
        @JsonProperty("content")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class News {
        @JsonProperty("articles")
        private List<Article> articles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("url")
        private String url;

        @JsonProperty("picurl")
        private String picUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mpnews {
        @JsonProperty("articles")
        private List<MpnewsArticle> articles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MpnewsArticle {
        @JsonProperty("title")
        private String title;

        @JsonProperty("thumb_media_id")
        private String thumbMediaId;

        @JsonProperty("author")
        private String author;

        @JsonProperty("content_source_url")
        private String contentSourceUrl;

        @JsonProperty("digest")
        private String digest;

        @JsonProperty("show_cover_pic")
        private Integer showCoverPic;
    }
}

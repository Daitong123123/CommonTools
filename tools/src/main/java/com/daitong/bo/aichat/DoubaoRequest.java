package com.daitong.bo.aichat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class DoubaoRequest {

    @JsonProperty("section_id")
    @SerializedName("section_id")
    private String sectionId;
    @JsonProperty("conversation_id")
    @SerializedName("conversation_id")
    private String conversationId;
    @JsonProperty("local_message_id")
    @SerializedName("local_message_id")
    private String localMessageId;
    @JsonProperty("completion_option")
    @SerializedName("completion_option")
    private CompletionOption completionOption;
    private List<DoubaoMessage> messages;

    @Data
    public static class DoubaoMessage {
        private String content;
        @JsonProperty("content_type")
        @SerializedName("content_type")
        private Integer contentType = 2001;
    }

    @Data
    public static class CompletionOption {
        @JsonProperty("is_regen")
        @SerializedName("is_regen")
        private boolean isRegen = false;
        @JsonProperty("with_suggest")
        @SerializedName("with_suggest")
        private boolean withSuggest = false;
        @JsonProperty("need_create_conversation")
        @SerializedName("need_create_conversation")
        private boolean needCreateConversation = false;
        @JsonProperty("launch_stage")
        @SerializedName("launch_stage")
        private int launchStage;
        @JsonProperty("is_replace")
        @SerializedName("is_replace")
        private boolean isReplace = false;
        @JsonProperty("is_delete")
        @SerializedName("is_delete")
        private boolean isDelete = false;
        @JsonProperty("message_from")
        @SerializedName("message_from")
        private int messageFrom =  0;
        @JsonProperty("use_deep_think")
        @SerializedName("use_deep_think")
        private boolean useDeepThink = false;
        @JsonProperty("event_id")
        @SerializedName("event_id")
        private String eventId = "0";
    }

}

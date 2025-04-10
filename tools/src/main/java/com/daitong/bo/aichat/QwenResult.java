package com.daitong.bo.aichat;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class QwenResult {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    @JsonProperty("system_fingerprint")
    @SerializedName("system_fingerprint")
    private String systemFingerprint;
    private Usage usage;

    @Data
    public static class Choice {
        @JsonProperty("finish_reason")
        @SerializedName("finish_reason")
        private String finishReason;
        private long index;
        private Message message ;

        /**
         * Message
         *
         * @since 2024-05-29
         */
        @Data
        public static class Message {
            private String content;
            private String role;
        }
    }

    @Data
    public static class Usage {
        @JsonProperty("completion_tokens")
        @SerializedName("completion_tokens")
        private long completionTokens;
        @JsonProperty("prompt_tokens")
        @SerializedName("prompt_tokens")
        private long promptTokens;
        @JsonProperty("total_tokens")
        @SerializedName("total_tokens")
        private long totalTokens;
    }

}

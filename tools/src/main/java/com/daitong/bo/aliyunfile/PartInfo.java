package com.daitong.bo.aliyunfile;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PartInfo {

    @JsonProperty("part_number")
    @SerializedName("part_number")
    private Integer partNumber;
    @JsonProperty("part_size")
    @SerializedName("part_size")
    private String partSize;
    @JsonProperty("upload_url")
    @SerializedName("upload_url")
    private String uploadUrl;
    @JsonProperty("internal_upload_url")
    @SerializedName("internal_upload_url")
    private String internalUpload_url;
    @JsonProperty("content_type")
    @SerializedName("content_type")
    private String contentType;
    @JsonProperty("upload_form_info")
    @SerializedName("upload_form_info")
    private String uploadForm_info;
    @JsonProperty("internal_upload_form_info")
    @SerializedName("internal_upload_form_info")
    private String internalUploadFormInfo;

}

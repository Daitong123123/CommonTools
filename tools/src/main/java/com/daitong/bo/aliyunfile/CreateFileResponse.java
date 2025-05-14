package com.daitong.bo.aliyunfile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CreateFileResponse {

    // 文件名
    @JsonProperty("file_name")
    @SerializedName("file_name")
    private String fileName;
    //文件类型
    private String type;
    //parent_file_id 父目录 id，上传到根目录时填写 root
    @JsonProperty("parent_file_id")
    @SerializedName("parent_file_id")
    private String parentFileId;
    @JsonProperty("drive_id")
    @SerializedName("drive_id")
    private String driveId;


    // 文件内容类型，默认值 application/oct-stream，常见的 content_type 可以参考 OSS
    @JsonProperty("encrypt_mode")
    @SerializedName("encrypt_mode")
    private String encryptMode;

    @JsonProperty("domain_id")
    @SerializedName("domain_id")
    private String domainId;

    @JsonProperty("revision_id")
    @SerializedName("revision_id")
    private String revisionId;

    @JsonProperty("rapid_upload")
    @SerializedName("rapid_upload")
    private Boolean rapidUpload;

    private String location;

    // 分片信息列表，最多 10000 个分片，当不填时，默认返回 1 个分片
    @JsonProperty("part_info_list")
    @SerializedName("part_info_list")
    private List<PartInfo> partInfoList;


    @JsonProperty("upload_id")
    @SerializedName("upload_id")
    private String uploadId;

    // 文件 id，覆盖写时必填
    @JsonProperty("file_id")
    @SerializedName("file_id")
    private String fileId;


}

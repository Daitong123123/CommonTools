package com.daitong.bo.aliyunfile;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CreateFileRequest {

    // 文件名
    private String name;
    //文件类型
    private String type;
    //parent_file_id 父目录 id，上传到根目录时填写 root
    @JsonProperty("parent_file_id")
    @SerializedName("parent_file_id")
    private String parentFileId;
    @JsonProperty("drive_id")
    @SerializedName("drive_id")
    private String driveId;
    @JsonProperty("share_id")
    @SerializedName("share_id")
    private String shareId;
    private Long size;
    // 文件内容类型，默认值 application/oct-stream，常见的 content_type 可以参考 OSS
    @JsonProperty("content_type")
    @SerializedName("content_type")
    private String contentType;

    // 分片信息列表，最多 10000 个分片，当不填时，默认返回 1 个分片
    @JsonProperty("part_info_list")
    @SerializedName("part_info_list")
    private List<PartInfo> partInfoList;
    // 用户自定义 tag，最多 1000 个 tag
    @JsonProperty("user_tags")
    @SerializedName("user_tags")
    private List<String> userTags;
    // 是否隐藏，默认不隐藏
    private Boolean hidden;
    // 文件描述信息，最长 1024 字符，默认为空
    private String description;
    // 文件内容 hash 值，需要根据 content_hash_name 指定的算法计算
    @JsonProperty("content_hash")
    @SerializedName("content_hash")
    private String contentHash;
    // 文件内容 hash 算法名，当前只支持 sha1
    @JsonProperty("content_hash_name")
    @SerializedName("content_hash_name")
    private String contentHashName;
    // preHash 使用预秒传功能时填写，为文件前 1KB sha1 值，当远端没有匹配上时，客户端无需再计算文件完整 sha1 去尝试秒传。
    @JsonProperty("pre_hash")
    @SerializedName("pre_hash")
    private String preHash;
    /**
     * 同名文件处理模式，可选值如下：
     * ignore：允许同名文件；
     * auto_rename：当发现同名文件是，云端自动重命名，默认为追加当前时间点，如 xxx _20060102_150405；
     * refuse：当云端存在同名文件时，拒绝创建新文件，返回客户端已存在同名文件的详细信息。
     * 默认为 ignore
     */
    @JsonProperty("check_name_mode")
    @SerializedName("check_name_mode")
    private String checkNameMode;

    // 文件 id，覆盖写时必填
    @JsonProperty("file_id")
    @SerializedName("file_id")
    private String fileId;

    // 文件本地创建时间，默认为空，格式为：yyyy-MM-ddTHH:mm:ssZ，采用 UTC +0 时区
    @JsonProperty("local_created_at")
    @SerializedName("local_created_at")
    private String localCreatedAt;

    // 文件本地修改时间，默认为空，格式为：yyyy-MM-ddTHH:mm:ssZ，采用 UTC +0 时区
    @JsonProperty("local_modified_at")
    @SerializedName("local_modified_at")
    private String localModifiedAt;

//    // 客户端指定的图片信息
//    @JsonProperty("image_media_metadata")
//    @SerializedName("image_media_metadata")
//    private String imageMediaMetadata;
//
//    // 客户端指定的视频信息
//    @JsonProperty("video_media_metadata")
//    @SerializedName("video_media_metadata")
//    private String videoMediaMetadata;

    // 是否开启并行上传分片功能
    @JsonProperty("parallel_upload")
    @SerializedName("parallel_upload")
    private String parallelUpload;
}

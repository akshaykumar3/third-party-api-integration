package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// POJO for Uploading to S3
@AllArgsConstructor
@Getter
@ToString
public class S3UploadRequest {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_path")
    private String filePath;
    @JsonProperty("bucket_name")
    private String bucketName;
    @JsonProperty("cloud_folder_name")
    private String cloudFolderName;
}

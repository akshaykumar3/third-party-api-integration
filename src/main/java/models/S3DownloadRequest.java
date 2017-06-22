package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
// POJO for Download from S3
public class S3DownloadRequest {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("location_to_save")
    private String locationToSave;
    @JsonProperty("bucket_name")
    private String bucketName;
    @JsonProperty("cloud_folder_name")
    private String cloudFolderName;
}

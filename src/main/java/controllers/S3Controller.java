package controllers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.S3DownloadRequest;
import models.S3UploadRequest;
import services.S3Services;
import utils.LogFactory;
import utils.UtilityFunctions;

// Controller to route all S3 related requests
public class S3Controller {

    private static final Logger logger = LogFactory.getLogger(S3Controller.class);

    public static void upload(RoutingContext context) {
        // Validate request
        S3UploadRequest s3UploadRequest = Json.decodeValue(context.getBodyAsString(), S3UploadRequest.class);
        if (!validateS3Upload(s3UploadRequest)) {
            UtilityFunctions.sendFailure(context.response(), "Invalid Request", HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        S3Services.upload(s3UploadRequest)
                .subscribe(response -> {
                    JsonObject apiResponse = new JsonObject()
                            .put("content_url", response);
                    UtilityFunctions.sendSuccess(context.response(), apiResponse);
                }, error -> {
                    logger.error("Error while uploading with request = " + s3UploadRequest.toString(), error);
                    UtilityFunctions.sendFailure(context.response(), "Error while uploading", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                });
    }

    public static void download(RoutingContext context) {
        // Validate Request
        String fileName = context.request().params().get("file_name");
        String locationToSave = context.request().params().get("location_to_save");
        String bucketName = context.request().params().get("bucket_name");
        String cloudFolderName = context.request().params().get("cloud_folder_name");
        S3DownloadRequest downloadRequest = new S3DownloadRequest(fileName, locationToSave, bucketName, cloudFolderName);
        if (!validateS3DownloadRequest(downloadRequest)) {
            UtilityFunctions.sendFailure(context.response(), "Error while downloading", HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        S3Services.download(downloadRequest)
                .subscribe(message -> {
                    JsonObject apiResponse = new JsonObject()
                            .put("message", message);
                    UtilityFunctions.sendSuccess(context.response(), apiResponse);
                }, error -> {
                    logger.error("Error while download with request = " + downloadRequest.toString(), error);
                    UtilityFunctions.sendFailure(context.response(), "Invalid Request", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                });
    }

    private static boolean validateS3DownloadRequest(S3DownloadRequest downloadRequest) {
        return UtilityFunctions.isPresent(downloadRequest.getBucketName()) &&
                UtilityFunctions.isPresent(downloadRequest.getCloudFolderName()) &&
                UtilityFunctions.isPresent(downloadRequest.getFileName()) &&
                UtilityFunctions.isPresent(downloadRequest.getLocationToSave());
    }

    private static boolean validateS3Upload(S3UploadRequest s3UploadRequest) {
        return UtilityFunctions.isPresent(s3UploadRequest.getBucketName()) &&
                UtilityFunctions.isPresent(s3UploadRequest.getCloudFolderName()) &&
                UtilityFunctions.isPresent(s3UploadRequest.getFileName()) &&
                UtilityFunctions.isPresent(s3UploadRequest.getFilePath());
    }
}

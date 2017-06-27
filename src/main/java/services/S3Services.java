package services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import io.vertx.core.logging.Logger;
import models.S3DownloadRequest;
import models.S3UploadRequest;
import rx.Observable;
import utils.LogFactory;
import verticles.APIVerticle;

import java.io.File;

// All S3 service related execution to be done here.
public class S3Services {

    private static final Logger logger = LogFactory.getLogger(S3Services.class);
    private static final String AWS_ACCESS_KEY = APIVerticle.config.getString("aws_access_key", "");
    private static final String AWS_SECRET_KEY = APIVerticle.config.getString("aws_secret_key", "");
    private static final String S3_BUCKET_NAME = APIVerticle.config.getString("aws_bucket_name", "");

    private static final AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
    private static final TransferManager transferManager = new TransferManager(credentials);


    public static Observable<String> upload(S3UploadRequest s3Request) {
        String cloudName = s3Request.getCloudFolderName() + "/" + s3Request.getFileName();
        PutObjectRequest request = new PutObjectRequest(S3_BUCKET_NAME, cloudName, new File(s3Request.getFilePath()))
                .withCannedAcl(CannedAccessControlList.PublicRead);

        logger.info("Upload started to location - "+cloudName);

        return Observable.create(subscriber -> {
            Upload upload = transferManager.upload(request);

            ProgressListener progressListener = progressEvent -> {
                switch (progressEvent.getEventType()) {
                    case TRANSFER_PART_COMPLETED_EVENT:
                        logger.info("Part Completed");
                        break;
                    case TRANSFER_PART_STARTED_EVENT:
                        logger.info("Part Started");
                        break;
                    case TRANSFER_PART_FAILED_EVENT:
                        logger.info("Part Failed");
                        break;
                    case TRANSFER_COMPLETED_EVENT:
                        String contentUrl = "https://s3-ap-southeast-1.amazonaws.com/" + S3_BUCKET_NAME + "/" + cloudName;
                        subscriber.onNext(contentUrl);
                        subscriber.onCompleted();
                        break;
                    case TRANSFER_FAILED_EVENT:
                        logger.error("Failure in uploading file to s3 :" + s3Request.getFileName());
                        try {
                            AmazonClientException e = upload.waitForException();
                            subscriber.onError(e);
                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                        break;
                }
            };

            upload.addProgressListener(progressListener);
        });
    }

    public static Observable<String> download(S3DownloadRequest request) {
        String relativeUrl = request.getCloudFolderName() + "/" + request.getFileName();
        GetObjectRequest getObjectRequest = new GetObjectRequest(S3_BUCKET_NAME, relativeUrl);

        return Observable.create(subscriber -> {
            ProgressListener progressListener = progressEvent -> {
                switch (progressEvent.getEventType()) {
                    case TRANSFER_COMPLETED_EVENT:
                        subscriber.onNext("Download completed.");
                        subscriber.onCompleted();
                        break;
                    case TRANSFER_FAILED_EVENT:
                        subscriber.onError(new Exception("Error while downloading data from s3"));
                        subscriber.onCompleted();
                        break;
                }
            };

            try {
                File testFile = new File(request.getLocationToSave());
                transferManager.download(getObjectRequest, testFile).addProgressListener(progressListener);
            } catch (AmazonServiceException ase) {
                logger.info("Caught an AmazonServiceException, which means your request made it "
                        + "to Amazon S3, but was rejected with an error response for some reason.");
                logger.info("Error Message:    " + ase.getMessage());
                logger.info("HTTP Status Code: " + ase.getStatusCode());
                logger.info("AWS Error Code:   " + ase.getErrorCode());
                logger.info("Error Type:       " + ase.getErrorType());
                logger.info("Request ID:       " + ase.getRequestId());
                subscriber.onError(ase);
                subscriber.onCompleted();
            } catch (AmazonClientException ace) {
                logger.info("Caught an AmazonClientException, which means the client encountered "
                        + "an internal error while trying to communicate with S3, "
                        + "such as not being able to access the network.");
                logger.info("Error Message: " + ace.getMessage());
                subscriber.onError(ace);
                subscriber.onCompleted();
            }
        });
    }
}

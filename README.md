# Third party API integration


An example to integrate with third party APIs like AWS S3 and Google maps APIs.

## Compatibility
- Java 8+
- Vert.x 3.x.x


## How to start the server
```
cd third-party-api-integration/config
update AWS access key, secret key and S3 bucket name in config.json file.
cd ..
mvn clean install
java -jar target/third-party-api-integration-1.0-SNAPSHOT-fat.jar -conf config/config.json
```

## How to call the APIs
```
Use the following sample curls to hit the API:
1. curl -X POST "http://127.0.0.1:8080/api/v1/s3/upload" -d '{"file_name":"<file_name>","file_path":"<local_file_path>","cloud_folder_name":"<folder_for_uploading>"}'
2. curl -X GET "http://127.0.0.1:8080/api/v1/s3/download?file_name=test.jpg&location_to_save=<file_name_with_extension>&cloud_folder_name=<folder_in_s3>"
3.a. curl -X GET "http://127.0.0.1:8080/api/v1/google/maps/geocode?latlng=12.9255259,77.6366633" if you don't have google key
3.b. curl -X GET "http://127.0.0.1:8080/api/v1/google/maps/geocode?latlng=12.9255259,77.6366633&key=<google_key>"
4. curl -X GET "http://127.0.0.1:8080//api/v1/google/maps/place?location=12.9255259,77.6366633&radius=500&key=<google_key>"
```
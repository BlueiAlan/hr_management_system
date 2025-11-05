package com.damien.utils;

import com.damien.context.BaseContext;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Slf4j
//@Component
public class MinioUtil {

    @Value("${sky.minio.endpoint}")
    private String endpoint;
    @Value("${sky.minio.access-key}")
    private String accessKey;
    @Value("${sky.minio.secret-key}")
    private String secretKey;
    @Value("${sky.minio.bucket-name}")
    private String bucketName;

    private static final String FILE_PREFIX = "/photos";

    /**
     * 获取MinioClient对象
     * @return MinioClient
     */
    private MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 批量删除文件
     * @param fileNames 文件名集合
     * @return 删除失败的文件信息集合
     */
    public List<DeleteError> deleteFiles(List<String> fileNames){
        return deleteFiles(bucketName, fileNames);
    }

    /**
     * 批量删除文件
     * @param bucketName 存储桶名称
     * @param fileNames 文件名集合
     * @return 删除失败的文件信息集合
     */
    @SneakyThrows
    public List<DeleteError> deleteFiles(String bucketName, List<String> fileNames){
        MinioClient minioClient = minioClient();

        List<DeleteObject> deleteObjects = fileNames.stream()
                .map(DeleteObject::new)
                .collect(Collectors.toList());

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(bucketName)
                .objects(deleteObjects)
                .build());

        List<DeleteError> errors = new ArrayList<>();
        for (Result<DeleteError> result : results) {
            errors.add(result.get());
        }
        return errors;
    }



    /**
     * 删除文件
     * @param fileName 文件名
     */
    public void deleteFile(String fileName){
        deleteFile(fileName, bucketName);
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @param bucketName 存储桶名称
     */
//    @SneakyThrows
    public void deleteFile(String fileName, String bucketName){
        MinioClient minioClient = minioClient();

        try{
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        }catch (Exception e){
            log.error("Error occurred: ", e);
            e.printStackTrace();
        }
    }


    /**
     * 文件上传
     * @param file 文件
     * @return 文件访问地址
     */
    public String uploadFile(MultipartFile file){
        return uploadFile(file, bucketName);
    }

    /**
     * 文件上传
     * @param file 文件
     * @param bucketName 存储桶名称
     * @return 文件访问地址
     */
    public String uploadFile(MultipartFile file, String bucketName){
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient = minioClient();

            // Make bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called bucketName.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket '" + bucketName + "' already exists.");
            }

            String fileName = generateFileName( file );
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                    .build()
            );
            System.out.println(
                    "'" + fileName + "' is successfully uploaded "
                            + " to bucket '" + bucketName + "'.");

            return getFileUrl(fileName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error occurred: ", e);
            log.error("Exception trace: ", e.getMessage());
        }
        return null;
    }

    /**
     * 获取文件访问地址
     * @param fileName 文件名
     * @return 文件访问地址
     */
    private String getFileUrl(String fileName){
        StringBuilder stringBuilder = new StringBuilder(endpoint + "/");
        stringBuilder
                .append(bucketName)
                .append("/")
                .append(fileName);
        return stringBuilder.toString();
    }

    /**
     * 从地址获取文件名
     * @param fileUrls 文件地址列表
     * @return 文件名列表
     */
    public List<String> getFileNameFromUrls(List<String> fileUrls){
        return fileUrls.stream()
                .map(url -> url.substring(url.lastIndexOf("/") + 1))
                .collect(Collectors.toList());
    }

    /**
     * 从地址获取文件名
     * @param fileUrl 文件地址
     * @return 文件名
     */
    public String getFileNameFromUrl(String fileUrl){
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }


    /**
     * 拼接文件名
     * @param fileName 文件名
     * @return 拼接后的文件名
     */
    private String expandFileName(String fileName){
        String id = BaseContext.getCurrentId().toString();
        return FILE_PREFIX + "/" + id + "/" + fileName;
    }

    /**
     * 生成文件名
     * @param file 文件
     * @return 文件名
     */
    private String generateFileName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String extension = null;
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return UUID.randomUUID() + extension;
    }
}

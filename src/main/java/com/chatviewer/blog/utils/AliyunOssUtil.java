package com.chatviewer.blog.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;


/**
 * @author ChatViewer
 */
@Slf4j
@Component
public class AliyunOssUtil {

    @Value("${my-conf.oss-end-point}")
    private String endPoint;
    @Value("${my-conf.ali-key}")
    private String accessKeyId;
    @Value("${my-conf.ali-secret}")
    private String accessSecretKey;
    @Value("${my-conf.oss-bucket}")
    private String bucketName;

    public String uploadFile(byte[] content, String objectName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build("https://" + endPoint, accessKeyId, accessSecretKey);

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content));

            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("上传文件成功");
            return "https://" + bucketName + '.' + endPoint + '/' + objectName;
        }
        catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        }
        catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return objectName;
    }
}
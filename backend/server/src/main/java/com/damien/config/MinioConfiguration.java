package com.damien.config;

// 由于需要调用MinioUtil类，所以不采取这个配置类来配置，而是用注入的方式
//@Configuration
//@Slf4j
public class MinioConfiguration {
//    @Bean
//    @ConditionalOnMissingBean
//    public MinioUtil minioUtil(MinioProperties minioProperties){
//        log.info("开始创建MinioUtil对象，访问MinIO的配置信息为：{}",minioProperties);
//        return new MinioUtil(
//                minioProperties.getEndpoint(),
//                minioProperties.getAccessKey(),
//                minioProperties.getSecretKey(),
//                minioProperties.getBucketName()
//        );
//    }
}

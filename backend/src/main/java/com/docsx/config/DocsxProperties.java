package com.docsx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "docsx")
public class DocsxProperties {

    private String dataDir = "./data";
    private String fileStore = "./data/files";
    private String onlyofficePath = "classpath:/onlyoffice/";
    private String onlyofficeCustomPath = "classpath:/onlyoffice-custom/";
    private String jwtSecret = "docsx-dev-secret";

    private TaskConfig task = new TaskConfig();
    private AsposeConfig aspose = new AsposeConfig();

    @Data
    public static class TaskConfig {
        private int threadPoolSize = 4;
        private int expireHours = 24;
        private int maxFileSizeMb = 100;
    }

    @Data
    public static class AsposeConfig {
        private String licensePath = "classpath:aspose-license.xml";
        private String fontDir = "./data/fonts";
    }
}

package com.docsx.engine.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Aspose 许可证加载器
 * 从 classpath 加载 aspose-license.xml
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class AsposeLicenseLoader {

    private static final Logger log = LoggerFactory.getLogger(AsposeLicenseLoader.class);
    private static boolean loaded = false;

    /**
     * 加载 Aspose.Words 许可证
     */
    public static synchronized void loadWordsLicense() {
        if (loaded) return;
        try (InputStream is = AsposeLicenseLoader.class.getClassLoader()
                .getResourceAsStream("aspose-license.xml")) {
            if (is != null) {
                com.aspose.words.License license = new com.aspose.words.License();
                license.setLicense(is);
                loaded = true;
                log.info("Aspose.Words license loaded successfully");
            } else {
                log.warn("aspose-license.xml not found in classpath, running in evaluation mode");
            }
        } catch (Exception e) {
            log.error("Failed to load Aspose.Words license", e);
        }
    }
}

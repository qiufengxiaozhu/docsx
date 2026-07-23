package com.docsx.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docsx.config.DocsxProperties;
import com.docsx.model.dto.R;
import com.docsx.model.entity.Font;
import com.docsx.mapper.FontMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * 管理后台 - 字体管理接口
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/api/fonts")
public class AdminFontController {

    private static final Logger log = LoggerFactory.getLogger(AdminFontController.class);

    private final FontMapper fontMapper;
    private final DocsxProperties properties;

    public AdminFontController(FontMapper fontMapper, DocsxProperties properties) {
        this.fontMapper = fontMapper;
        this.properties = properties;
    }

    /**
     * 分页查询字体
     */
    @GetMapping
    public R<Page<Font>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        LambdaQueryWrapper<Font> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Font::getCreatedAt);
        return R.ok(fontMapper.selectPage(new Page<>(page, size), wrapper));
    }

    /**
     * 上传字体文件
     */
    @PostMapping("/upload")
    public R<Font> upload(@RequestParam("file") MultipartFile file,
                          @RequestParam(required = false) String fontName,
                          @RequestParam(required = false) String fontFamily) {
        if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.matches(".*\\.(ttf|otf|woff|woff2|ttc)$")) {
            return R.fail("仅支持 ttf/otf/woff/woff2/ttc 字体格式");
        }

        try {
            Path fontDir = Paths.get(properties.getAspose().getFontDir());
            Files.createDirectories(fontDir);

            Path target = fontDir.resolve(originalName);
            file.transferTo(target.toFile());

            Font font = new Font();
            font.setFontName(fontName != null ? fontName : originalName.replaceAll("\\.[^.]+$", ""));
            font.setFontFamily(fontFamily);
            font.setFilePath(target.toString());
            font.setFileSize(file.getSize());
            font.setStatus(1);
            font.setCreatedAt(LocalDateTime.now());
            font.setUpdatedAt(LocalDateTime.now());
            fontMapper.insert(font);

            log.info("Font uploaded: {} ({})", font.getFontName(), originalName);
            return R.ok(font);
        } catch (IOException e) {
            log.error("Font upload failed", e);
            return R.fail("字体上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除字体
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        Font font = fontMapper.selectById(id);
        if (font != null && font.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(font.getFilePath()));
            } catch (IOException e) {
                log.warn("Failed to delete font file: {}", font.getFilePath());
            }
        }
        fontMapper.deleteById(id);
        return R.ok("已删除");
    }
}

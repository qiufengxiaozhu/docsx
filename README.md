# Docsx

文档比对服务 — 支持 Word/PDF/Excel 在线比对，三方应用 iframe 集成。

## 功能特性

- **Word 比对**：基于 Aspose.Words 提取 + NW 段落对齐 + DMP 词级 diff，精确到字符级差异
- **PDF 比对**（规划中）：Aspose.PDF 提取，复用段落 diff 引擎
- **Excel 比对**（规划中）：Aspose.Cells 提取，Sheet/Cell 级对齐
- **在线预览**：基于 OnlyOffice Personal WASM，纯浏览器端运行
- **三方集成**：HMAC-MD5 签名认证，通过 API 创建比对任务，返回 iframe URL
- **异步任务池**：大文档异步处理，轮询状态
- **管理后台**：应用管理、任务监控、字体管理、系统设置

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | LTS |
| Spring Boot | 3.4.4 | 后端框架 |
| MyBatis Plus | 3.5.9 | ORM |
| SQLite / MySQL | - | 双数据库支持 |
| Aspose.Words | 24.3 | 文档内容提取 |
| diff-match-patch | 1.2 | 字符级 diff |
| jieba-analysis | 1.0.2 | 中文分词 |
| Vue 3 + Element Plus | 3.5+ | 管理后台 |
| OnlyOffice Personal | 9.3 | WASM 文档预览 |
| Docker | - | 容器化部署 |

## 快速开始

### 开发环境

```bash
# 后端
cd backend
mvn spring-boot:run

# 前端管理后台
cd frontend
npm install
npm run dev
```

### Docker 部署

```bash
docker-compose up -d
```

访问：
- 管理后台：http://localhost:8080/admin/
- API 文档：http://localhost:8080/actuator
- 默认账号：`admin` / `admin123`

## 三方集成

### 认证方式

请求头携带 HMAC-MD5 签名：

```
X-App-Id: your-app-id
X-Timestamp: 1700000000
X-Nonce: random-string
X-Sign: MD5(app_secret + "@@" + timestamp + "@@" + nonce)
```

### 创建比对任务

```bash
curl -X POST http://localhost:8080/api/compare/create \
  -H "X-App-Id: your-app-id" \
  -H "X-Timestamp: $(date +%s)" \
  -H "X-Nonce: abc123" \
  -H "X-Sign: computed-sign" \
  -F "fileA=@document_v1.docx" \
  -F "fileB=@document_v2.docx"
```

### 查询状态

```bash
curl http://localhost:8080/api/compare/status/{taskId}
```

### 嵌入 iframe

```html
<iframe src="http://docsx-host:8080/view/{sessionId}"
        width="100%" height="100%" frameborder="0">
</iframe>
```

## 比对算法

基于 Needleman-Wunsch 全局序列对齐 + diff-match-patch 字符级 diff：

1. **Aspose 提取**：将文档结构化为段落/表格块列表
2. **NW 段落对齐**：O(m×n) DP + LCS 相似度，产出 SAME/MODIFY/ADD/DELETE
3. **表格分流**：匈牙利算法匹配表格对，单元格内复用段落 diff
4. **segmentDiff**：MODIFY 段落对用 jieba 分词 + DMP 做词级精细 diff
5. **前端渲染**：差异面板分组展示 + 文档内高亮定位

## 项目结构

```
docsx/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 管理后台
├── onlyoffice/       # OnlyOffice WASM 静态资源
├── onlyoffice-custom/# OnlyOffice 定制补丁
├── scripts/          # 部署脚本
├── docs/             # 架构文档
├── Dockerfile        # 多阶段构建
├── docker-compose.yml
└── README.md
```

## License

MIT

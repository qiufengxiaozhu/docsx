# Docsx

文档比对服务 — 支持 Word/PDF/Excel 在线比对，三方应用 iframe 集成。

## 功能特性

- **Word 比对**：基于 Aspose.Words 提取 + NW 段落对齐 + DMP 词级 diff，精确到字符级差异
- **PDF 比对**（规划中）：Aspose.PDF 提取，复用段落 diff 引擎
- **Excel 比对**（规划中）：Aspose.Cells 提取，Sheet/Cell 级对齐
- **在线预览**：基于 OnlyOffice Personal 9.3 WASM，纯浏览器端运行
- **三方集成**：HMAC-MD5 签名认证，通过 API 创建比对任务，返回 iframe URL
- **异步任务池**：大文档异步处理，轮询状态
- **管理后台**：应用管理、任务监控、字体管理、系统设置

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | LTS |
| Spring Boot | 3.4.4 | 后端框架 |
| MyBatis Plus | 3.5.9 | ORM |
| SQLite / MySQL | - | 双数据库支持 |
| Aspose.Words | 24.3 | 文档内容提取 |
| diff-match-patch | 1.2 | 字符级 diff |
| jieba-analysis | 1.0.2 | 中文分词 |
| Vue 3 + Element Plus | 3.5+ | 管理后台 |
| OnlyOffice Personal | 9.3.0.136 | WASM 文档预览 |
| Docker | - | 容器化部署 |

## 快速开始

### 开发环境

**后端启动**（需要 Java 17+、Maven 3.9+）：

```bash
cd backend
mvn compile
mvn spring-boot:run
```

或者在 IDEA 中直接运行 `DocsxApplication.main()`。

启动后访问：
- 后端 API：http://localhost:8999
- 健康检查：http://localhost:8999/actuator/health
- 比对页面：http://localhost:8999/view/{sessionId}

**前端管理后台**（开发模式，需 Node.js 18+）：

```bash
cd frontend
npm install
npm run dev
```

启动后访问 http://localhost:3000，会自动代理到后端 8999 端口。

**默认管理员账号**：`admin` / `admin123`

### 生产构建

```bash
# 构建前端
cd frontend && npm run build

# 构建后端（含前端产物）
cd backend && mvn package -DskipTests
java -jar target/docsx.jar
```

### Docker 部署

```bash
docker-compose up -d
```

## 三方系统集成

### 认证方式

请求头携带 HMAC-MD5 签名：

```
X-App-Key: your-app-key
X-Timestamp: 1700000000
X-Nonce: random-string
X-Sign: MD5(app_secret + "@@" + timestamp + "@@" + nonce)
```

### 创建比对任务

```bash
curl -X POST http://localhost:8999/api/compare/create \
  -H "X-App-Key: your-app-key" \
  -H "X-Timestamp: $(date +%s)" \
  -H "X-Nonce: abc123" \
  -H "X-Sign: computed-sign" \
  -F "file1=@document_v1.docx" \
  -F "file2=@document_v2.docx"
```

返回：
```json
{"code": 200, "data": {"sessionId": "xxx", "viewUrl": "/view/xxx"}}
```

### 查询状态

```bash
curl http://localhost:8999/api/compare/status/{sessionId}
```

### 嵌入 iframe

```html
<iframe src="http://docsx-host:8999/view/{sessionId}"
        width="100%" height="100%" frameborder="0">
</iframe>
```

## 比对算法

基于 Needleman-Wunsch 全局序列对齐 + diff-match-patch 字符级 diff（移植自 luoshu）：

1. **Aspose 提取**：将文档结构化为段落/表格块列表
2. **NW 段落对齐**：O(m×n) DP + LCS 相似度，产出 SAME/MODIFY/ADD/DELETE
3. **表格分流**：Dice 相似度行匹配，单元格内复用段落 diff
4. **segmentDiff**：MODIFY 段落对用 jieba 分词 + DMP 做词级精细 diff
5. **前端渲染**：差异面板合并同类项 + 字符级 diff 高亮 + 文档内高亮定位

## 项目结构

```
docsx/
├── backend/          # Spring Boot 后端
│   ├── src/main/java/com/docsx/
│   │   ├── config/       # 配置类
│   │   ├── controller/   # API 控制器
│   │   ├── engine/       # 比对引擎核心
│   │   │   ├── diff/     # NW、SegmentDiff、TableAligner
│   │   │   ├── extractor/# Aspose 内容提取
│   │   │   └── model/    # 引擎数据模型
│   │   ├── mapper/       # MyBatis Mapper
│   │   ├── model/        # 实体/DTO
│   │   ├── security/     # 认证过滤器
│   │   └── service/      # 业务服务
│   └── src/main/resources/
│       ├── templates/    # Thymeleaf 模板（比对页面）
│       └── schema-*.sql  # DDL
├── frontend/         # Vue 3 管理后台
├── onlyoffice/       # OnlyOffice Personal 9.3 WASM 资源
├── docs/             # 架构文档
├── Dockerfile        # 多阶段构建
├── docker-compose.yml
└── README.md
```

## License

MIT

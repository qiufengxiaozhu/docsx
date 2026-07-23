# Docsx - 多阶段构建

# ===== 阶段1: 前端构建 =====
FROM node:20-alpine AS frontend-build
WORKDIR /build
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com
COPY frontend/ ./
RUN npm run build

# ===== 阶段2: 后端构建 =====
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /build
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B 2>/dev/null || true
COPY backend/ ./
RUN mvn package -DskipTests

# ===== 阶段3: 运行时 =====
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="qiufengxiaozhu"
LABEL description="Docsx - 文档比对服务"

WORKDIR /app

# 复制后端 JAR
COPY --from=backend-build /build/target/docsx.jar ./docsx.jar

# 复制前端产物到静态资源目录
COPY --from=frontend-build /build/dist/ ./static/admin/

# 复制 OnlyOffice WASM 静态资源（需提前准备）
COPY onlyoffice/ ./onlyoffice/

# 复制 OnlyOffice 定制化资源
COPY onlyoffice-custom/ ./onlyoffice-custom/

# 安装字体渲染依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig libfreetype6 fonts-noto-cjk curl \
    && rm -rf /var/lib/apt/lists/*

# 数据目录
RUN mkdir -p /app/data/files /app/data/fonts /app/logs

# 暴露端口
EXPOSE 8080

# 数据卷
VOLUME ["/app/data", "/app/logs"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动（JVM 参数可通过 JAVA_OPTS 环境变量覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar docsx.jar \
    --docsx.data-dir=/app/data \
    --docsx.file-store=/app/data/files \
    --docsx.onlyoffice-path=file:/app/onlyoffice/ \
    --docsx.onlyoffice-custom-path=file:/app/onlyoffice-custom/ \
    --docsx.aspose.font-dir=/app/data/fonts"]

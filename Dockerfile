# ===== 构建阶段 =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 先复制 pom.xml，利用 Docker 层缓存加速依赖下载
COPY pom.xml .
COPY settings.xml .
RUN mvn -s settings.xml dependency:go-offline -B

# 复制源码并构建（跳过测试以加速镜像构建，测试在 CI 中单独运行）
COPY src ./src
RUN mvn -s settings.xml clean package -DskipTests -B

# ===== 运行阶段 =====
FROM eclipse-temurin:17-jre-alpine

# 安装时区数据和基础工具
RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S lifehub && adduser -S lifehub -G lifehub
USER lifehub

# 从构建阶段复制 fat jar
COPY --from=builder --chown=lifehub:lifehub /app/target/*.jar app.jar

# 暴露应用端口
EXPOSE 9000

# JVM 优化参数（容器感知内存、GC 日志）
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

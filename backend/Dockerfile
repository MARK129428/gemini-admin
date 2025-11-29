# ==================================================================
# 阶段 1: Build 阶段
# ==================================================================
FROM maven:3.8.8-amazoncorretto-17 AS build

WORKDIR /app

# 先复制 pom.xml
COPY pom.xml .

# 不使用 go-offline，直接构建
COPY src ./src
RUN mvn clean package -DskipTests

# ==================================================================
# 阶段 2: 运行阶段
# ==================================================================
FROM amazoncorretto:17.0.17

WORKDIR /app

# 复制构建阶段生成的 Jar 文件
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

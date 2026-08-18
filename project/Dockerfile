# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# ننسخ ملف pom.xml أول عشان نستفيد من كاش Docker للـ dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# ننسخ باقي المشروع ونبنيه
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# مستخدم غير root لتشغيل التطبيق (أفضل من ناحية الأمان)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

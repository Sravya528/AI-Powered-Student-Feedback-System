FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 9090

CMD ["java", "-jar", "target/ai-power3d-feedback-1.0.3.jar"]
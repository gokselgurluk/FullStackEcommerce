# Temel imajı belirleyin
FROM openjdk:17-jdk-slim

# Uygulama dosyasını kopyalayın
COPY target/eticaretAPI-0.0.1-SNAPSHOT.jar /app/eticaretAPI.jar

# Çalışma dizinini belirleyin
WORKDIR /app

EXPOSE 8080

# Uygulamayı çalıştırın
CMD ["java", "-jar", "eticaretAPI.jar"]

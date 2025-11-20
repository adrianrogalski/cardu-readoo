FROM eclipse-temurin:25-jdk

WORKDIR /app

# Skopiuj pliki projektu
COPY pom.xml .
COPY src ./src

# Zainstaluj Mavena i zbuduj JAR
RUN apt-get update && \
    apt-get install -y maven && \
    mvn -B clean package -DskipTests && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Spring Boot domyślnie wystawia się na 8080
EXPOSE 8080

# Uruchom JAR (jakikolwiek *.jar w target/)
CMD ["sh", "-c", "java -jar target/*.jar"]
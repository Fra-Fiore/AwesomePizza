# 1. Usa una immagine base di Java per costruire ed eseguire l'app
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# 2. Imposta la directory di lavoro
WORKDIR /app

# 3. Copia il file Maven (pom.xml) e le dipendenze per build efficiente
COPY pom.xml .
COPY lombok.config .

# 4. Copia il codice sorgente
COPY src ./src

# 5. Compila l'applicazione usando Maven
RUN mvn dependency:go-offline -B && mvn clean package -DskipTests

# 6. Runtime: usa un'immagine più leggera con JRE
FROM eclipse-temurin:21-jre

# 7. Imposta la directory di lavoro
WORKDIR /app

# 8. Copia il JAR generato dall'immagine builder
COPY --from=builder /app/target/awesomepizza-*.jar app.jar

# 9. Configura la porta su cui gira l'app
EXPOSE 8080

# 10. Comando di avvio dell'applicazione Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

# Usar uma imagem base oficial do Java 21 (mesma versão do seu projeto)
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de build do Maven para o container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Baixa as dependências. Isso otimiza o cache em builds futuros.
RUN ./mvnw dependency:go-offline

# Copia o resto do código fonte da sua aplicação
COPY src ./src

# Executa o build do Maven para gerar o arquivo .jar
RUN ./mvnw package -DskipTests

# Expõe a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando final para iniciar a aplicação quando o container rodar
CMD ["java", "-jar", "target/eventHub-0.0.1-SNAPSHOT.jar"]